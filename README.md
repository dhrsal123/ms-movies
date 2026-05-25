# Movie Management API

Microservice designed to handle the core catalog of the cinema ecosystem. It manages the lifecycle of movies, genres,
actors, directors, and rich media assets like trailers or posters.

## Key Features

* Movie Catalog Management: Operations to create, update, retrieve, and delete movies with robust validation limits on
  strings and array relationships.
* Genre Management: Categorize movies into specific genres and fetch all movies linked to a given genre identifier.
* Cast & Crew Directory: Detailed management of actors and directors, including birthdates and cloud-hosted profile
  picture references.
* Rich Media Attachments: Support for appending multiple media assets (Trailers, Teasers, Images) to a single movie with
  customizable display ordering.
* Reactive Architecture: Built using non-blocking reactive streams (`Mono` / `Flux`) for high-throughput performance.
* Role-Based Security: Administrative operations (`POST`, `PUT`, `DELETE`) are tightly restricted to accounts possessing
  the `EMPLOYEE` role via token validation.
* Standardized Error Responses: Automatically formats business, database, and validation exceptions into a predictable
  `BaseErrorDto` payload.

## Prerequisites

* Java 21
* Docker & Docker Compose
* Keycloak

## Environment configuration

Create a `.env` file in the root directory based on the example below:

```bash
# --- Redis Config ---
REDIS_HOST=localhost
REDIS_PASSWORD=YOUR_REDIS_PASSWORD

# --- DB Config ---
POSTGRES_DB=cinema_db
POSTGRES_HOST=localhost
POSTGRES_PASSWORD=YOUR_POSTGRES_PASSWORD
POSTGRES_USER=postgres

# --- Keycloak ---
KEYCLOAK_HOST=localhost

# --- LOKI ---
LOKI_HOST=localhost
```

---

## API Documentation

Once the service is running, you can explore the full API specification:

* Swagger UI: `http://localhost:8082/swagger-ui.html`
* OpenAPI Spec: `http://localhost:8082/v3/api-docs`

---

## Usage

### Role-Based Access Control

This service distinguishes between public information and management operations. Management endpoints require a valid
JWT with the `Manager` or `Employee`  authority.

#### Endpoint Examples

| Category  | Method   | Path                             | Required Role |
|:----------|:---------|:---------------------------------|:--------------|
| Movies	   | GET	     | /api/v1/movies	                  | None (Public) 
| Movies	   | POST     | 	/api/v1/movies                  | 	EMPLOYEE     
| Movies    | 	PUT	    | /api/v1/movies/{movieId}         | 	EMPLOYEE     
| Genres    | 	GET	    | /api/v1/genres/{genreId}/movies	 | None(Public)  
| Actors    | 	DELETE	 | /api/v1/actors/{actorId}         | 	EMPLOYEE     
| Directors | 	POST	   | /api/v1/directors                | 	EMPLOYEE     

### Supported Movie Media Types

When appending items to a movie's media array list inside the MovieRequestDto, items must be bound to one of the
following strict enumeration types:

* `TRAILER`, `TEASER`, `GALLERY_IMAGE`

---

## Error Handling

The service returns a standardized `BaseErrorDto` for all business and technical exceptions:

```json
{
  "message": "The requested theater, room, or seat could not be found.",
  "code": "CINEMA-001",
  "status": "BAD_REQUEST"
}
```

### Common Error Codes

* `CINEMA-001`: Resource Not Found / Validation Fail
* `CINEMA-003`: Data Conflict
* `CINEMA-004`: Forbidden (Missing Roles)
* `CINEMA-005`: Unauthorized (Expired or Missing Token)