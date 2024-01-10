from pydantic import BaseModel, EmailStr, validator
from fastapi import HTTPException

class NewUserForm(BaseModel):
    id: str
    password: str
    name: str
    email: EmailStr

@validator('id', 'password', 'name', 'email')
def check_empty(cls, value):
    if not value or not value.isspace():
        raise HTTPException(status_code=422, detail="모든 항목을 입력해주세요.")
    return value

@validator('password')
def check_empty(cls, value):
    if len(value) < 8:
        raise HTTPException(status_code=422, detail="비밀번호는 8자리 이상이어야 합니다.")

    if (not any(char.isdigit() for char in value)) or (not any(char.isalpha() for char in value)):
        raise HTTPException(status_code=422, detail="비밀번호는 영문과 숫자 모두를 포함해야 합니다.")
    
    return value