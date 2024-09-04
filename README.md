
# MoneyCard API

## Overview

The **MoneyCard API** is a Spring Boot application designed to manage money card records. It provides functionality to handle operations such as storing card information, querying card details, and managing card transactions. The project is built using **Spring Boot** with security configurations and includes unit and integration tests to ensure code quality.

## Features

- **Manage Money Cards**: Add, view, and manage money cards with details such as ID, amount, and owner.
- **REST API**: Provides endpoints to interact with the application using JSON.
- **Spring Security**: Configured to secure endpoints with basic authentication.
- **Database Integration**: Persists money card data using a database with predefined schema.
- **Testing**: Includes unit and JSON tests to validate API responses and ensure functionality.

## Project Structure

```plaintext
src/
├── main/
│   ├── java/com/example/moneycard/
│   │   ├── MoneyCardApplication.java     # Main application class
│   │   ├── MoneyCardController.java      # REST controller for handling API requests
│   │   ├── MoneyCard.java                # Model class representing a Money Card entity
│   │   ├── MoneyCardRepository.java      # Repository interface for accessing database
│   │   ├── SecurityConfig.java           # Spring Security configuration
│   └── resources/
│       ├── application.properties        # Application properties file
│       ├── schema.sql                    # SQL schema to set up the database
│
├── test/
│   ├── MoneyCardApplicationTests.java    # Test class for application context
│   ├── MoneyCardJsonTest.java            # JSON-based test for API functionality
│
└── list.json                             # Sample JSON data for testing purposes
```

## Setup Instructions

### Prerequisites

- **Java 11** or higher
- **Maven** (for building and running the application)
- **PostgreSQL** or another relational database (update configurations in `application.properties`)

### How to Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/moneycard-api.git
   cd moneycard-api
   ```

2. **Set up the database:**
    - Ensure that your database is running.
    - Update the `application.properties` file with your database connection details.

3. **Build and run the application:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the API:**
    - Once the application is running, you can access the MoneyCard API via:
      ```
      http://localhost:8080/api/moneycards
      ```

5. **Run Tests:**
   To run unit and integration tests:
   ```bash
   mvn test
   ```

## API Endpoints

| Method | Endpoint              | Description                   |
|--------|-----------------------|-------------------------------|
| GET    | `/api/moneycards`      | Retrieve all money cards       |
| GET    | `/api/moneycards/{id}` | Retrieve a specific card by ID |
| POST   | `/api/moneycards`      | Create a new money card        |
| PUT    | `/api/moneycards/{id}` | Update an existing card        |
| DELETE | `/api/moneycards/{id}` | Delete a card by ID            |

## Example JSON Data

Here is a sample of the JSON format used by the API:

```json
[
  {"id": 99, "amount": 123.45, "owner": "sarah1"},
  {"id": 100, "amount": 1.00, "owner": "sarah1"},
  {"id": 101, "amount": 150.00, "owner": "sarah1" }
]
```

## Technologies Used

- **Spring Boot**
- **Spring Security**
- **Maven**
- **JPA (Java Persistence API)**
- **JUnit** for testing



## SPRING ACADEMY

The project is based on a Course from Spring Academy.