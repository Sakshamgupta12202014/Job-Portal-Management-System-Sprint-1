# JPMS - Admin Service

## Overview

The **Admin Service** provides administrative capabilities for the Job Portal Management System. Admins can view all users and jobs across the platform, delete users/jobs, and generate platform-wide reports. This service communicates with Auth Service, Job Service, and Application Service via Feign clients.

| Property | Value |
|---|---|
| **Port** | `8084` |
| **Spring App Name** | `ADMIN-SERVICE` |
| **Database** | MySQL — `admin_db` |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL running with database `admin_db` created
- Eureka Server running on `localhost:8761`
- Auth Service running (for user data via Feign)
- Job Service running (for job data via Feign)
- Application Service running (for application stats via Feign)

> **Note:** All dependent services must be running for Admin Service to function correctly.

---

## Running the Service

```bash
cd JPMS-AdminService
mvn spring-boot:run
```

---

## API Endpoints

### Base URL

| Access | URL |
|---|---|
| **Direct** | `http://localhost:8084` |
| **Via Gateway** | `http://localhost:8080` |

> All endpoints require JWT authentication via the Gateway and **ADMIN** role.

---

### 1. Get All Users *(Admin Only)*

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/admin/users` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Required Role** | `ADMIN` |

#### Success Response — `200 OK`

```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "9876543210",
    "role": "JOB_SEEKER"
  },
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane@company.com",
    "phone": "9876543211",
    "role": "RECRUITER"
  }
]
```

#### Error Response — `403 Forbidden`

Returned if the user's role is not `ADMIN`.

---

### 2. Delete a User *(Admin Only)*

| Field | Value |
|---|---|
| **Method** | `DELETE` |
| **URL** | `http://localhost:8080/api/admin/users/{id}` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Required Role** | `ADMIN` |

#### Example

`DELETE http://localhost:8080/api/admin/users/3`

#### Success Response — `204 No Content`

---

### 3. Get All Jobs *(Admin Only)*

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/admin/jobs` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Required Role** | `ADMIN` |

#### Success Response — `200 OK`

```json
[
  {
    "id": 1,
    "title": "Java Backend Developer",
    "companyName": "TechCorp Inc.",
    "location": "Bangalore",
    "salary": "12-18 LPA",
    "experience": "3-5 years",
    "description": "...",
    "postedByEmail": "recruiter@example.com",
    "createdAt": "2026-03-22T10:30:00"
  }
]
```

---

### 4. Delete a Job *(Admin Only)*

| Field | Value |
|---|---|
| **Method** | `DELETE` |
| **URL** | `http://localhost:8080/api/admin/jobs/{id}` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Required Role** | `ADMIN` |

#### Example

`DELETE http://localhost:8080/api/admin/jobs/1`

#### Success Response — `204 No Content`

---

### 5. Get Platform Report *(Admin Only)*

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/admin/reports` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Required Role** | `ADMIN` |

#### Success Response — `200 OK`

```json
{
  "totalUsers": 15,
  "totalJobs": 8,
  "applicationStats": {
    "totalApplications": 25,
    "pendingCount": 10,
    "shortlistedCount": 8,
    "rejectedCount": 5,
    "acceptedCount": 2
  },
  "users": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "9876543210",
      "role": "JOB_SEEKER"
    }
  ],
  "jobs": [
    {
      "id": 1,
      "title": "Java Backend Developer",
      "companyName": "TechCorp Inc.",
      "location": "Bangalore",
      "salary": "12-18 LPA",
      "experience": "3-5 years",
      "description": "...",
      "postedByEmail": "recruiter@example.com",
      "createdAt": "2026-03-22T10:30:00"
    }
  ]
}
```

---

## Postman Testing Workflow

1. **Register** an admin user via Auth Service:
   ```json
   {
     "name": "Admin User",
     "email": "admin@portal.com",
     "password": "admin123",
     "phone": "9000000000",
     "role": "ADMIN"
   }
   ```
2. **Login** as admin → save the JWT token
3. **View** all users (endpoint #1) and all jobs (endpoint #3)
4. **Generate** the platform report (endpoint #5)
5. **Delete** a user (endpoint #2) or job (endpoint #4) and verify removal
6. Verify `403 Forbidden` by sending requests with a non-ADMIN token

---

## End-to-End Testing Order

For a complete Postman testing flow across all services:

| Step | Service | Action |
|---|---|---|
| 1 | Auth Service | Register a `RECRUITER`, `JOB_SEEKER`, and `ADMIN` |
| 2 | Auth Service | Login as `RECRUITER` → save token |
| 3 | Job Service | Create a few jobs |
| 4 | Auth Service | Login as `JOB_SEEKER` → save token |
| 5 | Application Service | Apply for jobs |
| 6 | Auth Service | Login as `RECRUITER` → save token |
| 7 | Application Service | View applications and update statuses |
| 8 | Auth Service | Login as `ADMIN` → save token |
| 9 | Admin Service | View all users, jobs, and platform report |
| 10 | Admin Service | Delete a user or job |
