# JPMS - API Gateway

## Overview

The **API Gateway** is the single entry point for all client requests in the Job Portal Management System. It routes requests to the appropriate microservice, validates JWT tokens, and injects user identity headers (`X-User-Email`, `X-User-Role`) into downstream requests.

| Property | Value |
|---|---|
| **Port** | `8080` |
| **Spring App Name** | `API-GATEWAY` |
| **Type** | Reactive (WebFlux) |

---

## Route Mapping

| Route | Target Service | Path Pattern |
|---|---|---|
| Auth Service | `AUTH-SERVICE` | `/api/auth/**` |
| Job Service | `JOB-SERVICE` | `/api/jobs/**` |
| Application Service | `APPLICATION-SERVICE` | `/api/applications/**` |
| Admin Service | `ADMIN-SERVICE` | `/api/admin/**` |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Eureka Server must be running on `localhost:8761`
- No database required

---

## Running the Service

```bash
cd JPMS-ApiGateWay
mvn spring-boot:run
```

> **Note:** Start the Eureka Server first, then the API Gateway, and then the other services.

---

## Testing with Postman (via Gateway)

All requests below go through the API Gateway at `http://localhost:8080`. The Gateway validates the JWT and injects headers automatically.

### 1. Health Check — Auth Service (Public)

| Field | Value |
|---|---|
| **Method** | `POST` |
| **URL** | `http://localhost:8080/api/auth/register` |
| **Description** | Public endpoint — no JWT required. If routed correctly, returns a response from Auth Service. |

### 2. Authenticated Request — Job Service

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/jobs` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Description** | Lists all jobs. Gateway validates the JWT and forwards the request. |

### 3. Authenticated Request — Application Service

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/applications/my` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Description** | Lists the logged-in user's applications. JWT must belong to a `JOB_SEEKER`. |

### 4. Authenticated Request — Admin Service

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8080/api/admin/reports` |
| **Headers** | `Authorization: Bearer <JWT_TOKEN>` |
| **Description** | Platform report. JWT must belong to an `ADMIN`. |

---

## How JWT Authentication Works

1. Client sends `Authorization: Bearer <token>` header
2. The Gateway's `JwtAuthFilter` validates the token
3. On success, the Gateway injects:
   - `X-User-Email` — extracted from JWT claims
   - `X-User-Role` — extracted from JWT claims
4. Downstream services read these headers to identify the user (no re-validation needed)
5. `/api/auth/**` routes are **public** and skip JWT validation

---

## Postman Setup Tips

1. Create a Postman **Environment** with variables:
   - `BASE_URL` = `http://localhost:8080`
   - `JWT_TOKEN` = *(paste token from /api/auth/login response)*
2. In the **Authorization** tab of each request, set:
   - Type: `Bearer Token`
   - Token: `{{JWT_TOKEN}}`
3. All requests to the gateway should use port **8080**, not individual service ports
