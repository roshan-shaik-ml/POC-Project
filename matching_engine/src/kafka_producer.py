"""
Kafka producer module for sending property leads to Kafka topics.

This module handles the communication with Kafka brokers and provides
a clean interface for sending lead data to the appropriate topics.
"""

from kafka import KafkaProducer
import json
from loguru import logger
from .config import settings

class LeadProducer:
    """
    Handles the production of lead messages to Kafka topics.
    
    This class manages the connection to Kafka and provides methods
    for sending lead data in a reliable way.
    """

    def __init__(self):
        """
        Initializes the Kafka producer with the configured settings.
        """
        self.producer = KafkaProducer(
            bootstrap_servers=settings.KAFKA_BOOTSTRAP_SERVERS,
            value_serializer=lambda v: json.dumps(v).encode('utf-8'),
            key_serializer=lambda v: v.encode('utf-8')
        )
        self.topic = settings.KAFKA_TOPIC_LEADS

    def send_lead(self, lead_id: str, lead_data: dict):
        """
        Sends a lead message to the configured Kafka topic.
        
        Args:
            lead_id: Unique identifier for the lead
            lead_data: Dictionary containing the lead information
            
        Raises:
            Exception: If the message cannot be sent to Kafka
        """
        try:
            future = self.producer.send(
                topic=self.topic,
                key=lead_id,
                value=lead_data
            )
            future.get(timeout=10)  # Wait for the send to complete
            logger.info(f"Successfully sent lead {lead_id} to Kafka")
        except Exception as e:
            logger.error(f"Failed to send lead {lead_id} to Kafka: {str(e)}")
            raise

    def close(self):
        """
        Closes the Kafka producer connection.
        """
        self.producer.close()
