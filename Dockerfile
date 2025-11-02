# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

# ---- Run stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Europe/Kyiv       JAVA_OPTS=""
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8090
# Spring Boot will pick up env vars for DB automatically from docker-compose
ENTRYPOINT ["/bin/sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
