# LearningFlashcardApp

A full-stack flashcard learning web application where users can create, manage, and study flashcard sets.

## Tech Stack

- **Backend:** Java 17+, Spring Boot 3, Spring Data JPA, Spring Security, SQLite, JWT
- **Frontend:** React, TypeScript, Vite, React Router

## Project Structure

```
LearningFllashcardApp/
├── backend/                          # Spring Boot API
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/flashlearn/app/
│       │   ├── controller/           # REST controllers
│       │   ├── service/              # Business logic
│       │   ├── repository/           # Spring Data JPA
│       │   ├── model/entity/         # JPA entities
│       │   ├── model/dto/            # Request/response DTOs
│       │   ├── config/               # Security, CORS, seed data
│       │   ├── security/             # JWT filter & utilities
│       │   └── exception/            # Error handling
│       ├── main/resources/
│       │   └── application.properties
│       └── test/java/                # Integration tests
└── frontend/
    └── src/
        ├── components/
        ├── pages/
        ├── services/
        └── models/
```

## Prerequisites

- **Java 17 or higher** (Spring Boot 3 requirement)
- **Apache Maven 3.9+**
- **Node.js 18+** (frontend only)

## Getting Started

### Backend

```bash
cd backend
mvn spring-boot:run
```

API runs at `http://localhost:3001`

On first startup, demo data is seeded automatically (admin, demo user, sample flashcard set).

### Frontend

```bash
cd frontend
npm install
npm run dev
```

App runs at `http://localhost:5173`

## Demo Accounts

| Role  | Email                 | Password  |
|-------|-----------------------|-----------|
| Admin | admin@flashcard.app   | admin123  |
| User  | user@flashcard.app    | user123   |

## API Endpoints

All routes are prefixed with `/api`.

| Method | Endpoint                    | Access        |
|--------|-----------------------------|---------------|
| POST   | /api/auth/register          | Public        |
| POST   | /api/auth/login             | Public        |
| GET    | /api/sets                   | Public        |
| GET    | /api/sets/mine              | Authenticated |
| GET    | /api/sets/:id               | Public*       |
| POST   | /api/sets                   | Authenticated |
| PUT    | /api/sets/:id               | Owner         |
| DELETE | /api/sets/:id               | Owner         |
| POST   | /api/sets/:id/cards         | Owner         |
| PUT    | /api/cards/:id              | Owner         |
| DELETE | /api/cards/:id              | Owner         |
| POST   | /api/learning/start/:setId  | Authenticated |
| POST   | /api/learning/result        | Authenticated |
| GET    | /api/statistics/user/:id    | Owner/Admin   |
| GET    | /api/admin/users            | Admin         |
| DELETE | /api/admin/users/:id        | Admin         |
| GET    | /api/admin/sets             | Admin         |
| DELETE | /api/admin/sets/:id         | Admin         |

\* Private sets require ownership.

## Running Tests

```bash
cd backend
mvn test
```

## Configuration

Key settings in `backend/src/main/resources/application.properties`:

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `3001` | API port |
| `spring.datasource.url` | `jdbc:sqlite:./dev.db` | SQLite database file |
| `app.jwt.secret` | (dev default) | JWT signing secret |
| `app.jwt.expiration-ms` | `604800000` | Token TTL (7 days) |

## Features

- Guest browsing and search of public flashcard sets
- User registration and login with BCrypt password hashing
- CRUD for flashcard sets and cards (owner-only)
- Learning sessions with "I know" / "I don't know" tracking
- Personal learning statistics and progress history
- Admin user and set management
