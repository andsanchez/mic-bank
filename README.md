# mic-bank
Reactive service for hexagonal architecture to handle bank transactions.

## Requirements

For building and running the application you need:

- JDK 11
- Maven 3 (Includes Maven Wrapper)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `io.andsanchez.bank.Application` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

Application started on port 8080.

## API Quickstart

Includes postman collection in the directory `src/main/postman` to test the api.

## Technologies dependencies

- Java 11
- Spring Boot 2.6.3
- Spring Webflux
- Spring Data
- OpenAPI 3
- MongoDB 4.0
- Lombok
- Mapstruct