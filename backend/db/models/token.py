from sqlalchemy import create_engine, Column, Integer, String, ForeignKey, DateTime, Text
from sqlalchemy.orm import declarative_base, relationship
from datetime import datetime
from pydantic import BaseModel
from ..base_class import Base
from db.models.relationship import *

class TokenBase(BaseModel):
    access_token: str
    token_type: str
    name: str

'''class Token(Base):
    __tablename__ = 'tokens'

    access_token = Column(String(100), primary_key=True, null=False)
    user = Column(String(100), ForeignKey('users.id'), null=False)
    expiration_date = Column(DateTime, null=False)'''