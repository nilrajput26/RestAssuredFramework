# Restful-Booker API Test Framework

API test automation for [Restful-Booker](https://restful-booker.herokuapp.com) with Rest Assured, TestNG, Jackson, Lombok, AssertJ and Allure.

## Prerequisites

- Java 21, Maven 3.9+
- Environment variable `BOOKER_PASSWORD` (the API password; never stored in the repo)

```powershell
setx BOOKER_PASSWORD "<password>"   # then fully restart IntelliJ / the terminal
```

## Running

| Command | What runs |
| --- | --- |
| `mvn test` | full regression suite (`testng.xml`) against `qa` |
| `mvn test -DsuiteXmlFile=testng-smoke.xml` | smoke group only |
| `mvn test -Denv=dev` | use `config/dev.properties` |
| `mvn test -Dtest=BookingTest#patchBooking` | one test method (ignores the suite file) |
| `mvn test -Dretry.count=0` | turn off retries of failed tests |
| `mvn allure:serve` | build the Allure report and open it in the browser |
| `mvn allure:report` | build the report into `target/site/allure-maven-plugin` |

## Structure

```
src/main/java/com
├── config/      ConfigManager     eager singleton: reads config/<env>.properties once, secrets from env vars
├── constants/   Endpoints         endpoint paths as constants
├── specs/       SpecFactory       builds the base spec and the auth spec (token passed in)
├── pojo/        Booking, BookingDates, BookingResponse, AuthRequest, AuthResponse
└── clients/     BookingClient, AuthClient, HealthClient   send requests, return Response, no assertions

src/test/java
├── com/base/       BaseTest (base spec, getAuthSpec), TokenManager (lazy cached token)
├── com/testdata/   BookingData (Faker + builders)
├── com/listeners/  TestListener (logging), RetryAnalyzer + RetryTransformer
└── tests/          HealthCheckTest, AuthTest, BookingTest

src/test/resources
├── config/         qa.properties, dev.properties
├── schemas/        booking-schema.json
├── allure.properties, log4j2.xml

testng.xml, testng-smoke.xml    suites
Jenkinsfile, azure-pipelines.yml   CI
```

## Design

- **Client layer**: one client per resource; the spec is passed in the constructor, so the same client works with or without a token.
- **Auth**: `TokenManager` calls `POST /auth` once per run (lazy, `synchronized`, cached in a static field). `getAuthSpec()` adds it as `Cookie: token=...`.
- **Independent tests**: each test creates its own booking; no `dependsOnMethods`.
- **Assertions**: `.then().statusCode()` for HTTP checks (before `.as()`), AssertJ for POJO comparisons, JSON schema for structure.
- **Groups**: `smoke` (critical path) and `regression` (everything).

## Restful-Booker quirks

| Call | Returns |
| --- | --- |
| `GET /ping` | 201 |
| `DELETE /booking/{id}` | 201 |
| `POST /auth` with wrong password | 200 + `{"reason":"Bad credentials"}` |
| PUT / PATCH / DELETE without token | 403 |

## Logs and reports

- Console + `target/logs/test-run.log` (log4j2)
- Allure: requests and responses attached to every test (`AllureRestAssured` filter)
- TestNG/Surefire XML: `target/surefire-reports`
