# Web-Gara Project Rules

## Project Overview
Garage management system for the Vietnamese market. Backend-only REST API.

## Mandatory Tech Stack
- **Language:** Java 17+
- **Framework:** Spring Boot 3.x
- **Database:** MongoDB 7.x (Spring Data MongoDB)
- **Containerization:** Docker & Docker Compose
- **Security:** Spring Security + JWT (jjwt 0.12.x)
- **API Docs:** SpringDoc OpenAPI (Swagger UI)
- **Mapping:** MapStruct (entity ↔ DTO)
- **PDF:** OpenPDF
- **Testing:** JUnit 5, Mockito, Testcontainers

## Architecture

### Modular Monolith — Package Structure
```
com.webgara/
├── config/                  # Spring configs (Security, MongoDB, CORS, Swagger)
├── security/                # JWT filter, OTP service, CustomUserDetails
├── common/
│   ├── exception/           # GlobalExceptionHandler, custom exceptions
│   ├── dto/                 # ApiResponse<T>, PageResponse<T>
│   ├── model/               # BaseDocument (id, createdAt, updatedAt)
│   └── util/                # DateUtil, CurrencyUtil
├── module/
│   └── {module_name}/
│       ├── controller/      # @RestController
│       ├── service/         # Interface + Impl
│       ├── repository/      # @Repository extends MongoRepository
│       ├── model/           # @Document entities
│       ├── dto/             # Request/Response DTOs
│       └── mapper/          # @Mapper (MapStruct)
└── WebGaraApplication.java
```

### Modules
auth, user, vehicle, garage, service, appointment, repair, inventory, invoice, payment, review, notification, report

## Coding Conventions

### Naming
- **Packages:** lowercase, singular (`module.user`, NOT `module.users`)
- **Classes:** PascalCase — `UserService`, `AppointmentController`
- **Methods:** camelCase — `findByEmail()`, `createAppointment()`
- **Constants:** UPPER_SNAKE_CASE — `MAX_LOGIN_ATTEMPTS`
- **REST endpoints:** kebab-case — `/api/v1/repair-orders`
- **MongoDB collections:** snake_case, plural — `repair_orders`, `inventory_transactions`
- **DTOs:** suffix with `Request` / `Response` — `CreateAppointmentRequest`, `AppointmentResponse`

### Java Style
- Use `Lombok` annotations: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Use `@RequiredArgsConstructor` for constructor injection (NO `@Autowired` on fields)
- Always use interfaces for Service layer (`UserService` interface + `UserServiceImpl`)
- Use `@Valid` on all request body parameters
- Return `ApiResponse<T>` wrapper for ALL endpoints
- Use `Optional<T>` return from repositories, handle with `.orElseThrow()`

### REST API
- Base path: `/api/v1/`
- Use proper HTTP methods: GET (read), POST (create), PUT (full update), PATCH (partial update), DELETE
- Pagination: `?page=0&size=20&sort=createdAt,desc`
- Filtering: query params — `?status=PENDING&from=2026-01-01&to=2026-03-01`
- Response format:
```json
{
  "success": true,
  "message": "Thành công",
  "data": { ... },
  "timestamp": "2026-03-06T10:00:00"
}
```
- Error format:
```json
{
  "success": false,
  "message": "Lỗi mô tả",
  "errors": [{ "field": "email", "message": "Email không hợp lệ" }],
  "timestamp": "2026-03-06T10:00:00"
}
```

### MongoDB
- All documents extend `BaseDocument` (contains `id`, `createdAt`, `updatedAt`)
- Use `@Document(collection = "collection_name")` explicitly
- Use `@Indexed` for frequently queried fields
- Use `@DBRef` sparingly — prefer storing ObjectId references as String
- Embed small, bounded sub-documents (e.g. `quote` inside `appointment`)
- Reference large, unbounded or independently queried documents (e.g. `repair_tasks`)
- Always define indexes in entity classes with `@CompoundIndex`

### Security
- Passwords: BCrypt (strength 12)
- JWT access token: 15 minutes expiry
- JWT refresh token: 7 days, stored in `users.refreshToken`
- 4 roles: `CUSTOMER`, `RECEPTIONIST`, `TECHNICIAN`, `MANAGER`
- Use `@PreAuthorize("hasRole('MANAGER')")` for role-based access
- Validate file uploads: type whitelist (jpg, png, mp4), max size limits

### Error Handling
- All custom exceptions extend a base `AppException`
- Use `@RestControllerAdvice` global exception handler
- HTTP status codes: 200 (OK), 201 (Created), 400 (Bad Request), 401 (Unauthorized), 403 (Forbidden), 404 (Not Found), 409 (Conflict), 500 (Internal Server Error)
- Never expose stack traces in production

### Testing
- Unit tests in `src/test/java`, mirror the main package structure
- Service tests: mock repositories with Mockito
- Controller tests: use `@WebMvcTest` + `MockMvc`
- Integration tests: use `@SpringBootTest` + Testcontainers (MongoDB)
- Test method naming: `should_ExpectedResult_When_Condition()`
- Target: 80%+ coverage on service layer

## Git Conventions
- **Gitflow branches:** `main` (production), `develop` (integration), `feature/*`, `release/*`, `hotfix/*`
- Branch naming: `feature/module-name`, `fix/issue-description`, `hotfix/critical-fix`
- Commit format: `type(scope): description` — e.g. `feat(appointment): add booking endpoint`
- Types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `style`
- Never push directly to `main` or `develop` — always via Pull Request
- Feature branches off `develop`, merge back to `develop`
- Hotfix branches off `main`, merge to both `main` AND `develop`

## Docker
- Development: `docker compose --profile dev up`
- Production: `docker compose up` (no dev profile)
- Always use multi-stage Dockerfile (build + run)
- Never hardcode secrets — use environment variables

## Key Business Rules
- Appointment status flow: `PENDING_CONFIRMATION → CONFIRMED → VEHICLE_RECEIVED → INSPECTING → AWAITING_QUOTE_APPROVAL → REPAIRING → COMPLETED → DELIVERED` (any → CANCELLED)
- Customer can only review AFTER status = DELIVERED
- Stock auto-deducts when invoice is created
- Low stock alert when quantity <= minQuantity
- One review per appointment (unique constraint)
- Appointment number format: `APT-YYYYMMDD-NNN`
- Invoice number format: `INV-YYYYMMDD-NNN`
- Currency: VND (no decimal), VAT 10%

## Language
- Code: English (class names, variables, methods, comments)
- API messages / UI strings: Vietnamese
- Documentation: Vietnamese or English
