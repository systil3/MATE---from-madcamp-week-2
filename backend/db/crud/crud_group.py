from fastapi import FastAPI, Depends, HTTPException, UploadFile, File
from sqlalchemy.orm import Session
from sqlalchemy import and_
from db.models.models import *
from pydantic import BaseModel
from .crud_user import *
from typing import List
import uuid
import base64
import codecs

class GroupBase(BaseModel):
    id: str
    password: str
    group_name: str
    host_id: str
    
class CompletedBooleanBase(BaseModel):
    completed: bool
    msg: str
    
def to_GroupBase(group):
    return GroupBase(
        id= group.id,
        password= group.password,
        group_name= group.group_name,
        host_id= group.host_id,
    )

def create_group(db: Session, group: GroupBase, current_user: UserBase):
    def generate_unique_code(length=12):
        return base64.urlsafe_b64encode(
            codecs.encode(uuid.uuid4().bytes, "base64").rstrip()
        ).decode()[:length]
    
    group = Group(id=generate_unique_code(), password=group.password, 
                  group_name=group.group_name, host_id=current_user.id)
    db.add(group)
    print(group.id)
    db.commit()
    db.refresh(group)
    
    db.execute(user_group.insert().values(
        user_id=current_user.id, group_id=group.id))
    db.commit()
    
    return to_GroupBase(group)

def add_user_to_group(db: Session, group_id: str, password: str, current_user: UserBase):
    group = db.query(Group).filter(Group.id == group_id).first()
    if group:
        if group.password == password:
            db.execute(user_group.insert().values(
                user_id=current_user.id, group_id=group_id))
            db.commit()
            return CompletedBooleanBase(
                completed=True, msg=f"{current_user.name} Joined Group {group.group_name}")
        else:
            return CompletedBooleanBase(
                completed=False, msg="그룹 비밀번호가 일치하지 않습니다.")
    else:
        return CompletedBooleanBase(
                completed=False, msg="그룹이 존재하지 않습니다.")

def show_all_groups_from_user(db: Session, user_id: str):
    #user = db.query(User).filter(User.id == user_id).first()
    print(user_id)
    user_groups = (
        db.query(Group)
        .join(user_group)
        .join(User)
        .filter(User.id == user_id)
        .all()
    )
    return list(user_groups)

def show_all_users_from_group(db: Session, group_id: str):
    print(group_id)
    group_users = (
        db.query(User)
        .join(user_group)
        .join(Group)
        .filter(Group.id == group_id)
        .all()
    )
    return list(group_users)    
    
def dropout_of_group(db: Session, group_id: str, group_password: str, current_user: UserBase):
    group = db.query(Group).filter(Group.id == group_id).first()
    
    if group:
        group_name = group.group_name
        user_id = current_user.id
        if group.host_id == current_user.id:
            return CompletedBooleanBase(
                completed=False, msg="그룹 호스트이기 때문에 그룹을 탈퇴할 수 없습니다")
        if group.password != group_password:
            return CompletedBooleanBase(
                completed=False, msg="그룹 비밀번호가 일치하지 않습니다")
        db.execute(
            user_group.delete().where(
                and_(
                    user_group.c.user_id == user_id,
                    user_group.c.group_id == group_id
                )
            )
        )
        db.commit()
        return CompletedBooleanBase(
                completed=True, msg=f"user {current_user.id} dropped out of group {group_name}")
    else:
        return CompletedBooleanBase(
                completed=False, msg="그룹이 존재하지 않습니다.")

def get_group(db: Session, group_id: str):
    group = db.query(Group).filter(Group.id == group_id).first()
    if group:
        return to_GroupBase(group)
    else:
        raise HTTPException(status_code=404, detail="Group not found") 

def delete_group_from_user(db: Session, group_id: str, password: str, current_user: UserBase):
    group = db.query(Group).filter(Group.id == group_id).first()
    if group:
        group_name = group.group_name
        if group.host_id != current_user.id:
            return CompletedBooleanBase(
                completed=False, msg="그룹 호스트가 아니기 때문에 그룹을 삭제할 수 없습니다.")
        elif group.password != password:
            return CompletedBooleanBase(
                completed=False, msg="그룹 비밀번호가 일치하지 않습니다.")
        else:
            db.delete(group)
            db.commit()
            #db.refresh(group) : error
            return CompletedBooleanBase(
                completed=True, msg=f"group {group_name} deleted")

    else:
        return CompletedBooleanBase(
                completed=False, msg="그룹이 존재하지 않습니다.")


