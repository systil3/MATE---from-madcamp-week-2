from fastapi import APIRouter
from database import SessionLocal
from db.models.appointments import Appointment
from db.crud.crud_appointment import *
from starlette import status

router = APIRouter(
    prefix="/appointments",
)

# Function to get the database session
def get_db():
    # Assuming SessionLocal is your function to create a database session
    db = SessionLocal()  
    try:
        yield db
    finally:
        db.close()
        
@router.post("/create", response_model=AppointmentBase)
def create_appointment_api(
    appoint: AppointmentBase,
    db: Session = Depends(get_db)
):
    return create_appointment(db=db, appoint=appoint)

@router.get("/info/{appointment_id}", response_model=AppointmentBase)
def read_appointment(appointment_id: int, db: Session = Depends(get_db)):
    appointment = get_appointment(db, appointment_id)
    if appointment is None:
        raise HTTPException(status_code=404, detail="Appointment not found")
    return appointment

@router.put("/update/{appointment_id}", response_model=AppointmentBase)
def update_appointment_api(
    appoint = AppointmentBase,
    db: Session = Depends(get_db)
):
    appointment = update_appointment(db, appoint=appoint)
    if appointment is None:
        raise HTTPException(status_code=404, detail="Appointment not found")
    return appointment

@router.delete("/delete/{appointment_id}", response_model=AppointmentBase)
def delete_appointment_api(appointment_id: int, db: Session = Depends(get_db)):
    appointment = delete_appointment(db, appointment_id)
    if appointment is None:
        raise HTTPException(status_code=404, detail="Appointment not found")
    return appointment
