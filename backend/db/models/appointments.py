from sqlalchemy import create_engine, Column, Integer, String, ForeignKey, DateTime, Text
from sqlalchemy.orm import declarative_base, relationship
from datetime import datetime
from pydantic import BaseModel
from ..base_class import Base
from db.models.relationship import *

class Appointment(Base):
    __tablename__ = 'appointments'

    id = Column(String(20), primary_key=True, nullable=False)
    time = Column(DateTime, default=datetime.utcnow, nullable=False)
    title = Column(String(100))
    content = Column(Text)
    location = Column(String(100))
    group_id = Column(String(20), ForeignKey('allgroups.id'), nullable=False)
