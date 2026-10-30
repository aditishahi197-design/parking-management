# Parking Management System REST API

A Spring Boot REST API for managing parking slots, vehicle bookings, checkout billing, and date-wise revenue using PostgreSQL and Spring Data JPA.

## Technologies Used

- Java 21
- Spring Boot 3.5.6
- Maven
- PostgreSQL
- Spring Data JPA
- Hibernate
- Swagger UI / OpenAPI
- JUnit

## Requirements

- Java 21
- Maven (or Maven Wrapper, if included)
- PostgreSQL
- pgAdmin 4 (optional, for database management)
- Postman or Swagger UI (for API testing)

## Database Setup

1. Open pgAdmin and create a database named `parking_db`.
2. Configure your PostgreSQL credentials in your terminal.

For macOS (zsh):

```bash
export DB_USERNAME=postgres
export DB_PASSWORD='YOUR_POSTGRES_PASSWORD'
```

Optional database URL override:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/parking_db
```

**Note:** Replace `YOUR_POSTGRES_PASSWORD` with your actual PostgreSQL password. Do not commit real database credentials to GitHub.

## Run the Application

Navigate to the project directory:

```bash
cd parking-management
```

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

Hibernate creates or updates database tables at application startup using `ddl-auto=update`.

`src/main/resources/schema.sql` is provided as a reference schema. It is not automatically executed by the default configuration.

## Swagger UI

After starting the application, open:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Swagger UI allows you to execute and test the REST API endpoints.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/slots` | Create a parking slot |
| GET | `/api/slots/available` | List available parking slots |
| POST | `/api/bookings` | Book a parking slot for a vehicle |
| GET | `/api/bookings/active` | List active bookings |
| GET | `/api/bookings/{id}/fare` | Calculate parking fare |
| POST | `/api/bookings/{id}/checkout` | Checkout, record fare, and release slot |
| GET | `/api/reports/revenue?date=2026-10-25` | Retrieve paid revenue for a date |

### Fare Calculation at a Specific Time

To calculate the fare at a particular timestamp, use the optional `at` parameter:

```text
GET /api/bookings/{id}/fare?at=2026-10-25T13:05:00
```

## Example Request Bodies

### 1. Create Parking Slot

```json
{
  "slotNumber": "A-01"
}
```

### 2. Book a Vehicle

```json
{
  "vehicleNumber": "MH31AB1234",
  "slotId": 1
}
```

## Billing Rule

The parking fee is **₹50 per 3 hours or any fraction thereof**.

| Parking Duration | Parking Fee |
|---|---:|
| Zero duration | ₹0 |
| 10 minutes | ₹50 |
| 1 hour | ₹50 |
| Exactly 3 hours | ₹50 |
| 3 hours and 5 minutes | ₹100 |
| 6 hours | ₹100 |
| 6 hours and 10 minutes | ₹150 |

The fare is calculated based on the vehicle's entry and exit timestamps.

Checkout marks the payment status as `PAID` to simulate successful payment for this assignment. No external payment gateway is integrated.

## Error Handling

The API uses standardized JSON error responses.

| HTTP Status | Description |
|---|---|
| 400 Bad Request | Invalid input or validation failure |
| 404 Not Found | Requested resource does not exist |
| 409 Conflict | Conflict, such as booking an occupied slot |
| 500 Internal Server Error | Unexpected server-side error |

Unexpected errors return a generic response without exposing internal exception details.

## Run Automated Tests

Execute the following command from the project directory:

```bash
mvn test
```

The tests verify application functionality, including parking fee calculation.

## Project Notes

- The application provides a REST API and does not include a frontend.
- Swagger UI or Postman can be used for API testing.
- PostgreSQL stores parking slots and vehicle booking records.
- Database passwords and other sensitive credentials should not be committed to source control.
- For production deployment, database migrations using Flyway or Liquibase, authentication, and real payment confirmation should be implemented.

## Future Enhancements

- User authentication and authorization.
- Online payment gateway integration.
- Parking history and detailed analytics.
- Frontend dashboard for parking management.
- Deployment to a cloud platform.