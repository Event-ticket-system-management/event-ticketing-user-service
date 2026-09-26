# Event Ticketing - User Service

[![CI Pipeline](https://github.com/Event-ticket-system-management/event-ticketing-user-service/actions/workflows/ci.yml/badge.svg)](https://github.com/Event-ticket-system-management/event-ticketing-user-service/actions)

## 📌 Overview
event-ticketing-user-service is the core microservice responsible for managing the User Management and Authentication context of the Event Ticketing System.

## 🏗️ Service Responsibilities
- User registration and login.
- Authentication & Authorization utilizing **Spring Security** and **JWT**.
- Password hashing and user profile context management.

## 🛠️ Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.x
- **Security:** Spring Security, JWT
- **Database:** PostgreSQL (`user_db`)
- **Containerization:** Docker

## 📐 Architecture
```mermaid
graph TD
    Client[API Gateway / Client] -->|HTTP / REST| Controller[User Controller]
    Controller --> Service[User Service]
    Service --> Security[Spring Security / JWT]
    Service --> Repo[User Repository]
    Repo -> DB[(PostgreSQL user_db)]
