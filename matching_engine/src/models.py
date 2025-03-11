"""
Data models for the Matching Engine.

This module defines the core data structures used throughout the matching engine,
including User preferences, Property details, and Property leads.
"""

from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime
from uuid import UUID

class User(BaseModel):
    """
    User model representing a registered user in the system.
    
    Attributes:
        id: Unique identifier for the user
        username: User's username
        email: User's email address
        first_name: User's first name
        last_name: User's last name
        phone: Optional phone number
    """
    id: UUID
    username: str
    email: str
    first_name: str
    last_name: str
    phone: Optional[str]

class Zipcode(BaseModel):
    """
    Zipcode model representing a postal code associated with a preference.
    
    Attributes:
        id: Unique identifier for the zipcode entry
        zipcode: The actual zipcode string
    """
    id: UUID
    zipcode: str

class Preference(BaseModel):
    """
    Preference model representing a user's property preferences.
    
    Attributes:
        id: Unique identifier for the preference
        user_id: ID of the user who created this preference
        min_price: Minimum acceptable price
        max_price: Maximum acceptable price
        beds: Minimum number of bedrooms
        baths: Minimum number of bathrooms
        min_area: Minimum square footage
        type: Optional property type preference
        city: Optional preferred city
        state: Optional preferred state
        zipcodes: List of preferred zipcodes
        user: Associated user object
    """
    id: UUID
    user_id: UUID
    min_price: int
    max_price: int
    beds: int
    baths: int
    min_area: float
    type: Optional[str]
    city: Optional[str]
    state: Optional[str]
    zipcodes: List[Zipcode]
    user: User

class Property(BaseModel):
    """
    Property model representing a real estate listing.
    
    Attributes:
        id: Unique identifier for the property
        address: Street address
        city: City location
        state: State location
        zipcode: Property's zipcode
        price: Listed price
        beds: Number of bedrooms
        baths: Number of bathrooms
        area: Square footage
        property_type: Type of property
        year_built: Optional year of construction
        listing_date: When the property was listed
        status: Current listing status
    """
    id: UUID
    address: str
    city: str
    state: str
    zipcode: str
    price: int
    beds: int
    baths: int
    area: float
    property_type: str
    year_built: Optional[int]
    listing_date: datetime
    status: str

class PropertyLead(BaseModel):
    """
    PropertyLead model representing a match between a user preference and a property.
    
    Attributes:
        id: Unique identifier for the lead
        user_id: ID of the matched user
        property_id: ID of the matched property
        preference_id: ID of the matching preference
        match_score: Calculated match score (0-100)
        user: User details dictionary
        property: Property details dictionary
        preference: Preference details dictionary
        created_at: Timestamp of lead creation
    """
    id: UUID
    user_id: UUID
    property_id: UUID
    preference_id: UUID
    match_score: float
    user: dict
    property: dict
    preference: dict
    created_at: datetime = datetime.now()

    def to_kafka_message(self) -> dict:
        """
        Converts the lead to a Kafka-friendly message format.
        
        Returns:
            dict: Formatted message for Kafka
        """
        return {
            "lead_id": str(self.id),
            "user_id": str(self.user_id),
            "property_id": str(self.property_id),
            "preference_id": str(self.preference_id),
            "match_score": self.match_score,
            "user_details": {
                "username": self.user["username"],
                "email": self.user["email"],
                "first_name": self.user["first_name"],
                "last_name": self.user["last_name"],
                "phone": self.user["phone"]
            },
            "property_details": {
                "address": self.property["address"],
                "city": self.property["city"],
                "state": self.property["state"],
                "zipcode": self.property["zipcode"],
                "price": self.property["price"],
                "beds": self.property["beds"],
                "baths": self.property["baths"],
                "area": self.property["area"],
                "property_type": self.property["property_type"],
                "year_built": self.property.get("year_built"),
                "listing_date": self.property["listing_date"].isoformat(),
                "status": self.property["status"]
            },
            "preference_details": {
                "min_price": self.preference["min_price"],
                "max_price": self.preference["max_price"],
                "beds": self.preference["beds"],
                "baths": self.preference["baths"],
                "min_area": self.preference["min_area"],
                "type": self.preference["type"],
                "city": self.preference["city"],
                "state": self.preference["state"],
                "zipcodes": [z["zipcode"] for z in self.preference["zipcodes"]]
            },
            "created_at": self.created_at.isoformat()
        }
