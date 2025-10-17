# Spring Boot Database Validator

- A lightweight Spring Boot starter tool to **validate database tables, views, sequences, and other objects** in your environment. 
- Often times when we promote our applications across different environments (dev, staging, production), there are chances that some database objects might be missing or not created properly.
- This tool helps to mitigate such issues by validating the presence of required database objects at application startup.
- Ensures your database is correctly set up for your applications, catching missing objects early in the development or deployment process.

## Features

- Validate presence of tables, views, sequences, and more.
- Configurable via Spring properties.
- Works seamlessly as a Spring Boot **CommandLineRunner** or in a Spring context.
- Conditional autoconfiguration with `@ConditionalOnClass` and `@ConditionalOnProperty`.

## Getting Started

### Prerequisites

- Java 21+
- Spring Boot 3.x
- Maven

### Installation

Add as a dependency:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>database-validator</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or clone and include directly in your project.

### Usage

```properties
db.validator.enabled = true
```

## Note

- Ensure your database connection is properly configured in your Spring Boot application.
- This currently supports major JDBC supported databases (Relational Databases) only such as MySQL, PostgreSQL, Oracle, SQL Server, etc.
- We are working on adding support for NoSQL databases in future releases.
- Make sure to turn of hibernate's automatic schema generation to avoid conflicts. `spring.jpa.hibernate.ddl-auto=none`
- If you are looking for hibernate's automatic schema generation features, then this starter might not be suitable for your use case, since the goal is to catch the missing database objects early in the deployment processes.

## Thank you

- If you find this tool useful, please give it a star!
- Feel free to share feedback or report issues.
- Contributions are welcome!
- Please check [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
