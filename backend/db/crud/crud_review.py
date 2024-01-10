from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from models import *
from pydantic import BaseModel

class ReviewBase(BaseModel):
    id : int
    name : str
    details : str
    appointment_id : int

def to_ReviewBase(review):
    return ReviewBase(
        id = review.id,
        name = review.name,
        details = review.details,
        appointment_id = review.appointment_id
    )

def create_review(db: Session, name: str, details: str, appointment_id: int):
    review = Review(name=name, details=details, appointment_id=appointment_id)
    db.add(review)
    db.commit()
    db.refresh(review)
    return to_ReviewBase(review)

def get_review(db: Session, review_id: int):
    review = db.query(Review).filter(Review.id == review_id).first()
    return to_ReviewBase(review)

def get_reviews(db: Session, skip: int = 0, limit: int = 10):
    review = db.query(Review).filter(Review.id == review_id).first()
    return to_ReviewBase(review)

def update_review(db: Session, review_id: int, name: str, details: str):
    review = db.query(Review).filter(Review.id == review_id).first()
    if review:
        review.name = name
        review.details = details
        db.commit()
        db.refresh(review)
    else:
        raise HTTPException(status_code=404, detail="Review not found")
    return to_ReviewBase(review)

def delete_review(db: Session, review_id: int):
    review = db.query(Review).filter(Review.id == review_id).first()
    if review:
        db.delete(review)
        db.commit()
    else:
        raise HTTPException(status_code=404, detail="Review not found")
    return to_ReviewBase(review)


