
# Dispatch Load Balancer

A Java Spring Boot REST API that assigns delivery orders to vehicles based on delivery priority, vehicle capacity, and geographic distance.

## Features

- REST APIs for accepting delivery orders and vehicle details
- Priority-aware order assignment: HIGH, MEDIUM, LOW
- Vehicle capacity validation during assignment
- Geographic distance calculation using the Haversine formula
- Dispatch plan containing assigned orders, total load, and route distance
- Validation and error handling for invalid or duplicate records
- MySQL persistence using Spring Data JPA
- Interactive API documentation with Swagger UI
- Automated tests using JUnit and Mockito

## Technology Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA and Hibernate
- MySQL
- Maven
- Jakarta Bean Validation
- Springdoc OpenAPI / Swagger UI
- JUnit 5 and Mockito

## Prerequisites

- JDK 21
- MySQL 8 or compatible version
- Git

## Configuration

Configure your MySQL connection in `src/main/resources/application.properties`.

Create a database named:

```sql
CREATE DATABASE dispatch_load_balancer;
```

Set the database URL, username, and password using your local configuration or environment variables. Do not commit real credentials to GitHub.

## Run the Application

From the project root, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The application starts on port `8080` by default.

## Run Tests

```powershell
.\mvnw.cmd clean test
```

## Swagger Documentation

When the application is running locally, open:

http://localhost:8080/swagger-ui/index.html

OpenAPI JSON:

http://localhost:8080/v3/api-docs

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/dispatch/orders` | Accept a batch of delivery orders |
| POST | `/api/dispatch/vehicles` | Accept a batch of vehicles |
| GET | `/api/dispatch/plan` | Generate and retrieve the dispatch plan |
| POST | `/api/dispatch/optimize` | Run the dispatch optimizer |
| GET | `/api/orders` | Retrieve delivery orders |
| GET | `/api/vehicles` | Retrieve vehicles |

## Example: Submit Delivery Orders

`POST /api/dispatch/orders`

```json
{
  "orders": [
    {
      "orderId": "ORD-001",
      "latitude": 17.39,
      "longitude": 78.49,
      "address": "Hyderabad Delivery Point",
      "packageWeight": 20,
      "priority": "HIGH"
    }
  ]
}
```

## Example: Submit Vehicles

`POST /api/dispatch/vehicles`

```json
{
  "vehicles": [
    {
      "vehicleId": "VEH-001",
      "capacity": 100,
      "currentLatitude": 17.385,
      "currentLongitude": 78.4867,
      "currentAddress": "Hyderabad Depot"
    }
  ]
}
```

## Dispatch Algorithm

1. Orders are processed by priority, with HIGH before MEDIUM and LOW.
2. For each order, the optimizer selects an eligible vehicle with sufficient remaining capacity.
3. Among eligible vehicles, the nearest vehicle to the order's current route position is selected.
4. The route distance is accumulated using the Haversine formula.
5. Orders that cannot be assigned are reported by the optimizer.

The algorithm is heuristic-based; it does not guarantee a globally optimal solution for every input.

## Testing

Automated tests cover application startup, order and vehicle batch services, dispatch optimization, priority handling, capacity limits, unassigned orders, and geographic distance calculations.

## Project Structure

```text
src/
  main/
    java/com/freightfox/dispatch/
      controller/
      dto/
      entity/
      exception/
      repository/
      service/
      validation/
    resources/
      application.properties
  test/
    java/com/freightfox/dispatch/
```

## Notes

- Do not commit database passwords, API secrets, or other credentials.
- The application currently uses a local MySQL database by default.
- Configure a production database before deployment.
