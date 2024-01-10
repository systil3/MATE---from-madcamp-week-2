from fastapi import APIRouter, Response
from database import SessionLocal
from typing import List
from db.models.users import User
from db.models.token import TokenBase
from fastapi.security import OAuth2PasswordRequestForm, OAuth2PasswordBearer
from db.crud.crud_user import *
from db.crud.crud_appointment import *
from db.crud.crud_group import *
from starlette import status
from datetime import datetime, timedelta
from jose import jwt 
from authentication import *
from fastapi import UploadFile, File

router = APIRouter(
    prefix="/users",
)


###################### 유저 그 자체를 관리 ##########################

################# 인증 관련 #####################

@router.post("/login", response_model=TokenBase)
async def login(
    response: Response,
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: Session = Depends(get_db),
):
    user_id = form_data.username
    user_found = db.query(User).filter(User.id == user_id).first()

    if (user_found is None) or (user_found.password != form_data.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="유저 정보가 맞지 않습니다.",
            headers={"WWW-Authenticate": "Bearer"},
        )
        
    # Create JWT token
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_jwt_token(
        data={"sub": user_id}, expires_delta=access_token_expires
    )
    
    return {"access_token": access_token, 
            "token_type": "bearer", 
            "name" : user_found.name}


################################################ 

@router.post("/create", response_model=UserBase)
def create_user_api(
    user: UserBase,
    db: Session = Depends(get_db)
):  
    return create_user(db=db, user=user)

@router.get("/info/{id}", response_model=UserBase)
def read_user_api(
    id: str, 
    db: Session = Depends(get_db)
):
    return get_user(db, id)

@router.get("/find/{id}", response_model=ExistsBooleanBase)
def find_user_api(
    id: str, 
    db: Session = Depends(get_db)
): 
    return find_if_user_exists(db, id)

@router.put("/update/{id}", response_model=UserBase)
def update_user_api(
    user: UserBase,
    db: Session = Depends(get_db)
):
    return update_user(db=db, user=user)
    
@router.delete("/delete/{id}", response_model=UserBase)
def delete_user_api(id: str, db: Session = Depends(get_db)):
    return delete_user(db, id)

########################## 유저의 그룹 관리 ###############################

@router.post("/group/create", response_model=GroupBase)
def create_group_from_user_api(
    group: GroupBase,
    current_user: UserBase = Depends(get_current_user),
    db: Session = Depends(get_db)
):  
    return create_group(db=db, group=group, current_user=current_user)

@router.post("/group/join", response_model=CompletedBooleanBase)
def join_group_from_user_api(
    group_id = str,
    group_password = str,
    current_user: UserBase = Depends(get_current_user),
    db: Session = Depends(get_db)
):  
    return add_user_to_group(db=db, group_id=group_id, password=group_password, current_user=current_user)

@router.get("/group/show",   response_model=List[GroupBase])
def show_all_groups_from_user_api(
    current_user: UserBase = Depends(get_current_user),
    db: Session = Depends(get_db),
):  
    return show_all_groups_from_user(db=db, user_id=current_user.id)

@router.get("/appointment/show",   response_model=List[AppointmentBase])
def show_all_appointments_from_user_api(
    current_user: UserBase = Depends(get_current_user),
    db: Session = Depends(get_db),
):  
    return show_all_appointments_from_user(db=db, user_id=current_user.id)

@router.delete("/group/dropout/", response_model=CompletedBooleanBase)
def dropout_of_group_api(
    group_id = str,
    group_password = str,
    current_user: UserBase = Depends(get_current_user),
    db: Session = Depends(get_db)
):  
    return dropout_of_group(db=db, group_id=group_id, group_password=group_password, current_user=current_user)

@router.delete("/group/delete", response_model=CompletedBooleanBase)
def DELETE_group_from_user_api(
    group_id = str,
    group_password = str,
    current_user: UserBase = Depends(get_current_user),
    db: Session = Depends(get_db)
):  
    return delete_group_from_user(db=db, group_id=group_id, password=group_password, current_user=current_user)

@router.get("/token")
async def read_items(token: str = Depends(oauth2_scheme)):
    return {"token": token}

########################## 토큰 ###############################
 
@router.get("/token")
async def read_items(token: str = Depends(oauth2_scheme)):
    return {"token": token}