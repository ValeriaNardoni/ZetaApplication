# Stage 1: build con Maven
FROM maven:3.9.2-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copia solo i file necessari per scaricare le dipendenze
COPY pom.xml .
COPY src ./src

# Build del progetto
RUN mvn clean package -DskipTests

# Stage 2: immagine finale più leggera
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia il jar dalla build
COPY --from=build /app/target/*.jar app.jar

# Esponi la porta di Spring Boot
EXPOSE 8080

# Avvia l'app
ENTRYPOINT ["java", "-jar", "app.jar"]