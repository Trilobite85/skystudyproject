# Movie rating service

A Spring Boot-based REST API for managing movies, featuring JWT authentication, role-based authorization, and Docker Compose deployment.

## Exposed REST Endpoints

- `POST /register` — Register a new user
- `DELETE /register/{id}` — Delete a user by ID
- `POST /auth/login` — Authenticate and receive a JWT token
- `POST /auth/logout` — Logout by adding a JWT token to blacklist
- `GET /movies` — List all movies
- `GET /movies/{id}` — Get details for a specific movie
- `GET /movies/top-rated` — List top-rated movies
- `GET /movies/{id}/ratings` — Get ratings for a movie (requires authentication)
- `POST /movies/{id}/ratings` — Add or update a rating for a movie (requires authentication)
- `DELETE /movies/{id}/ratings` — Delete user's rating for a movie (requires authentication)
- `POST /movies` — Add or update a movie (**ADMIN only**)
- `PUT /movies/{id}` — Update a movie (**ADMIN only**)
- `DELETE /movies/{id}` — Delete a movie (**ADMIN only**)

## Authentication

- Uses JWT (JSON Web Token) for stateless authentication.
- Obtain a token via `POST /auth/login` and include it in the `Authorization: Bearer <token>` header for protected endpoints.

## Authorization

- Role-based access control:
  - `POST` and `PUT` on `/movies` endpoints require the `ADMIN` role.
  - `DELETE /movies/{id}` requires authentication.
  - `GET /movies` and `GET /movies/{id}` are public.
- User roles are included in the JWT and enforced via Spring Security.

## Rollout Steps for Docker Compose

1. **Build the JAR:**
   ```sh
   mvn clean package
   ```
2. **Build and start with Docker Compose:**
   ```sh
   docker-compose up --build
   ```
3. **Access the API:**
   - The app will be available at `http://localhost:8080`.

## Java and Spring Versions

- **Java:** 20
- **Spring Boot:** 3.3.3
