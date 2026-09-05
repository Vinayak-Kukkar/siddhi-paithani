# Stage 1: Build the Spring Boot application using Maven & Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create lightweight execution container with Java 21 JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar


EXPOSE 8084
ENV PORT=8084

ENTRYPOINT ["java", "-jar", "app.jar"]
