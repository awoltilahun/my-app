# TechApp Backend - Spring Boot REST API

## Overview
Spring Boot REST API backend for TechApp with MySQL database integration.

## Technology Stack
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.x
- Maven

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.x
- MySQL Workbench (optional)

## Database Setup

1. Start MySQL server
2. Run the database setup script:
```bash
mysql -u root -p < ../database/setup.sql
```

Or manually execute the SQL commands in `../database/setup.sql`

## Configuration

Update `src/main/resources/application.properties` if needed:
- Database URL: `jdbc:mysql://localhost:3306/myappdb`
- Username: `myappuser`
- Password: `mypassword`

## Build and Run

### Using Maven:
```bash
# Navigate to backend directory
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Using Java:
```bash
# Build JAR file
mvn clean package

# Run JAR
java -jar target/demo-1.0.0.jar
```

## API Endpoints

### Tech Tips API
- `GET /api/techtips` - Get all tech tips
- `GET /api/techtips/{id}` - Get tech tip by ID
- `POST /api/techtips` - Create new tech tip
- `PUT /api/techtips/{id}` - Update tech tip
- `DELETE /api/techtips/{id}` - Delete tech tip
- `GET /api/techtips/search?keyword={keyword}` - Search tech tips

### App Links API
- `GET /api/applinks` - Get all app links
- `GET /api/applinks/{id}` - Get app link by ID
- `POST /api/applinks` - Create new app link
- `PUT /api/applinks/{id}` - Update app link
- `DELETE /api/applinks/{id}` - Delete app link
- `GET /api/applinks/search?keyword={keyword}` - Search app links

## Sample API Requests

### Create Tech Tip (POST)
```json
{
  "title": "Android Best Practices",
  "description": "Learn modern Android development patterns",
  "videoLink": "https://www.youtube.com/watch?v=example"
}
```

### Create App Link (POST)
```json
{
  "name": "Netflix",
  "playstoreUrl": "https://play.google.com/store/apps/details?id=com.netflix.mediaclient"
}
```

## Testing
Base URL: `http://localhost:8080`

Use tools like:
- Postman
- cURL
- Swagger UI (can be added)
- Browser for GET requests

## Troubleshooting

### Database Connection Issues
- Verify MySQL is running
- Check database credentials in `application.properties`
- Ensure database `myappdb` exists

### Port Already in Use
Change port in `application.properties`:
```
server.port=8081
```

## Project Structure
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/techapp/demo/
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── DemoApplication.java
│   │   └── resources/
│   │       └── application.properties
└── pom.xml
```
