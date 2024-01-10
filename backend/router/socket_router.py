from fastapi import APIRouter
from database import SessionLocal
from db.models.users import User
from db.models.token import TokenBase
from fastapi.security import OAuth2PasswordRequestForm, OAuth2PasswordBearer
from db.crud.crud_user import *
from starlette import status
from datetime import datetime, timedelta
from jose import jwt 
from app_socket.websocket import *

router = APIRouter(
    prefix="/socket",
)

@router.websocket("/get")
async def websocket_connect(websocket: WebSocket):
    await websocket_endpoint(websocket)