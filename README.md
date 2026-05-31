# Global Live Class Booking Service

## Project Overview
This project implements a backend service for a Global Live Class Offering Booking System. It is designed to manage global class schedules, handle complex timezone conversions, and ensure data consistency during concurrent booking attempts. The system supports two primary user roles: Teachers (who create offerings and manage schedules) and Parents/Students (who view and book offerings).

**Core Domain Entities:**
*   **Course/Class**: The base template for learning content.
*   **Offering / Section**: A specific schedulable instance of a Course.
*   **Session**: The actual meeting occurrences within an offering, defined by specific start and end times.
*   **User**: Divided into Teacher and Parent/Student roles.

**Key Functional Requirements:**
*   **Teacher Workflow**: Create offerings, manage sessions, view self-created offerings.
*   **Parent Workflow**: Discover available offerings (localized to their timezone), book entire offerings, view booked offerings.

**Critical Business Rules:**
1.  **Atomic Offering Booking**: Booking is strictly at the Offering level.
2.  **Timezone Integrity**: Teachers create schedules in their local timezone; Parents see session timings converted to their local timezone. Timestamps are stored in UTC.
3.  **Conflict Detection & Locking**: Prevents parents from booking overlapping sessions.
4.  **Concurrency Handling**: Robust handling of simultaneous booking attempts and prevention of double-booking overlapping slots.

## Tech Stack Used
*   **Language**: Java 17
*   **Framework**: Spring Boot (3.5.14)
*   **Database**: PostgreSQL
*   **ORM**: Spring Data JPA / Hibernate
*   **Build Tool**: Maven
*   **API Documentation**: Springdoc OpenAPI
*   **Containerization**: Docker, Docker Compose
*   **Utility**: Lombok

## Setup Instructions

### Prerequisites
Before you begin, ensure you have the following installed:
*   Java Development Kit (JDK) 17
*   Apache Maven (or use the provided Maven Wrapper)
*   Docker
*   Docker Compose

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/global-live-class-booking-service.git
cd global-live-class-booking-service
```

### 2. Build the Application
Use the Maven Wrapper to build the Spring Boot application. This will generate a JAR file in the `target/` directory.
```bash
./mvnw clean package -DskipTests
```

### 3. Run with Docker Compose
The project includes a `docker-compose.yml` file that sets up both the Spring Boot application and a PostgreSQL database.
```bash
docker-compose up --build
```
This command will:
*   Build the Docker image for the Spring Boot application.
*   Start a PostgreSQL container, initializing it with schema (`01_schema.sql`) and sample data (`02_data.sql`).
*   Start the Spring Boot application container.

The application will be accessible at `http://localhost:8080`.

## Environment Variables Required
The application's database connection is configured via environment variables, primarily managed by `docker-compose.yml`:

| Variable                      | Description                       | Default Value (in `docker-compose.yml`) |
| :---------------------------- | :-------------------------------- | :-------------------------------------- |
| `SPRING_DATASOURCE_URL`       | JDBC URL for the PostgreSQL database | `jdbc:postgresql://db:5432/live-class-booking` |
| `SPRING_DATASOURCE_USERNAME`  | Database username                 | `postgres`                              |
| `SPRING_DATASOURCE_PASSWORD`  | Database password                 | `12345`                                 |

## API Documentation
The API documentation is generated using Springdoc OpenAPI and can be accessed at:
*   **Swagger UI**: `http://localhost:8080/swagger-ui.html`
*   **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Database Schema Overview
The database schema is defined in `src/main/resources/01_schema.sql` and includes the following tables:

*   **`users`**: Stores user information (teachers and parents), including their name, role, and timezone.
    *   `id` (BIGSERIAL PRIMARY KEY)
    *   `name` (VARCHAR)
    *   `role` (VARCHAR)
    *   `timezone_id` (VARCHAR)
*   **`courses`**: Stores general course details.
    *   `id` (BIGSERIAL PRIMARY KEY)
    *   `title` (VARCHAR)
    *   `description` (TEXT)
*   **`offerings`**: Represents a specific instance or section of a course.
    *   `id` (BIGSERIAL PRIMARY KEY)
    *   `course_id` (BIGINT, FK to `courses`)
    *   `teacher_id` (BIGINT, FK to `users`)
    *   `name` (VARCHAR)
    *   `price` (DECIMAL)
    *   `currency` (VARCHAR)
    *   `total_seats` (INTEGER)
    *   `booked_seats` (INTEGER)
    *   `version` (BIGINT, for optimistic locking)
*   **`sessions`**: Defines specific time slots for an offering.
    *   `id` (BIGSERIAL PRIMARY KEY)
    *   `offering_id` (BIGINT, FK to `offerings`)
    *   `teacher_id` (BIGINT, FK to `users`)
    *   `start_time` (TIMESTAMP WITH TIME ZONE)
    *   `end_time` (TIMESTAMP WITH TIME ZONE)
*   **`bookings`**: Records a parent's booking of an entire offering.
    *   `id` (BIGSERIAL PRIMARY KEY)
    *   `user_id` (BIGINT, FK to `users`)
    *   `offering_id` (BIGINT, FK to `offerings`)
    *   `booked_at` (TIMESTAMP WITH TIME ZONE)
    *   `booking_price` (DECIMAL)
    *   `currency` (VARCHAR)
    *   `CONSTRAINT unique_user_offering` (Ensures a user can book an offering only once)

## Assumptions Made
*   **Atomic Booking**: Booking is strictly at the offering level; individual sessions cannot be booked.
*   **Timezone Storage**: All timestamps are stored in UTC in the database to maintain global consistency.
*   **User Roles**: Users are strictly categorized as either `TEACHER` or `PARENT`.
*   **Currency**: Default currency for offerings is USD, but can be specified.
*   **Optimistic Locking**: The `offerings` table includes a `version` column to handle concurrent updates to `booked_seats`.

## Concurrency Handling Approach
The system employs the following strategies for concurrency control:
*   **Optimistic Locking**: The `offerings` table includes a `version` column. When a booking attempt is made, the `version` is checked and incremented. If the `version` has changed between reading and writing, it indicates a concurrent modification, and the transaction will be retried or rolled back. This helps prevent race conditions on `booked_seats`.
*   **Database Transactions**: All booking operations are encapsulated within database transactions to ensure atomicity and isolation.
*   **Unique Constraints**: A `unique_user_offering` constraint on the `bookings` table prevents a single user from booking the same offering multiple times.

## Timezone Handling Approach
Timezone management is critical for a global system:
*   **Storage**: All `TIMESTAMP` fields in the database (e.g., `sessions.start_time`, `sessions.end_time`, `bookings.booked_at`) are stored as `TIMESTAMP WITH TIME ZONE`. This ensures that the exact point in time is preserved regardless of the server's local timezone.
*   **Teacher Input**: Teachers are assumed to input session times in their local timezone, which is stored in the `users.timezone_id` field. The application is responsible for converting these to UTC before storing them.
*   **Parent Display**: When parents view offerings, the system converts the UTC session times back to the parent's local timezone (also stored in `users.timezone_id`) for display.
*   **Java Time API**: The application leverages Java's `java.time` package (e.g., `ZonedDateTime`, `ZoneId`) for robust timezone conversions.

## Steps to Run the Application Locally
Please refer to the "Setup Instructions" section above for detailed steps on how to build and run the application using Docker Compose.
The application will be available at `http://localhost:8080` and its API documentation at `http://localhost:8080/swagger-ui.html`.

---

Made with ❤️ and Late Night Efforts by Anant Saini.
© 2026 Anant Saini. All rights reserved.
