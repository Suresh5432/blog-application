# Blog Application

A full-stack blog application built with **Spring Boot**, **Spring MVC**, **Spring Data JPA**, **Hibernate**, **Spring Security**, **Thymeleaf**, and **PostgreSQL**.

The application allows users to register, log in, create and manage blog posts, add tags, and interact with posts through comments.

## Features

- User registration and login
- Password encryption using BCrypt
- Authentication and authorization using Spring Security
- Role-based authorization
- Create, read, update, and delete blog posts
- Publish and unpublish posts
- Search posts
- Filter posts
- Sort posts
- Pagination
- Tag management
- Many-to-many relationship between posts and tags
- Comments on posts
- User-specific functionality
- Admin-specific functionality
- Server-side rendering using Thymeleaf
- PostgreSQL database
- JPA/Hibernate ORM

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Programming language |
| Spring Boot | Application framework |
| Spring MVC | Web/MVC layer |
| Spring Data JPA | Data access |
| Hibernate | ORM |
| Spring Security | Authentication and authorization |
| Thymeleaf | Server-side HTML rendering |
| PostgreSQL | Database |
| Maven | Build and dependency management |
| Lombok | Boilerplate code reduction |

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── mountblue/
│   │           └── blogapplication/
│   │               ├── controller/
│   │               ├── service/
│   │               ├── repository/
│   │               ├── entity/
│   │               ├── dto/
│   │               ├── security/
│   │               └── BlogApplication.java
│   │
│   └── resources/
│       ├── static/
│       │   └── css/
│       ├── templates/
│       │   ├── fragments/
│       │   ├── Posts.html
│       │   ├── Register.html
│       │   ├── login.html
│       │   ├── create-post.html
│       │   └── post-details.html
│       └── application.properties
│
└── test/
