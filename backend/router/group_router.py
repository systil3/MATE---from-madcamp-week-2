from fastapi import APIRouter
from database import SessionLocal
from typing import List
from db.models.users import User
from db.models.token import TokenBase
from fastapi.security import OAuth2PasswordRequestForm, OAuth2PasswordBearer
from db.crud.crud_user import *
from db.crud.crud_group import *
from db.crud.crud_appointment import *
from starlette import status
from datetime import datetime, timedelta
from authentication import *
from jose import jwt 

router = APIRouter(
    prefix="/group",
)

def get_db():
    db = SessionLocal()  
    try:
        yield db
    finally:
        db.close()

@router.get("/info/", response_model=GroupBase)
def read_group_api(
    group_id: str, 
    db: Session = Depends(get_db)
):
    return get_group(db, group_id)

@router.get("/users", response_model=List[UserBase])
def show_all_users_from_group_api(
    group_id: str, 
    db: Session = Depends(get_db)
):
    return show_all_users_from_group(db, group_id)

################## 그룹의 약속 관리 ########################

@router.post("/appointment/create", response_model=CompletedBooleanBase)
def create_appointment_from_group_api(
    appointment: AppointmentBase,
    current_user: UserBase = Depends(get_current_user),
    db: Session = Depends(get_db)
):  
    return create_appointment_from_group(db=db, appointment=appointment, current_user=current_user)

@router.delete("/appointment/delete", response_model=CompletedBooleanBase)
def delete_appointment_from_group_api(
    appointment_id = str,
    current_user: UserBase = Depends(get_current_user),
    db: Session = Depends(get_db)
):  
    return delete_appointment_from_group(db=db, appointment_id=appointment_id, current_user=current_user)
