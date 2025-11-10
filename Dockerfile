# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom and sources
COPY pom.xml .
COPY src ./src

# Build without tests to speed up
RUN mvn -B -DskipTests clean package

# ---- Run stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# Heroku sets PORT; just pass it to Spring via server.port
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
