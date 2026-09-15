# Puzzle Store

Puzzle Store is a Spring Boot web application for managing and serving puzzle data. The project is built with Java and Maven, with PostgreSQL persistence, Spring Data JPA, Spring Security, and Thymeleaf for the web layer.


## Tech Stack
- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- OpenCSV / Jackson CSV
- Maven

## Highlights
- Secure application architecture with Spring Security
- Persistent data storage using PostgreSQL and Spring Data JPA
- Server-rendered web UI powered by Spring MVC and Thymeleaf
- Request and model validation using Jakarta Bean Validation
- CSV data processing with OpenCSV and Jackson CSV
- Layered Spring architecture designed for maintainability
- Modern Java 21 implementation
- Spring Boot testing infrastructure for persistence, security, MVC, Thymeleaf, and validation
- Maven-based build with the Maven Wrapper for reproducible development

## Customer Experience
- Browse the puzzle store
- View available puzzles
- View individual puzzle information
- Create and use a customer account
- Authenticate securely
- Access functionality available to regular customers
- Interact with the storefront without administrative privileges

## Administrator Experience
- Secure administrator authentication
- Role-based access to administrative functionality
- Manage application data through protected functionality
- Automatically provision the initial administrator account 
- Access functionality unavailable to regular customers

## Admin Account & Initialization
Puzzle Store includes an application initializer that prepares the database when the application starts.

On first startup, the initializer is responsible for creating the application's initial administrative user if one does not already exist. This means a fresh database can be brought into a usable state without manually inserting users or roles through SQL.

Default Admin Account
For local development, the initializer creates the following administrator account:

- Username / Email: admin.user@gmail.com
- Password: admin123
- Role: ADMIN

## Getting Started
### Prerequisites
Make sure you have the following installed:
- Java 21 or later 
- PostgreSQL 
- Git

### Configure the Database
Puzzle Store uses PostgreSQL through Spring Data JPA.

Create a PostgreSQL database for the application, then configure the database connection in your Spring Boot configuration.

1. Clone the repository
   git clone https://github.com/amyfan7/puzzle-store.git
   cd puzzle-store

2. Create a PostgreSQL database
   Create a local database for the application:

3. Start the application
   The project includes the Maven Wrapper, so you don't need to install Maven globally. 
./mvnw spring-boot:run
4. Build the application
      ./mvnw clean package
The resulting JAR will be generated under:
target/
Run the packaged application with:
java -jar target/puzzlestore-0.0.1-SNAPSHOT.jar

