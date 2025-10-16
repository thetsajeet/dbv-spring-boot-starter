# Spring Boot Database Validator

A lightweight Spring Boot tool to **validate database tables, views, sequences, and other objects** in your environment. Ensures your database is correctly set up for your applications, catching missing objects early in the development or deployment process.

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
db.validator.enabled=true
```