# MDOTM Pet Service (Spring Boot 3, Java 17)

Simple REST service to manage pets. Clean layered architecture with repository port/adapters (in‑memory and JPA/H2), validation, standardized error handling, pagination/sorting, and OpenAPI.

## Quickstart

Prereqs: JDK 17+. Uses Gradle Wrapper; no local Gradle required.

- Run tests:
  - Windows: `cmd /c "cd /d mdotm-pet-service && gradlew.bat test"`
- Start (default profile: inmemory):
  - Windows: `cmd /c "cd /d mdotm-pet-service && gradlew.bat bootRun"`
- Start with JPA/H2:
  - Windows: `cmd /c "cd /d mdotm-pet-service && gradlew.bat bootRun -Dspring.profiles.active=jpa"`
- Build JAR: `gradlew.bat clean build`

Swagger UI: http://localhost:8080/swagger-ui.html  |  OpenAPI JSON: http://localhost:8080/v3/api-docs

## Architecture (NoSQL-ready)

- domain: `Pet` model, `PetRepository` port, pagination (`PageRequest`, `PagedResult`) and `PetCriteria`.
- application: `PetService` orchestrates rules and uses the port.
- infrastructure: adapters
  - `inmemory` (default profile): `InMemoryPetRepository` for fast dev/tests.
  - `jpa` (profile=jpa): `PetJpaAdapter` + `PetEntity` + `PetJpaRepository` (H2 in-memory).
- interfaces: REST (controllers, DTOs, exception handler).

To switch to a non-relational DB, implement a new adapter for `PetRepository` and activate it via Spring profile; no changes to controller/service/domain required.

## Configuration

- Default profile: `inmemory` (see `application.properties`).
- JPA/H2 profile: `jpa` (see `application-jpa.properties`, H2 console at `/h2-console`).

## API

Base path: `/api/v1/pets`

- POST `/` → 201 Created + Location
  - Body: `{ "name": "Rex", "species": "Dog", "age": 3, "ownerName": "Alice" }`
- GET `/{id}` → 200 or 404
- PUT `/{id}` → 200 (full update)
- PATCH `/{id}` → 200 (partial update)
  - Body (any subset): `{ "name": "New name" }`
- DELETE `/{id}` → 204
- GET `/` (list) → 200 with page
  - Query: `page` (0..), `size` (>0), `sort` (repeatable, e.g. `sort=name,asc&sort=age,desc`), filters: `species`, `name` (contains)

Example list:
```
curl "http://localhost:8080/api/v1/pets?page=0&size=10&sort=name,asc&species=Dog&name=re"
```

## Error handling

Errors use RFC 9457 `application/problem+json` with consistent fields (title, status, detail, type) and validation errors grouped per field.
