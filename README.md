# Zeta Application

Microservices-based application for user and document management, developed as a technical exercise.

---

## 🏗️ Architecture

The application is composed of two main microservices:

- **UserService**
  - user management
  - authentication and authorization (JWT)

- **DocumentService**
  - document management
  - digital signature simulation

Each service has its own dedicated PostgreSQL database.

Microservices communicate via REST APIs and share the security context using JWT.

---

## 🧱 Internal Architecture

Each microservice follows a layered architecture:

- Controller → handles HTTP requests  
- Service → contains business logic  
- Repository → data access layer  
- Entity → data model  
- DTO → data transfer objects  

---

## 🔐 Security

- Authentication via JWT
- Passwords encrypted with BCrypt
- Stateless system (no server-side sessions)

### Flow:
1. User logs in and receives a JWT token  
2. The token is included in the Authorization header  
3. Protected endpoints validate the token  

### Authorization:
- USER → access to own data  
- ADMIN → full access  

---

## ⚙️ Technologies Used

- Java + Spring Boot
- Spring Security
- JWT (JSON Web Token)
- JPA / Hibernate
- PostgreSQL (main database)
- H2 (in-memory database for testing)
- Docker / Docker Compose
- Postman (manual API testing)
- JUnit & Mockito (automated testing)

---

## 🚀 Running the Project

Prerequisite:
- Docker installed

### Start the application:

```bash
docker-compose up --build


