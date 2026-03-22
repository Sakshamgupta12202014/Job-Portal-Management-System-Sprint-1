# Job Portal Management System (JPMS)
## Spring Boot Microservices | Java 17 | Spring Cloud 2023.0.1

---

## Services & Ports

| Service                      | Port | Eureka Name          | Description                              |
|------------------------------|------|----------------------|------------------------------------------|
| JobPortal-EurekaServer       | 8761 | EUREKA-SERVER        | Service registry — start this FIRST      |
| JobPortal-ApiGateway         | 8989 | API-GATEWAY          | Single entry point, JWT validation       |
| JobPortal-AuthService        | 8081 | AUTH-SERVICE         | Register, login, refresh, logout         |
| JobPortal-JobService         | 8082 | JOB-SERVICE          | Job CRUD, search, pagination             |
| JobPortal-ApplicationService | 8083 | APPLICATION-SERVICE  | Apply, track, status updates             |
| JobPortal-AdminService       | 8084 | ADMIN-SERVICE        | User mgmt, job monitoring, reports       |

---

## Prerequisites
- Java 17
- Maven 3.8+
- MySQL 8.x running on port 3306

---

## Step 1 — Database Setup
```bash
mysql -u root -p < database-setup.sql
```
Creates: jobportal_auth_db, jobportal_job_db, jobportal_app_db
Hibernate auto-creates tables on first startup (ddl-auto=update).

---

## Step 2 — Start Services (Order Matters)
```
1. JobPortal-EurekaServer   →  mvn spring-boot:run  (check http://localhost:8761)
2. JobPortal-ApiGateway     →  mvn spring-boot:run
3. JobPortal-AuthService    →  mvn spring-boot:run
4. JobPortal-JobService     →  mvn spring-boot:run
5. JobPortal-ApplicationService → mvn spring-boot:run
6. JobPortal-AdminService   →  mvn spring-boot:run
```

IMPORTANT: Always test via port 8989 (Gateway), never directly on service ports.

---

## Quick Postman Test Flow

### 1. Register Seeker
POST http://localhost:8989/api/auth/register
{"name":"John","email":"john@test.com","password":"pass1234","role":"JOB_SEEKER"}
→ 201 + tokens

### 2. Register Recruiter
POST http://localhost:8989/api/auth/register
{"name":"Jane","email":"jane@test.com","password":"pass1234","role":"RECRUITER"}
→ 201 + tokens

### 3. Login
POST http://localhost:8989/api/auth/login
{"email":"jane@test.com","password":"pass1234"}
→ 200 + accessToken (copy this)

### 4. Post a Job (RECRUITER)
POST http://localhost:8989/api/jobs
Authorization: Bearer <RECRUITER_accessToken>
{"title":"Java Dev","companyName":"TechCorp","location":"Bangalore","jobType":"FULL_TIME","description":"Spring Boot role"}
→ 201

### 5. Apply (JOB_SEEKER)
POST http://localhost:8989/api/applications  (multipart/form-data)
Authorization: Bearer <SEEKER_accessToken>
jobId=1, resume=<file.pdf>
→ 201

### 6. Update Status (RECRUITER)
PATCH http://localhost:8989/api/applications/1/status
Authorization: Bearer <RECRUITER_accessToken>
{"newStatus":"UNDER_REVIEW"}
→ 200

### 7. Admin Report
GET http://localhost:8989/api/admin/reports
Authorization: Bearer <ADMIN_accessToken>
→ 200

---

## JWT Details
- Access token: 15 min | Refresh token: 7 days (UUID in DB)
- Gateway injects X-User-Id + X-User-Role headers after validation
- Downstream services trust headers, never re-validate JWT

## Swagger UI
- Auth:        http://localhost:8081/swagger-ui.html
- Job:         http://localhost:8082/swagger-ui.html
- Application: http://localhost:8083/swagger-ui.html
- Admin:       http://localhost:8084/swagger-ui.html

## Error Format (all services)
{"status":409,"error":"Conflict","message":"Already applied","timestamp":"..."}
