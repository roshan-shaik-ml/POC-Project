"""
Configuration module for the Matching Engine.

This module handles all configuration settings for the matching engine, including
database connections, Kafka settings, and matching parameters. It uses environment
variables with fallback default values.
"""

from pydantic_settings import BaseSettings
from dotenv import load_dotenv
import os

load_dotenv()

class Settings(BaseSettings):
    """
    Settings class that manages all configuration parameters for the matching engine.
    
    Attributes:
        USERS_DB_*: User database connection parameters
        REALTOR_DB_*: Realtor database connection parameters
        KAFKA_*: Kafka broker and topic settings
        MATCHING_*: Matching engine parameters
    """
    # Users Database
    USERS_DB_HOST: str = os.getenv("USERS_DB_HOST", "localhost")
    USERS_DB_PORT: int = int(os.getenv("USERS_DB_PORT", "5433"))
    USERS_DB_NAME: str = os.getenv("USERS_DB_NAME", "temp")
    USERS_DB_USER: str = os.getenv("USERS_DB_USER", "postgres")
    USERS_DB_PASSWORD: str = os.getenv("USERS_DB_PASSWORD", "postgres")

    # Realtor Database
    REALTOR_DB_HOST: str = os.getenv("REALTOR_DB_HOST", "localhost")
    REALTOR_DB_PORT: int = int(os.getenv("REALTOR_DB_PORT", "5432"))
    REALTOR_DB_NAME: str = os.getenv("REALTOR_DB_NAME", "realtor")
    REALTOR_DB_USER: str = os.getenv("REALTOR_DB_USER", "postgres")
    REALTOR_DB_PASSWORD: str = os.getenv("REALTOR_DB_PASSWORD", "postgres")

    # Kafka Settings
    KAFKA_BOOTSTRAP_SERVERS: str = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
    KAFKA_TOPIC_LEADS: str = os.getenv("KAFKA_TOPIC_LEADS", "property_leads")

    # Matching Engine Settings
    MATCHING_INTERVAL_MINUTES: int = int(os.getenv("MATCHING_INTERVAL_MINUTES", "5"))
    MAX_PRICE_VARIANCE_PERCENT: float = float(os.getenv("MAX_PRICE_VARIANCE_PERCENT", "10.0"))
    MAX_AREA_VARIANCE_PERCENT: float = float(os.getenv("MAX_AREA_VARIANCE_PERCENT", "15.0"))

    class Config:
        env_file = ".env"

settings = Settings()
