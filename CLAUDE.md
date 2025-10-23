# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Geoevent-api is a Scala-based REST API for a geolocation-based event management system. It uses Apache Pekko HTTP (successor to Akka HTTP) with PostgreSQL, built with functional programming patterns via Doobie.

## Common Commands

### Development
```bash
sbt compile              # Compile the project
sbt test                 # Run all tests
sbt run                  # Start server on port 8081
sbt "testOnly *UserRoutesTest"  # Run a single test class
```

### Docker
```bash
docker-compose up        # Start API and PostgreSQL containers
docker-compose down      # Stop containers
./run.sh                 # Alternative startup script
./shutdown.sh            # Alternative shutdown script
```

### Docker Network Configuration
The docker-compose setup creates a custom network (192.168.10.0/24):
- API: 192.168.10.11:8081
- PostgreSQL: 192.168.10.12:5432

## Architecture

### Layered Architecture Pattern

**1. Routes Layer** (`routes/`)
- Defines RESTful endpoints using Pekko HTTP DSL
- Applies authorization via `Authorization.authorizeToken` directive
- Handles request/response marshalling with Spray JSON
- Key files: `UserCalls.scala`, `AuthCalls.scala`, `GeoEventRoutes.scala`, `GeoStampCalls.scala`, `ChatMessageRoutes.scala`

**2. Registry Layer** (`registies/`)
- Implements `RegistryCalls[A]` trait for generic CRUD operations
- Each registry extends `DbConnection` for database access
- Uses Doobie's functional IO operations with `.transact(transactor).unsafeRunSync()`
- Pattern: Each domain entity has its own registry handling all DB operations

**3. Models Layer** (`models/`)
- Case classes for domain entities
- Spray JSON implicit formats in `JsonImplicitFormats.scala`
- Custom formatters for `Timestamp` and `Option` types
- Response wrappers: `SuccessResponse`, `ErrorResponse`

**4. Database Layer** (`database/`)
- `DbConnection.scala` provides Doobie Transactor for PostgreSQL
- Flyway migrations run automatically on application startup
- Migration files in `src/main/resources/db/migration/`

### Authentication Flow

1. User registers via `POST /users` (password hashed with bcrypt)
2. User authenticates via `POST /authorize` with credentials
3. System checks for existing valid token (24-hour window)
4. Returns existing token or creates new one
5. Protected endpoints require Bearer token in Authorization header
6. `Authorization.authorizeToken` directive validates token and extracts userId

### Data Model Relationships

```
User ──┬─→ ChatMessage (one-to-many)
       ├─→ GeoEvent (one-to-many)
       └─→ GeoStamp (one-to-many)

GeoEvent ──→ ChatMessage (linked via eventId)
GeoStamp ──→ GeoEvent (optional link via geoEventId)

Auth: Tracks valid tokens per user with 24-hour expiry
```

### Key Architectural Decisions

- **Functional Database Access**: Doobie provides type-safe SQL with IO monads
- **Synchronous Route Handlers**: Uses `unsafeRunSync()` for synchronous execution within routes
- **Token-Based Auth**: Bearer tokens with 24-hour expiry, tokens can be reused within window
- **Flyway Migrations**: Version-controlled schema evolution (V1.1 through V1.5)
- **Registry Pattern**: Abstracts database operations, enables testability

## Testing

### Test Structure
All tests extend `TestFrame.scala` which provides:
- Route setup with ActorSystem and test configuration
- Pre-created test user for authentication
- `beforeAll`/`afterAll` hooks for setup/teardown
- Pekko's `ScalatestRouteTest` for HTTP testing

### Test Approach
- Integration tests exercising full route handlers
- Uses `Marshal().to[MessageEntity]` for request bodies
- Tests authorization with Bearer tokens
- Validates status codes and content types

### Running Tests
```bash
sbt test                           # All tests
sbt "testOnly *UserRoutesTest"     # Single test suite
```

## Database Migrations

Migrations are in `src/main/resources/db/migration/`:
- V1_1: users table
- V1_2: auth (authorization tokens)
- V1_3: geo_stamps
- V1_4: geo_events
- V1_5: chat_messages

Flyway runs automatically on application startup via `QuickstartApp.scala`.

## Application Entry Point

`QuickstartApp.scala`:
1. Initializes Pekko ActorSystem
2. Runs Flyway database migrations
3. Composes all route handlers
4. Binds HTTP server to 0.0.0.0:8081

## API Specification

OpenAPI 3.0 specification available in `openapi.json`.

## Technology Stack

- **Scala**: 2.12.20
- **Build Tool**: SBT 1.9.9
- **Web Framework**: Apache Pekko HTTP 1.1.0
- **Actor System**: Apache Pekko 1.1.2 (typed)
- **Database**: PostgreSQL 14.1
- **DB Access**: Doobie 1.0.0-RC1
- **Migrations**: Flyway 8.5.13
- **JSON**: Spray JSON
- **Auth**: Bcrypt (scala-bcrypt 4.3.0)
- **Testing**: ScalaTest 3.2.19 with Pekko HTTP TestKit

## CI/CD

GitHub Actions workflows:
- `scala.yml`: Runs tests on every push/PR to main
- `ci-cd.yml`: Tests + Docker build/push on main branch pushes
