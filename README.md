# Event Ticketing - User Service

[![CI Pipeline](https://github.com/YOUR_GITHUB_USERNAME/event-ticketing-user-service/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_GITHUB_USERNAME/event-ticketing-user-service/actions)

## 📌 Overview
`event-ticketing-user-service` is the core microservice responsible for User Authentication, Authorization, and User Profile Management within the Event Ticketing System.

---

## 🏗️ Service Responsibilities
- User registration and account profile management.
- Password hashing using **BCrypt**.
- Authentication & JWT token issuance/validation using **Spring Security**.
- Role-Based Access Control (**RBAC**): `USER`, `ORGANIZER`, `ADMIN`.

---

## 🛠️ Tech Stack & Configuration

| Component | Technology / Detail |
| :--- | :--- |
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **Security** | Spring Security |
| **Persistence** | Spring Data JPA (Hibernate) |
| **Database** | PostgreSQL (`user_db`) |
| **Build Tool** | Maven (`pom.xml`) |
| **Server Port** | `8081` |
| **Boilerplate Reduction** | Lombok |

---

## 📐 Service Architecture

```mermaid
graph TD
    Client[API Gateway / Client] -->|HTTP / REST Port 8081| Controller[User Controller]
    Controller --> Service[User Service]
    Service --> Security[Spring Security / BCrypt / JWT]
    Service --> Repo[User Repository]
    Repo --> DB[(PostgreSQL: user_db)]
