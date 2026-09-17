# Employee Access Management API

Backend REST API untuk mengelola permintaan akses karyawan terhadap berbagai resource perusahaan, dengan dukungan **multi-role authentication**, **two-level approval workflow**, access catalog, dan dashboard summary metrics.

Project ini dibuat sebagai backend-only service menggunakan Java dan Spring Boot dengan PostgreSQL sebagai database.

---

## 1. Project Overview

### 1.1 Objective

Application menyediakan RESTful API untuk:

* Authentication menggunakan username/email dan password.
* Authorization berdasarkan role.
* Menampilkan access catalog.
* Membuat access request.
* Melihat request milik user yang sedang login.
* Manager melakukan approval/rejection terhadap request direct reports.
* Admin melakukan final approval/rejection.
* Menyediakan dashboard metrics berupa aggregated request statistics.

Requirement utama assessment mencakup multi-role authentication, access request management, two-level approval workflow, dan complex data querying untuk summary metrics.

### 1.2 User Roles

| Role      | Responsibility                                            |
| --------- | --------------------------------------------------------- |
| `USER`    | Submit access request dan melihat request history sendiri |
| `MANAGER` | Approve/reject request dari direct reports                |
| `ADMIN`   | Final approve/reject request yang sudah disetujui Manager |

Manager bukan entity user yang terpisah. Manager merupakan user biasa yang mempunyai role `MANAGER` dan mempunyai direct reports melalui `manager_id`.

User dengan role `MANAGER` atau `ADMIN` juga dapat melakukan aktivitas sebagai standard user, termasuk membuat access request sendiri.

---

# 2. Architecture Design

Project menggunakan layered architecture untuk memisahkan HTTP handling, business logic, persistence, dan security.

```text
                    CLIENT
                      |
                      | HTTP / JSON
                      v
             +-------------------+
             |    Controller     |
             +-------------------+
                      |
                      v
             +-------------------+
             |     Service       |
             | Business Logic    |
             +-------------------+
                      |
                      v
             +-------------------+
             |    Repository     |
             |   Spring Data JPA |
             +-------------------+
                      |
                      v
             +-------------------+
             |    PostgreSQL     |
             +-------------------+


             SECURITY FLOW
                    |
                    v
             +-------------------+
             | Spring Security   |
             | JWT Filter        |
             +-------------------+
                    |
                    v
             Authentication
                    |
                    v
             Authorization
                    |
                    v
             Controller
```

---

# 3. Database ERD

```text
+----------------------+
|       roles          |
+----------------------+
| PK id                |
|    name              |
+----------+-----------+
           |
           | 1
           |
           | N
+----------v-----------+
|        users         |
+----------------------+
| PK id                |
|    username          |
|    email             |
|    password          |
| FK manager_id -------+------+
|    enabled           |      |
|    created_at        |      |
|    updated_at        |      |
+----------+-----------+      |
           |                  |
           |                  |
           | self-reference   |
           +------------------+


+----------------------+
|   access             |
+----------------------+
| PK id                |
|    code              |
|    name              |
|    description       |
+----------+-----------+
           |
           | 1
           |
           | N
+----------v-----------+
|   access_requests    |
+----------------------+
| PK id                |
| FK user_id           |
| FK access_id         |
|    reason            |
|    status            |
|    created_at        |
|    updated_at        |
+----------+-----------+
           |
           | 1
           |
           | N
+----------v-----------+
| approval_actions   |
+----------------------+
| PK id                |
| FK request_id        |
| FK approver_id       |
|    action            |
|    notes             |
|    created_at        |
+----------------------+
```
# 4. Project Prerequisites

Pastikan environment memiliki:

```text
Java 21+
Maven 3.9+
Git
```

Verify:

```bash
java -version
```

```bash
mvn -version
```

---

# 5. Clone Repository

```bash
git clone <repository-url>
```

Masuk ke project:

```bash
cd employee-access-management
```

# 6. Configure Application

Periksa:

```text
src/main/resources/application.yml
```

Contoh:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/employee_access_management
    username: postgres
    password: postgres

  jpa:
    hibernate:
      ddl-auto: validate

  flyway:
    enabled: true
    locations: classpath:db/migration
```

Sesuaikan username/password/database dengan `Aplikasi Postgres`.

---

# 7. Start Application

Dengan Maven:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

Atau:

```bash
mvn spring-boot:run
```

Application akan berjalan pada:

```text
http://localhost:8080
```

# 8. Test API

## Step 1 — Login User

```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "user01",
  "password": "password"
}
```

Save JWT response.

---

## Step 2 — Get Access Catalog

```http
GET http://localhost:8080/api/v1/accesses
Authorization: Bearer <USER_TOKEN>
```

---

## Step 3 — Create Access Request

```http
POST http://localhost:8080/api/v1/requests
Authorization: Bearer <USER_TOKEN>
Content-Type: application/json
```

Body:

```json
{
  "access_id": 1,
  "reason": "Need VPN access for development"
}
```

Expected initial state:

```text
PENDING_MANAGER
```

---

## Step 4 — View My Requests

```http
GET http://localhost:8080/api/v1/requests/me
Authorization: Bearer <USER_TOKEN>
```

---

## Step 5 — Login Manager

```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "manager01",
  "password": "password"
}
```

---

## Step 6 — Manager Approval Inbox

```http
GET http://localhost:8080/api/v1/approvals
Authorization: Bearer <MANAGER_TOKEN>
```

`user01` request harus muncul karena:

```text
user01.manager_id = manager01.id
```

dan request berada pada:

```text
PENDING_MANAGER
```

---

## Step 7 — Manager Approves

```http
PATCH http://localhost:8080/api/v1/approvals/{request_id}/action
Authorization: Bearer <MANAGER_TOKEN>
Content-Type: application/json
```

Body:

```json
{
  "action": "APPROVE",
  "notes": "Approved by manager"
}
```

Expected:

```text
PENDING_ADMIN
```

---

## Step 8 — Login Admin

```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "admin01",
  "password": "password"
}
```

---

## Step 9 — Admin Approval Inbox

```http
GET http://localhost:8080/api/v1/approvals
Authorization: Bearer <ADMIN_TOKEN>
```

Request dengan:

```text
PENDING_ADMIN
```

harus muncul.

---

## Step 10 — Admin Final Approval

```http
PATCH http://localhost:8080/api/v1/approvals/{request_id}/action
Authorization: Bearer <ADMIN_TOKEN>
Content-Type: application/json
```

Body:

```json
{
  "action": "APPROVE",
  "notes": "Final approval"
}
```

Expected:

```text
APPROVED
```

---

# Step 11. Test Dashboard

```http
GET http://localhost:8080/api/v1/dashboard/metrics
Authorization: Bearer <ADMIN_TOKEN>
```

Example:

```json
{
  "total_requests": 1,
  "in_progress": 0,
  "waiting_manager": 0,
  "waiting_admin": 0,
  "approved": 1,
  "rejected": 0
}
```

---

# 9. API Summary

| Method  | Endpoint                        | Scope         |
| ------- | ------------------------------- | ------------- |
| `POST`  | `/api/v1/auth/login`            | Public        |
| `GET`   | `/api/v1/accesses`              | Authenticated |
| `POST`  | `/api/v1/requests`              | Authenticated |
| `GET`   | `/api/v1/requests/me`           | Authenticated |
| `GET`   | `/api/v1/approvals`             | Manager/Admin |
| `PATCH` | `/api/v1/approvals/{id}/action` | Manager/Admin |
| `GET`   | `/api/v1/dashboard/metrics`     | Authenticated |

---

# 10. Security Rules

JWT diperlukan untuk protected endpoints.

Format:

```http
Authorization: Bearer <JWT>
```

Authorization rules:

### USER

Dapat:

```text
Create own request
View own requests
```

### MANAGER

Dapat:

```text
Create own request
View own requests
View direct-report pending approvals
Approve/reject direct-report requests
```

### ADMIN

Dapat:

```text
Create own request
View own requests
View PENDING_ADMIN requests
Approve/reject final-stage requests
```

Manager tidak dapat memproses request dari user yang bukan direct report.

Request yang sudah:

```text
APPROVED
```

atau:

```text
REJECTED
```

tidak dapat diproses kembali.

---

# 11. Error Handling

API menggunakan centralized exception handling melalui:

```text
GlobalExceptionHandler
```

Contoh response error:

```json
{
  "status": 400,
  "message": "Invalid request"
}
```

HTTP status digunakan sesuai kondisi:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
500 Internal Server Error
```

The project is ready for API testing using Postman.
