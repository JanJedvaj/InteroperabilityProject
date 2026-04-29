# Interoperability Project — Books (FakeRESTAPI, theme #9)

University project for **Interoperabilnost informacijskih sustava** (Algebra, 3. godina).

The system exposes the public **Books** entity from FakeRESTAPI
(`https://fakerestapi.azurewebsites.net/api/v1/Books`) through six different
interoperability layers and integrates them in a single Thymeleaf web UI with
two roles (read-only and full access).

## Planned features (built incrementally)

1. **REST + XML/JSON import** — POST endpoint that accepts XML and JSON files,
   validates them against XSD and JSON Schema, persists valid books.
2. **SOAP + XPath** — SOAP endpoint that filters a generated XML by a search
   term using XPath.
3. **Jakarta XML validation** — programmatic validation report over the
   generated XML using `jakarta.xml.validation`.
4. **gRPC weather** — gRPC server that fetches DHMZ
   (`https://vrijeme.hr/hrvatska_n.xml`) and returns current temperature for
   a given city name (or partial match).
5. **Custom REST + JWT + GraphQL + switch** — full CRUD with access/refresh
   tokens, GraphQL queries and mutations, and a runtime switch between the
   public FakeRESTAPI and the local custom REST.
6. **Web UI** — Thymeleaf pages with role-aware controls calling all of the
   above.

## Tech stack

- Java 21
- Spring Boot 3.3.x
- Maven
- Microsoft SQL Server (managed via SSMS)

## Build & run

This project ships a Maven Wrapper, so you do **not** need a global Maven
install — only JDK 21. From the project root:

**Windows (PowerShell / cmd):**
```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw clean verify
./mvnw spring-boot:run
```

The first invocation downloads Maven 3.9.9 + the wrapper jar
(~10 MB) into your local Maven cache; subsequent runs are instant.

Default ports: web on `8080`, gRPC on `9090` (added in a later commit).

## Status

This is the first commit — project skeleton only. Subsequent commits add one
feature each. See the plan in `docs/` (added later) for the full roadmap.
