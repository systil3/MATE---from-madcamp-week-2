from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from db.base import *
from pydantic import BaseModel, EmailStr, validator
import re

class UserBase(BaseModel):
    id: str
    password: str
    name: str
    email: str

    '''@validator('password')
    def check_password(cls, value):
        if len(value) < 8:
            raise HTTPException(status_code=422, detail="비밀번호는 8자리 이상이어야 합니다.")

        if (not any(char.isdigit() for char in value)) or (not any(char.isalpha() for char in value)):
            raise HTTPException(status_code=422, detail="비밀번호는 영문과 숫자 모두를 포함해야 합니다.")
        
        return value
    
    @validator("email")
    def validate_email(cls, value):
        email_regex = r'^\S+@\S+\.\S+$'
        if not re.match(email_regex, value):
            raise ValueError("올바른 이메일 형식이 아닙니다.")
        return value'''
    
class ExistsBooleanBase(BaseModel):
    exists: bool

def to_UserBase(user):
    return UserBase(
        id=user.id,
        password=user.password,
        name=user.name, 
        email=user.email)

def create_user(db: Session, user: UserBase):
    try:
        db_user = User(id=user.id, password=user.password, name=user.name, email=user.email)
        db.add(db_user)
        db.commit()
        db.refresh(db_user)
        return user
    except Exception as e:
        # Log the exception or handle it appropriately
        print(e)
        raise HTTPException(status_code=500, detail="User not created")

def get_user(db: Session, user_id: str):
    user = db.query(User).filter(User.id == user_id).first()
    if user is None:
        raise HTTPException(status_code=404, detail="User not found")
    return to_UserBase(user)

def find_if_user_exists(db: Session, id: str):
    user = db.query(User).filter(User.id == id).first()    
    if user is not None:
        return ExistsBooleanBase(exists=True)
    else:
        return ExistsBooleanBase(exists=False)
    
def get_user_by_email(db: Session, email: str):
    user = db.query(User).filter(User.email == email).first()
    return to_UserBase(user)

def get_users(db: Session, skip: int = 0, limit: int = 10):
    user = db.query(User).offset(skip).limit(limit).all()
    return to_UserBase(user)

def update_user(db: Session, user: UserBase):
    user_id = user.id
    user_found = db.query(User).filter(User.id == user_id).first()
    if user_found is not None:
        user_found.name = user.name
        user_found.email = user.email
        user_found.password = user.password
        db.commit()
        db.refresh(user_found)
        return to_UserBase(user_found)
    else:
        raise HTTPException(status_code=404, detail="User not found")

def delete_user(db: Session, user_id: str):
    user = db.query(User).filter(User.id == user_id).first()
    if user is not None:
        db.delete(user)
        user_group.delete().where(
                (user_group.c.user_id == user_id)
            )
        db.commit()
        return to_UserBase(user)
    else:
        raise HTTPException(status_code=404, detail="User not found")
