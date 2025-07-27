
# Two Spring Boot Services: Auth + Todo
This challenge is part of the application process at Europace.

This project consists of two Spring Boot microservices:

- auth-service – handles authentication and token verification
- todo-service – provides a todo management API 

The services communicate over HTTP and are containerized using Docker Compose. H2 is used as an in-memory database.

## How to Run with Docker
From the root of the project, run:
```
docker-compose up --build
```
This will start both services:

- auth-service on port 8081
- todo-service on port 8080

## API Endpoints & Examples
## Auth Service (http://localhost:8081)

**POST /register**

Register a new user

Request body:
```
{
  "username": "john",
  "password": "password123"
}
```

**POST /login**

Authenticate user and get a token

Request body:
```
{
  "username": "user",
  "password": "password"
}
```
Response: 
```
ee3918a5-066c-4690-8580-8b2ede9fc4efff
```

**POST /token**

Verify a token and get a user id

Request header:
```
Authorization: "ee3918a5-066c-4690-8580-8b2ede9fc4efff"

```
Response: 
```
{
  "id": 1,  
  "username": "user",
  "password": "password"
}
```

## Todo Service (http://localhost:8080)
**POST /todos**

Add a new todo item

Request header:
```
Authorization: "ee3918a5-066c-4690-8580-8b2ede9fc4efff"

```
Request body:
```
{
  "title": "Buy groceries",
  "description": "We need milk",
  "status": "TODO"
}
```
**GET /todos**

Get all todos for the authenticated user

Request header:
```
Authorization: "ee3918a5-066c-4690-8580-8b2ede9fc4efff"

```

**Possible responses:**

401 Unauthorized: Token missing or invalid

403 Forbidden: User trying to access unauthorized data

200 OK: Todos returned successfully