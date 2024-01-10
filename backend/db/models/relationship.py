from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from models import *
from pydantic import BaseModel
from sqlalchemy import Table, ForeignKey, Column, Integer, String
from sqlalchemy.orm import relationship
from db.base import Base

user_group = Table(
    'user_group',
    Base.metadata,
    Column('user_id', String(50), ForeignKey('users.id')),
    Column('group_id', String(20), ForeignKey('allgroups.id'))
) 


