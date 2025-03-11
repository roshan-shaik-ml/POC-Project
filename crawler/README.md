# Real Realty - Zillow Data Scraper

A robust data scraping application designed to collect and store real estate data from Zillow. The application features a modular architecture with PostgreSQL for structured data storage and comprehensive logging capabilities.

## Features

- Automated scraping of Zillow real estate listings
- Data extraction for houses, brokers, addresses, and images
- Robust error handling and logging
- PostgreSQL database integration
- Containerized development environment with Docker
- Admin interfaces for both PostgreSQL (pgAdmin) and MongoDB (Mongo Express)

## Tech Stack

- **Python 3.x**
- **PostgreSQL** - Primary database
- **MongoDB** - Secondary database (optional)
- **Docker & Docker Compose** - Containerization
- **psycopg2** - PostgreSQL adapter for Python
- **SQLAlchemy** - SQL toolkit and ORM
- **Pydantic** - Data validation
- **Requests** - HTTP library for API calls

## Prerequisites

- Docker and Docker Compose
- Python 3.x
- pip (Python package installer)

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd real-realty
```

2. Create and activate a virtual environment:
```bash
python -m venv .venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate
```

3. Install dependencies:
```bash
pip install -r requirements.txt
```

4. Set up environment variables:
```bash
cp .env.example .env
# Edit .env with your configuration
```

5. Start the Docker containers:
```bash
docker-compose up -d
```

## Database Setup

The application uses PostgreSQL as its primary database. The database containers are configured with the following default credentials:

### PostgreSQL
- Host: localhost
- Port: 5434
- Database: entities
- Username: postgres
- Password: password

Access pgAdmin at `http://localhost:5050` with:
- Email: admin@admin.com
- Password: admin

### MongoDB (Optional)
- Host: localhost
- Port: 27017
- Username: root
- Password: example

Access Mongo Express at `http://localhost:8081`

## Project Structure

```
real-realty/
├── src/
│   ├── db/
│   │   ├── repositories/         # Database operations
│   │   └── init.sql             # Database schema
│   ├── utils/
│   │   ├── logger.py            # Logging configuration
│   │   ├── parser.py            # Data parsing utilities
│   │   └── payloads/            # API request payloads
│   └── zillow_scraper.py        # Main scraper implementation
├── logs/                        # Application logs
├── scripts/                     # Utility scripts
├── docker-compose.yml           # Docker services configuration
├── Dockerfile                   # Application container definition
├── requirements.txt             # Python dependencies
└── .env                        # Environment variables
```

## Usage

1. Ensure all containers are running:
```bash
docker-compose ps
```

2. Run the scraper:
```bash
python src/zillow_scraper.py
```

The scraper will:
- Fetch real estate listings from Zillow
- Parse and validate the data
- Store information in the database
- Log all operations in the `logs` directory

## Logging

The application implements comprehensive logging with:
- Timestamp
- Log level
- Source file and line number
- Detailed error messages and stack traces
- Separate log files for each run

Logs are stored in the `logs` directory with timestamps in the filename.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

[Add your license information here]

## Disclaimer

This project is for educational purposes only. Ensure you comply with Zillow's terms of service and robots.txt when using this scraper.

