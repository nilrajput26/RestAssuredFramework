# My framework: class-by-class quick revision

Read this just before an interview. For each class: what it does, what I used, and why.

## ConfigManager (`com.config.ConfigManager`)

**What it does:** loads the environment's properties file (qa / dev / uat) **once** and gives every test the same values, e.g. `baseUri`.

**Pattern:** **Eager singleton.**
- `private static final ConfigManager INSTANCE = new ConfigManager();` creates the one object when the class loads.
- `private` constructor: nobody can call `new ConfigManager()`, so only one object can exist.
- `public static getInstance()` is the only way to get it.
- **Why eager:** config is always needed, so there is no benefit in creating it late. It is **thread-safe without `synchronized`**, because the JVM loads a class only once (safe for parallel tests). It is the simplest correct option.

**How the environment is chosen:** `System.getProperty("env", "qa")`
- Reads `-Denv=dev` from the command line: `mvn test -Denv=dev`. It also works in IntelliJ VM options and CI pipelines.
- `qa` is only the **default** when nothing is passed, so it is not hard-coding.
- One file per environment: `src/test/resources/config/qa.properties`, `dev.properties`, ...

**How the file is read:**
- `ConfigManager.class.getClassLoader().getResourceAsStream("config/" + env + ".properties")`
  - Finds the file on the **classpath** (`src/test/resources`), not by a hard-coded disk path, so it works on any machine and in CI.
  - Returns `null` if the file is missing, so I **fail fast**: `throw new RuntimeException(fileName + " not found")`.
- `Properties prop` + `prop.load(input)` reads every `key=value` line into a map.
- **try-with-resources** `try (InputStream input = ...)` closes the stream automatically, even on error.
- `IOException` (checked) is wrapped in a `RuntimeException`, keeping the original as the cause.

**How tests use it:** `BaseTest` calls `.setBaseUri(ConfigManager.getInstance().getProperty("baseUri"))`.

**Passwords:** never in the properties file. Read them with `System.getenv("API_PASSWORD")`: an OS env variable locally, Jenkins credentials or ADO secret variables / Key Vault in CI. The Java code is the same in every case.

**Interview line:** "ConfigManager is an eager singleton that loads an environment-specific properties file from the classpath using getClassLoader().getResourceAsStream(). The environment comes from -Denv, with qa as the default, so the same code runs on any environment locally or in CI. It fails fast if the file is missing, and secrets come from environment variables, not the file."
