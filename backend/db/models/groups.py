from sqlalchemy import create_engine, Column, Integer, String, ForeignKey, DateTime, Text
from sqlalchemy.orm import declarative_base, relationship
from datetime import datetime
from pydantic import BaseModel
from ..base_class import Base
from db.models.relationship import *
unique_code_length = 15

class Group(Base):
    __tablename__ = 'allgroups'

    id = Column(String(unique_code_length), primary_key=True)
    password = Column(String(100), nullable=False)
    group_name = Column(String(100), nullable=False)
    host_id = Column(String(100), ForeignKey('users.id'))
    users = relationship("User", secondary=user_group, back_populates="groups")
    