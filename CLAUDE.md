# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Spring Boot 3.5.6 Reactive REST API** for managing franchises, built with:
- **Spring WebFlux** (reactive web framework)
- **Spring Data Reactive MongoDB** (reactive NoSQL data access)
- **Java 17**
- **Maven** build system
- **Lombok** for boilerplate reduction
- **Spring REST Docs** for API documentation generation

Base package: `org.esteban.springboot.springmvc.app.franchise_apirest`

## Key Architecture Characteristics

### Reactive Programming Model
This application uses **Project Reactor** and Spring WebFlux for non-blocking, reactive operations. All database operations and HTTP endpoints should return reactive types (`Mono<T>`, `Flux<T>`).

### MongoDB Integration
Uses Spring Data Reactive MongoDB. Connection details are configured in `src/main/resources/application.properties`.

## Build & Development Commands

### Build the project
```bash
mvnw clean install
```

### Run the application
```bash
mvnw spring-boot:run
```

### Run tests
```bash
mvnw test
```

### Run a single test class
```bash
mvnw test -Dtest=YourTestClassName
```

### Run a single test method
```bash
mvnw test -Dtest=YourTestClassName#testMethodName
```

### Package the application
```bash
mvnw package
```

### Generate API documentation
Documentation is automatically generated during the `prepare-package` phase using Asciidoctor and Spring REST Docs. Run:
```bash
mvnw package
```
Generated docs will be in `target/generated-docs/`.

## Development Notes

### Maven Wrapper
Use `mvnw` (Linux/Mac) or `mvnw.cmd` (Windows) instead of `mvn` to ensure consistent Maven version.

### Lombok Configuration
Lombok annotation processing is configured in the Maven compiler plugin. IDEs may require the Lombok plugin installed and annotation processing enabled.

### DevTools
Spring Boot DevTools is included for automatic application restart during development when files change.