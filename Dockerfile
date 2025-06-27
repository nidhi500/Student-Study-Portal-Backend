# Use a JDK image and manually install gradlew wrapper
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app
COPY . .

# Ensure gradlew is executable
RUN chmod +x gradlew

# Build the Spring Boot JAR
RUN ./gradlew bootJar --no-daemon

# Use a minimal runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
