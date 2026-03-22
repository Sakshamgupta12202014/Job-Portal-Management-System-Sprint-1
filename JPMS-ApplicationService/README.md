# JPMS - Application Service

## Overview

The **Application Service** manages job applications in the Job Portal Management System. Job seekers can apply for jobs and view their applications, while recruiters can view applications for their posted jobs and update application statuses.

| Property | Value |
|---|---|
| **Port** | `8083` |
| **Spring App Name** | `APPLICATION-SERVICE` |
| **Database** | MySQL — `application_db` |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL running with database `application_db` created
- Eureka Server running on `localhost:8761`
- Auth Service running (to obtain JWT tokens)
- Job Service running (applications reference job IDs)

---

## Running the Service

```bash
cd JPMS-ApplicationService
mvn spring-boot:run
```

---

## API Endpoints

### Base URL

| Access | URL |
|---|---|
| **Direct** | `http://localhost:8083` |
| **Via Gateway** | `http://localhost:8080` |

> All endpoints require JWT authentication via the Gateway (except internal endpoints).

---

### 1. Apply for a Job *(Job Seeker Only)*

| Field | Value |
|---|---|
| **Method** | `POST` |
| **URL** | `http://localhost:8080/api/applications` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Content-Type** | `application/json` |
| **Required Role** | `JOB_SEEKER` |

#### Request Body

```json
{
  "jobId": 1,
  "resumeUrl": "https://example.com/resumes/john-doe.pdf"
}
```

> `jobId` is **required**. `resumeUrl` is optional.

#### Success Response — `201 Created`

```json
{
  "id": 1,
  "applicantEmail": "john@example.com",
  "jobId": 1,
  "resumeUrl": "https://example.com/resumes/john-doe.pdf",
  "status": "PENDING",
  "appliedAt": "2026-03-22T11:00:00"
}
```

#### Error Response — `403 Forbidden`

Returned if the user's role is not `JOB_SEEKER`.

---

### 2. Get My Applications *(Job Seeker Only)*

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/applications/my` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Required Role** | `JOB_SEEKER` |

#### Success Response — `200 OK`

```json
[
  {
    "id": 1,
    "applicantEmail": "john@example.com",
    "jobId": 1,
    "resumeUrl": "https://example.com/resumes/john-doe.pdf",
    "status": "PENDING",
    "appliedAt": "2026-03-22T11:00:00"
  }
]
```

---

### 3. Get Applications for a Job *(Recruiter Only)*

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/applications/job/{jobId}` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Required Role** | `RECRUITER` |

#### Example

`GET http://localhost:8080/api/applications/job/1`

#### Success Response — `200 OK`

```json
[
  {
    "id": 1,
    "applicantEmail": "john@example.com",
    "jobId": 1,
    "resumeUrl": "https://example.com/resumes/john-doe.pdf",
    "status": "PENDING",
    "appliedAt": "2026-03-22T11:00:00"
  }
]
```

---

### 4. Update Application Status *(Recruiter Only)*

| Field | Value |
|---|---|
| **Method** | `PATCH` |
| **URL** | `http://localhost:8080/api/applications/{id}/status` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Content-Type** | `application/json` |
| **Required Role** | `RECRUITER` |

#### Request Body

```json
{
  "status": "SHORTLISTED"
}
```

> **Possible statuses:** `PENDING`, `SHORTLISTED`, `REJECTED`, `ACCEPTED`

#### Success Response — `200 OK`

```json
{
  "id": 1,
  "applicantEmail": "john@example.com",
  "jobId": 1,
  "resumeUrl": "https://example.com/resumes/john-doe.pdf",
  "status": "SHORTLISTED",
  "appliedAt": "2026-03-22T11:00:00"
}
```

---

### 5. Get Application Stats *(Internal Only)*

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8083/api/internal/applications/stats` |
| **Description** | Internal endpoint — not exposed via API Gateway. Used by Admin Service via Feign client. |

#### Success Response — `200 OK`

```json
{
  "totalApplications": 25,
  "pendingCount": 10,
  "shortlistedCount": 8,
  "rejectedCount": 5,
  "acceptedCount": 2
}
```

---

## Postman Testing Workflow

1. **Login** as a `JOB_SEEKER` via Auth Service → save the JWT token
2. **Apply** for a job using endpoint #1 (ensure a job exists in Job Service)
3. **View** your applications using endpoint #2
4. **Switch** to a `RECRUITER` JWT token
5. **View** applications for a specific job using endpoint #3
6. **Update** an application's status using endpoint #4
7. **Internal** endpoint (#5) can be tested directly at port `8083`
