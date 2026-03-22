# JPMS - Eureka Server

## Overview

The **Eureka Server** is the service discovery component for the Job Portal Management System. All microservices register themselves with this server, enabling dynamic service-to-service communication via Spring Cloud Netflix Eureka.

| Property | Value |
|---|---|
| **Port** | `8761` |
| **Spring App Name** | `JPMS-EurekaServer` |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- No database required

---

## Running the Service

```bash
cd JPMS-EurekaServer
mvn spring-boot:run
```

> **Note:** Start the Eureka Server **first** before launching any other microservice.

---

## Verifying with Postman / Browser

### 1. Eureka Dashboard

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8761` |
| **Description** | Opens the Eureka dashboard (browser recommended). Shows all registered service instances. |

### 2. Registered Applications (REST API)

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8761/eureka/apps` |
| **Headers** | `Accept: application/json` |
| **Description** | Returns a JSON list of all registered microservices and their metadata. |

#### Sample Response

```json
{
  "applications": {
    "application": [
      {
        "name": "AUTH-SERVICE",
        "instance": [
          {
            "hostName": "localhost",
            "port": { "$": 8081, "@enabled": "true" },
            "status": "UP"
          }
        ]
      }
    ]
  }
}
```

### 3. Specific Application Info

| Field | Value |
|---|---|
| **Method** | `GET` |
| **URL** | `http://localhost:8761/eureka/apps/{SERVICE-NAME}` |
| **Headers** | `Accept: application/json` |
| **Description** | Returns details for a specific registered service. |

**Example URLs:**

- `http://localhost:8761/eureka/apps/AUTH-SERVICE`
- `http://localhost:8761/eureka/apps/JOB-SERVICE`
- `http://localhost:8761/eureka/apps/APPLICATION-SERVICE`
- `http://localhost:8761/eureka/apps/ADMIN-SERVICE`
- `http://localhost:8761/eureka/apps/API-GATEWAY`

---

## Postman Setup Tips

1. Create a new Postman **Collection** called `JPMS - Eureka Server`
2. Add the requests above as individual requests
3. Set the `Accept` header to `application/json` for REST endpoints
4. No authentication is required for Eureka endpoints
