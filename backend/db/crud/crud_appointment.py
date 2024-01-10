from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from db.models.models import *
from pydantic import BaseModel
from datetime import datetime
from .crud_user import *
import uuid
import base64
import codecs

class CompletedBooleanBase(BaseModel):
    completed: bool
    msg: str

class AppointmentBase(BaseModel):
    id : str
    time : datetime
    title : str
    content : str
    location : str
    group_id : str
    
def to_AppointmentBase(appointment):
    return AppointmentBase(
        id = appointment.id,
        time = appointment.time,
        title = appointment.title,
        content = appointment.content,
        location = appointment.location,
        group_id = appointment.group_id
    )

def create_appointment_from_group(db: Session, appointment: AppointmentBase, 
                                   current_user: UserBase):
    def generate_unique_code(length=12):
        return base64.urlsafe_b64encode(
            codecs.encode(uuid.uuid4().bytes, "base64").rstrip()
        ).decode()[:length]
    
    group = db.query(Group).filter(Group.id == appointment.group_id).first()
    if not group:
        raise HTTPException(status_code=404, detail="Group not found")
    if group.host_id != current_user.id:
        return CompletedBooleanBase(completed=False,
                                    msg="그룹 호스트가 아니기 때문에 약속을 생성할 수 없습니다.")
    
    appointment = Appointment(
        id=generate_unique_code(length=7),
        time=appointment.time,
        title=appointment.title, 
        content=appointment.content, 
        location=appointment.location,
        group_id=appointment.group_id)
    db.add(appointment)
    db.commit()
    db.refresh(appointment)
    return CompletedBooleanBase(completed=True,
            msg=f"Appointment {appointment.title} created by group {group.group_name}")

def show_all_appointments_from_user(db: Session, user_id: str):
    uga_join_condition = user_group.c.group_id == Appointment.group_id
    user_appointments = db.query(user_group, Appointment) \
        .join(Appointment, uga_join_condition)  \
        .filter(user_group.c.user_id == user_id)    \
        .all()
    print(user_appointments)
    return list(map(lambda x : to_AppointmentBase(x[2]), user_appointments))

def delete_appointment_from_group(db: Session, appointment_id: str, current_user: UserBase):
    appointment = db.query(Appointment).filter(Appointment.id == appointment_id).first()
    appointment_name = appointment.title
    if not appointment:
        raise HTTPException(status_code=404, detail="Appointment not found")

    group = db.query(Group).filter(Group.id == appointment.group_id).first()
    if not group:
        raise HTTPException(status_code=404, detail="Group of appointment not found")
    if group.host_id != current_user.id:
        return CompletedBooleanBase(completed=False,
                                    msg="그룹 호스트가 아니기 때문에 약속을 삭제할 수 없습니다.")
    
    db.delete(appointment)
    db.commit()
    return CompletedBooleanBase(completed=True,
            msg=f"Appointment {appointment_name} of group {group.group_name} deleted")