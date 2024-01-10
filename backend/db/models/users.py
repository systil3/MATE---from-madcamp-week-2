from sqlalchemy import create_engine, Column, Integer, String, ForeignKey, DateTime, Text
from sqlalchemy.orm import declarative_base, relationship
from datetime import datetime
from pydantic import BaseModel
from ..base_class import Base
from db.models.relationship import *

class User(Base):
    __tablename__ = 'users'

    id = Column(String(50), primary_key=True, nullable=False)
    password = Column(String(100), nullable=False)
    name = Column(String(100), nullable=False)
    email = Column(String(100), nullable=False)
    
    groups = relationship("Group", secondary=user_group, back_populates="users")