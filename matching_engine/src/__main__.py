"""
Main entry point for the Matching Engine.

This module initializes and runs the matching engine, handling startup,
shutdown, and error conditions appropriately.
"""

from loguru import logger
from .matching_engine import MatchingEngine

def main():
    """
    Main function that starts the matching engine and handles its lifecycle.
    
    Handles graceful shutdown on keyboard interrupt and logs any fatal errors.
    """
    try:
        engine = MatchingEngine()
        engine.start()
    except KeyboardInterrupt:
        logger.info("Shutting down Matching Engine...")
        engine.stop()
    except Exception as e:
        logger.error(f"Fatal error: {str(e)}")
        raise

if __name__ == "__main__":
    main() 