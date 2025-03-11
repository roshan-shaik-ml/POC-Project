"""
Core matching engine module that handles property matching logic.

This module contains the main matching engine implementation that connects to
databases, processes user preferences, finds matching properties, and generates leads.
"""

from sqlalchemy import create_engine, text
from loguru import logger
import schedule
import time
import uuid
from datetime import datetime
from typing import List, Dict
import json

from .config import settings
from .models import Property, Preference, PropertyLead
from .kafka_producer import LeadProducer

class MatchingEngine:
    """
    Core matching engine that processes preferences and generates property leads.
    
    This class handles the main matching logic, database connections, and lead generation.
    It runs on a scheduled interval and processes all active preferences against available
    properties to find potential matches.
    """

    def __init__(self):
        """
        Initializes database connections and Kafka producer.
        """
        # Initialize database connections
        self.users_db = create_engine(
            f"postgresql://{settings.USERS_DB_USER}:{settings.USERS_DB_PASSWORD}@"
            f"{settings.USERS_DB_HOST}:{settings.USERS_DB_PORT}/{settings.USERS_DB_NAME}"
        )
        
        self.realtor_db = create_engine(
            f"postgresql://{settings.REALTOR_DB_USER}:{settings.REALTOR_DB_PASSWORD}@"
            f"{settings.REALTOR_DB_HOST}:{settings.REALTOR_DB_PORT}/{settings.REALTOR_DB_NAME}"
        )
        
        self.kafka_producer = LeadProducer()
        logger.info("Matching Engine initialized")

    def get_all_preferences(self) -> List[Dict]:
        """
        Retrieves all user preferences from the database.
        
        Returns:
            List[Dict]: List of preference dictionaries with user information
        """
        query = """
        SELECT 
            p.id, p.min_price, p.max_price, p.beds, p.baths, p.min_area, 
            p.type, p.city, p.state,
            u.id as user_id, u.username, u.email, u.first_name, u.last_name, u.phone,
            array_agg(z.zipcode) as zipcodes
        FROM preference p
        JOIN users u ON p.user_id = u.id
        LEFT JOIN zipcode z ON z.preference_id = p.id
        GROUP BY p.id, u.id
        """
        with self.users_db.connect() as conn:
            result = conn.execute(text(query))
            return [dict(row) for row in result]

    def get_matching_properties(self, preference: Dict) -> List[Dict]:
        """
        Finds properties that match the given preference criteria.
        
        Args:
            preference: Dictionary containing user preferences
            
        Returns:
            List[Dict]: List of matching property dictionaries
        """
        # Build the query based on preference criteria
        query = """
        SELECT *
        FROM properties
        WHERE status = 'active'
        AND price BETWEEN :min_price AND :max_price
        AND beds >= :beds
        AND baths >= :baths
        AND area >= :min_area
        """
        
        params = {
            "min_price": preference["min_price"],
            "max_price": preference["max_price"],
            "beds": preference["beds"],
            "baths": preference["baths"],
            "min_area": preference["min_area"]
        }

        # Add type filter if specified
        if preference.get("type"):
            query += " AND property_type = :property_type"
            params["property_type"] = preference["type"]

        # Add location filters
        if preference.get("city"):
            query += " AND city = :city"
            params["city"] = preference["city"]
        
        if preference.get("state"):
            query += " AND state = :state"
            params["state"] = preference["state"]

        # Add zipcode filter if zipcodes are specified
        if preference.get("zipcodes"):
            query += " AND zipcode = ANY(:zipcodes)"
            params["zipcodes"] = preference["zipcodes"]

        with self.realtor_db.connect() as conn:
            result = conn.execute(text(query), params)
            return [dict(row) for row in result]

    def calculate_match_score(self, preference: Dict, property: Dict) -> float:
        """
        Calculates a match score between a preference and a property.
        
        The score is based on various factors including price, beds, baths,
        area, and location matches. The maximum score is 100.
        
        Args:
            preference: Dictionary containing user preferences
            property: Dictionary containing property details
            
        Returns:
            float: Match score between 0 and 100
        """
        score = 0.0
        
        # Price match (30%)
        if property["price"] >= preference["min_price"] and property["price"] <= preference["max_price"]:
            price_score = 30.0
            # Bonus for being in the middle of the range
            price_range = preference["max_price"] - preference["min_price"]
            if price_range > 0:
                price_position = abs((property["price"] - preference["min_price"]) / price_range - 0.5)
                price_score += (0.5 - price_position) * 10
            score += price_score

        # Beds match (20%)
        if property["beds"] >= preference["beds"]:
            score += 20.0
            # Bonus for exact match
            if property["beds"] == preference["beds"]:
                score += 5.0

        # Baths match (15%)
        if property["baths"] >= preference["baths"]:
            score += 15.0
            # Bonus for exact match
            if property["baths"] == preference["baths"]:
                score += 5.0

        # Area match (15%)
        if property["area"] >= preference["min_area"]:
            score += 15.0
            # Penalty for being too much larger
            if property["area"] > preference["min_area"] * 1.5:
                score -= 5.0

        # Location match (20%)
        if property["zipcode"] in preference["zipcodes"]:
            score += 20.0
        elif property["city"] == preference.get("city"):
            score += 15.0
        elif property["state"] == preference.get("state"):
            score += 10.0

        return min(100.0, score)

    def run_matching(self):
        """
        Executes one complete matching cycle.
        
        Retrieves all preferences, finds matching properties, calculates scores,
        and generates leads for high-scoring matches.
        """
        try:
            logger.info("Starting matching process")
            preferences = self.get_all_preferences()
            logger.info(f"Found {len(preferences)} preferences to process")

            for preference in preferences:
                matching_properties = self.get_matching_properties(preference)
                logger.info(f"Found {len(matching_properties)} matching properties for preference {preference['id']}")

                for property in matching_properties:
                    match_score = self.calculate_match_score(preference, property)
                    
                    # Only create leads for good matches (score > 70)
                    if match_score > 70:
                        lead = PropertyLead(
                            id=uuid.uuid4(),
                            user_id=preference["user_id"],
                            property_id=property["id"],
                            preference_id=preference["id"],
                            match_score=match_score,
                            user={
                                "username": preference["username"],
                                "email": preference["email"],
                                "first_name": preference["first_name"],
                                "last_name": preference["last_name"],
                                "phone": preference["phone"]
                            },
                            property=property,
                            preference=preference
                        )
                        
                        # Send to Kafka
                        self.kafka_producer.send_lead(
                            lead_id=str(lead.id),
                            lead_data=lead.to_kafka_message()
                        )
                        logger.info(f"Created and sent lead {lead.id} with match score {match_score}")

            logger.info("Matching process completed")
        except Exception as e:
            logger.error(f"Error in matching process: {str(e)}")
            raise

    def start(self):
        """
        Starts the matching engine.
        
        Schedules regular matching runs and executes the first run immediately.
        """
        logger.info("Starting Matching Engine")
        # Schedule the matching process
        schedule.every(settings.MATCHING_INTERVAL_MINUTES).minutes.do(self.run_matching)
        
        # Run immediately on start
        self.run_matching()
        
        # Keep running
        while True:
            schedule.run_pending()
            time.sleep(1)

    def stop(self):
        """
        Stops the matching engine and cleans up resources.
        """
        self.kafka_producer.close()
        logger.info("Matching Engine stopped")
