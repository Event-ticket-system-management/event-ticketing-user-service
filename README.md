# User Service - Architecture

## Overview
The User Service handles authentication, authorization, and user profile management for the event ticketing system.

## Responsibilities
- User Registration & Authentication
- Password Hashing (BCrypt)
- JWT Token Generation & Validation
- Role-based Access Control (USER, ORGANIZER, ADMIN)

## Tech Stack
- Java 21 / Spring Boot 3.x
- Spring Security
- Spring Data JPA
- PostgreSQL (`user_db`)
