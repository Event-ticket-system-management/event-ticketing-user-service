# User Service - Architecture

## Overview
User Service එක Event Ticketing System එකේ Authentication, Authorization, සහ User Profile Management භාරව කටයුතු කරයි.

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
