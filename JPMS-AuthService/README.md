# JPMS - Auth Service

## Overview

The **Auth Service** handles user registration and login for the Job Portal Management System. It issues JWT tokens upon successful authentication, which are required by all other services (via the API Gateway).

| Property | Value |
|---|---|
| **Port** | `8081` |
| **Spring App Name** | `AUTH-SERVICE` |
| **Database** | MySQL — `auth_db` |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL running with database `auth_db` created
- Eureka Server running on `localhost:8761`

---

## Running the Service

```bash
cd JPMS-AuthService
mvn spring-boot:run
```

---

## API Endpoints

### Base URL

| Access | URL |
|---|---|
| **Direct** | `http://localhost:8081` |
| **Via Gateway** | `http://localhost:8080` |

> All `/api/auth/**` endpoints are **public** (no JWT required).

---

### 1. Register a New User

| Field | Value |
|---|---|
| **Method** | `POST` |
| **URL** | `http://localhost:8080/api/auth/register` |
| **Content-Type** | `application/json` |

#### Request Body

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "9876543210",
  "role": "JOB_SEEKER"
}
```

> **Valid roles:** `JOB_SEEKER`, `RECRUITER`, `ADMIN`

#### Success Response — `201 Created`

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "JOB_SEEKER",
  "userId": 1
}
```

#### Error Response — `400 Bad Request` (Validation)

```json
{
  "name": "must not be blank",
  "email": "must be a well-formed email address"
}
```

---

### 2. Login

| Field | Value |
|---|---|
| **Method** | `POST` |
| **URL** | `http://localhost:8080/api/auth/login` |
| **Content-Type** | `application/json` |

#### Request Body

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

#### Success Response — `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "JOB_SEEKER",
  "userId": 1
}
```

#### Error Response — `401 Unauthorized`

```json
{
  "message": "Invalid credentials"
}
```

---

### 3. Get All Users *(Internal Only)*

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8081/api/internal/users` |
| **Description** | Internal endpoint — not exposed via API Gateway. Used by Admin Service via Feign client. |

#### Sample Response — `200 OK`

```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "9876543210",
    "role": "JOB_SEEKER"
  }
]
```

---

### 4. Delete User by ID *(Internal Only)*

| Field | Value |
|---|---|
| **Method** | `DELETE` |
| **URL** | `http://localhost:8081/api/internal/users/{id}` |
| **Description** | Internal endpoint — not exposed via API Gateway. Used by Admin Service. |

#### Success Response — `204 No Content`

#### Error Response — `404 Not Found`

---

## Postman Testing Workflow

1. **Register** users with different roles (`JOB_SEEKER`, `RECRUITER`, `ADMIN`)
2. **Login** with each user to obtain JWT tokens
3. **Save** the `token` from the response into a Postman environment variable `JWT_TOKEN`
4. Use that token as `Bearer Token` in subsequent requests to other services

### Postman Script to Auto-Save Token

Add this to the **Tests** tab of both Register and Login requests:

```javascript
var jsonData = pm.response.json();
pm.environment.set("JWT_TOKEN", jsonData.token);
pm.environment.set("USER_ROLE", jsonData.role);
pm.environment.set("USER_ID", jsonData.userId);
```
