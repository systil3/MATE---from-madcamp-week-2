from fastapi import FastAPI, Depends, Path, HTTPException, APIRouter
from sqlalchemy import MetaData
from sqlalchemy.orm import Session
from pydantic import BaseModel
from db.session import db_engine, SessionLocal
from db.base import Base
from db.models.relationship import *
from db.crud.crud_user import *
from db.crud.crud_appointment import *
from db.crud.crud_group import *
from db.crud.crud_review import *
from router import user_router
from router import group_router
from router import socket_router

def create_tables():
    db = SessionLocal()
    Base.metadata.create_all(bind=db_engine)
    db.commit()
    
# Function to get the database session
def get_db():
    db = SessionLocal()  
    try:
        yield db
    finally:
        db.close()
        
app = FastAPI()
db = SessionLocal()

app.include_router(user_router.router)
app.include_router(group_router.router)
app.include_router(socket_router.router)

@app.get("/")
async def root():
    create_tables()
    return {"message": "Hellow world"}
