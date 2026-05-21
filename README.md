# InteroperabilityProject — Books (Theme #9)

**Algebra University, 3rd year — Interoperabilnost informacijskih sustava**  
Entity: `Book` via [FakeRESTAPI](https://fakerestapi.azurewebsites.net/api/v1/Books)

---

## Prerequisites

| Requirement | Version |
|---|---|
| JDK | 21 (Microsoft Build of OpenJDK recommended) |
| SQL Server | 2019+ (Express is fine) / SSMS for setup |
| Maven | Not required — Maven Wrapper (`mvnw`) bundled |

---

## Database Setup

1. Open SSMS and connect to `localhost`.
2. Ensure SQL Server is in **Mixed Mode** (Server Properties → Security → *SQL Server and Windows Authentication mode*). Restart the SQL Server service if you change this.
3. Run `docs/ssms-setup.sql` (idempotent — safe to re-run):
   ```sql
   -- Creates database BooksDb, login books_user / StrongP@ssw0rd!, and grants db_owner
   ```
4. Verify: `SELECT name FROM sys.databases WHERE name = 'BooksDb'` should return a row.

---

## Build & Run

```powershell
# 1. Set JAVA_HOME (required in every new shell on Windows)
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot"

# 2. Start the app (downloads Maven 3.9.9 on first run)
.\mvnw.cmd spring-boot:run
```

Expected output:
```
Seeded 3 books.
Seeded users: reader, admin.
Started BackendApplication in X.XXX seconds
```

App runs on **http://localhost:8181**  
gRPC server on port **9090**

---

## Running Tests

Tests use H2 in-memory DB — SQL Server not required:

```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot"
.\mvnw.cmd test
```

Run a single test class:
```powershell
.\mvnw.cmd test "-Dtest=BookControllerSmokeTest"
.\mvnw.cmd test "-Dtest=XmlValidationServiceTest"
.\mvnw.cmd test "-Dtest=XPathFilterServiceTest"
```

---

## Default Users

| Username | Password | Role | Can do |
|---|---|---|---|
| `reader` | `reader123` | READ | GET endpoints only |
| `admin` | `admin123` | FULL | All CRUD + mutations |

---

## Feature Demo

### 1 — Books REST CRUD (`/api/v1/books`)

```bash
# List all books
curl http://localhost:8181/api/v1/books

# Get single book
curl http://localhost:8181/api/v1/books/1

# Search by term
curl "http://localhost:8181/api/v1/books?term=clean"

# Login to get a token
curl -X POST http://localhost:8181/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Create (requires FULL role)
curl -X POST http://localhost:8181/api/v1/books \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"My Book","pageCount":200}'

# Update
curl -X PUT http://localhost:8181/api/v1/books/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated","pageCount":300}'

# Delete
curl -X DELETE http://localhost:8181/api/v1/books/1 \
  -H "Authorization: Bearer <token>"
```

---

### 2 — SOAP + XPath Search (`/ws/books.wsdl`)

**WSDL:** http://localhost:8181/ws/books.wsdl

```bash
curl -X POST http://localhost:8181/ws \
  -H "Content-Type: text/xml" \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:b="http://algebra.hr/books/soap">
    <soapenv:Body>
      <b:searchBooksByTermRequest>
        <b:term>clean</b:term>
      </b:searchBooksByTermRequest>
    </soapenv:Body>
  </soapenv:Envelope>'
```

Web UI: http://localhost:8181/soap-search

---

### 3 — Jakarta XML Validation (`/api/v1/validate-xml`)

```bash
# Valid book XML
curl -X POST http://localhost:8181/api/v1/validate-xml \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/xml" \
  --data-binary '<book><title>Clean Code</title><pageCount>431</pageCount></book>'

# Invalid (missing title)
curl -X POST http://localhost:8181/api/v1/validate-xml \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/xml" \
  --data-binary '<book><pageCount>100</pageCount></book>'
```

Web UI: http://localhost:8181/validation

---

### 4 — gRPC Weather (DHMZ)

**Proto:** `src/main/proto/weather.proto`  
Service: `WeatherService.FindTemperature` (server-streaming)  
Server: `localhost:9090`

Test with [grpcurl](https://github.com/fullstorydev/grpcurl):
```bash
grpcurl -plaintext -d '{"cityName":"Zagreb"}' \
  localhost:9090 hr.algebra.books.grpc.WeatherService/FindTemperature
```

Web UI: http://localhost:8181/weather

---

### 5 — JWT Authentication (`/api/auth/login`, `/api/auth/refresh`)

```bash
# Login
curl -X POST http://localhost:8181/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
# Returns: {"accessToken":"...","refreshToken":"..."}

# Refresh
curl -X POST http://localhost:8181/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refresh-token>"}'
```

---

### 6 — Public/Custom API Mode Switch (`/api/v1/mode`)

```bash
# Check current mode (authenticated)
curl http://localhost:8181/api/v1/mode \
  -H "Authorization: Bearer <token>"

# Switch to FakeRESTAPI (requires FULL role)
curl -X PUT "http://localhost:8181/api/v1/mode?mode=PUBLIC" \
  -H "Authorization: Bearer <token>"

# Switch back to local DB
curl -X PUT "http://localhost:8181/api/v1/mode?mode=CUSTOM" \
  -H "Authorization: Bearer <token>"
```

Web UI: http://localhost:8181/settings

---

### 7 — GraphQL (`/graphql`)

**GraphiQL:** http://localhost:8181/graphiql

```graphql
# Queries (no auth required)
{ books { id title pageCount } }
{ book(id: 1) { id title description pageCount excerpt publishDate } }

# Mutations (requires FULL role — add Authorization header in GraphiQL settings)
mutation {
  createBook(input: { title: "New Book", pageCount: 150 }) { id title }
}

mutation {
  updateBook(id: 1, input: { title: "Updated", pageCount: 200 }) { id title }
}

mutation {
  deleteBook(id: 5)
}
```

Web playground: http://localhost:8181/graphql-ui

---

### 8 — XML + JSON Import with Validation (`/api/v1/import`)

```bash
# Import valid XML
curl -X POST http://localhost:8181/api/v1/import \
  -H "Authorization: Bearer <token>" \
  -F "file=@src/main/resources/samples/book-valid.xml"

# Import invalid XML (returns 422 with error list)
curl -X POST http://localhost:8181/api/v1/import \
  -H "Authorization: Bearer <token>" \
  -F "file=@src/main/resources/samples/book-invalid.xml"

# Import valid JSON
curl -X POST http://localhost:8181/api/v1/import \
  -H "Authorization: Bearer <token>" \
  -F "file=@src/main/resources/samples/book-valid.json"
```

Web UI: http://localhost:8181/import

---

### 9 — Thymeleaf Web UI

| URL | Description | Auth required |
|---|---|---|
| http://localhost:8181/ | Home dashboard | Any |
| http://localhost:8181/login | Login page | — |
| http://localhost:8181/books | Books list + search | Any |
| http://localhost:8181/books/new | Create book form | FULL |
| http://localhost:8181/settings | API mode switch | FULL |
| http://localhost:8181/soap-search | SOAP/XPath search | Any |
| http://localhost:8181/validation | XML validation report | Any |
| http://localhost:8181/weather | DHMZ weather lookup | Any |
| http://localhost:8181/graphql-ui | GraphQL playground | Any |
| http://localhost:8181/import | File import | Any |

---

## Project Structure

```
hr.algebra.books
├── BackendApplication.java
├── common/          — config, exception handler, seeder
├── book/            — domain, JPA, REST, GraphQL, web controllers
├── auth/            — /api/auth login & refresh
├── security/        — JWT filter chains (API + web)
├── user/            — AppUser entity + repository
├── soap/            — Spring-WS SOAP endpoint + XPath filter
├── xmlvalidation/   — Jakarta XML validation report
├── grpc/            — gRPC WeatherService + DHMZ client
├── importer/        — XML+JSON import with XSD/JSON-Schema validation
├── weather/         — web wrapper for DHMZ weather display
└── home/            — home dashboard + settings page
```

---

## Key Configuration (`src/main/resources/application.yml`)

| Property | Default | Description |
|---|---|---|
| `server.port` | `8181` | HTTP port |
| `grpc.server.port` | `9090` | gRPC port |
| `app.api.mode` | `CUSTOM` | `CUSTOM` (local DB) or `PUBLIC` (FakeRESTAPI) |
| `app.jwt.secret` | see yml | HS256 base64 key — change in production |
| `app.jwt.access-ttl` | `PT15M` | Access token lifetime |
| `app.dhmz.url` | DHMZ feed URL | Croatian weather XML feed (windows-1250 encoded) |
