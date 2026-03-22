# JPMS - Job Service

## Overview

The **Job Service** manages job postings for the Job Portal Management System. Recruiters can create, update, and delete jobs, while all authenticated users can browse and search jobs.

| Property | Value |
|---|---|
| **Port** | `8082` |
| **Spring App Name** | `JOB-SERVICE` |
| **Database** | MySQL — `job_db` |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL running with database `job_db` created
- Eureka Server running on `localhost:8761`
- Auth Service running (to obtain JWT tokens)

---

## Running the Service

```bash
cd JPMS-JobService
mvn spring-boot:run
```

---

## API Endpoints

### Base URL

| Access | URL |
|---|---|
| **Direct** | `http://localhost:8082` |
| **Via Gateway** | `http://localhost:8080` |

> All endpoints require JWT authentication via the Gateway (except internal endpoints).

---

### 1. Create a Job *(Recruiter Only)*

| Field | Value |
|---|---|
| **Method** | `POST` |
| **URL** | `http://localhost:8080/api/jobs` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Content-Type** | `application/json` |
| **Required Role** | `RECRUITER` |

#### Request Body

```json
{
  "title": "Java Backend Developer",
  "companyName": "TechCorp Inc.",
  "location": "Bangalore",
  "salary": "12-18 LPA",
  "experience": "3-5 years",
  "description": "Looking for an experienced Java developer with Spring Boot expertise."
}
```

#### Success Response — `201 Created`

```json
{
  "id": 1,
  "title": "Java Backend Developer",
  "companyName": "TechCorp Inc.",
  "location": "Bangalore",
  "salary": "12-18 LPA",
  "experience": "3-5 years",
  "description": "Looking for an experienced Java developer with Spring Boot expertise.",
  "postedByEmail": "recruiter@example.com",
  "createdAt": "2026-03-22T10:30:00"
}
```

#### Error Response — `403 Forbidden`

Returned if the user's role is not `RECRUITER`.

---

### 2. Get All Jobs *(Paginated)*

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/jobs` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Query Params** | `page=0&size=10&sort=createdAt,desc` *(optional)* |

#### Success Response — `200 OK`

```json
{
  "content": [
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
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

---

### 3. Get Job by ID

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/jobs/{id}` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |

#### Example

`GET http://localhost:8080/api/jobs/1`

#### Success Response — `200 OK`

```json
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
```

---

### 4. Search Jobs

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/jobs/search` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |

#### Query Parameters

| Param | Type | Required | Example |
|---|---|---|---|
| `keyword` | String | No | `Java` |
| `location` | String | No | `Bangalore` |
| `experience` | String | No | `3-5 years` |
| `page` | int | No | `0` |
| `size` | int | No | `10` |

#### Example

`GET http://localhost:8080/api/jobs/search?keyword=Java&location=Bangalore&page=0&size=10`

#### Success Response — `200 OK`

Returns a paginated response (same format as Get All Jobs).

---

### 5. Get My Jobs *(Recruiter Only)*

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/jobs/my` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Required Role** | `RECRUITER` |

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

### 6. Update a Job *(Recruiter Only)*

| Field | Value |
|---|---|
| **Method** | `PUT` |
| **URL** | `http://localhost:8080/api/jobs/{id}` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Content-Type** | `application/json` |
| **Required Role** | `RECRUITER` |

#### Request Body

```json
{
  "title": "Senior Java Backend Developer",
  "companyName": "TechCorp Inc.",
  "location": "Hyderabad",
  "salary": "18-25 LPA",
  "experience": "5-8 years",
  "description": "Updated description for senior role."
}
```

#### Success Response — `200 OK`

Returns the updated job object.

---

### 7. Delete a Job *(Recruiter Only)*

| Field | Value |
|---|---|
| **Method** | `DELETE` |
| **URL** | `http://localhost:8080/api/jobs/{id}` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Required Role** | `RECRUITER` |

#### Success Response — `204 No Content`

---

### 8. Get All Jobs — List *(Internal Only)*

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8082/api/internal/jobs/all` |
| **Description** | Internal endpoint — not exposed via API Gateway. Used by Admin Service via Feign client. |

#### Success Response — `200 OK`

Returns a list of all jobs.

---

### 9. Delete Job by Admin *(Internal Only)*

| Field | Value |
|---|---|
| **Method** | `DELETE` |
| **URL** | `http://localhost:8082/api/internal/jobs/{id}` |
| **Description** | Internal endpoint — not exposed via API Gateway. Used by Admin Service. |

#### Success Response — `204 No Content`

---

## Postman Testing Workflow

1. **Login** as a `RECRUITER` via Auth Service → save the JWT token
2. **Create** a few jobs using endpoint #1
3. **Browse** jobs using endpoints #2, #3, #4 (can use any role)
4. **View** recruiter's own jobs using endpoint #5
5. **Update** a job using endpoint #6
6. **Delete** a job using endpoint #7
7. **Internal** endpoints (#8, #9) can be tested directly at port `8082`
