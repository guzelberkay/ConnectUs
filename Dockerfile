# Build stage: Use a Gradle image to build the application
FROM gradle:jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Run stage: Use a lightweight JDK image to run the app
FROM amazoncorretto:21.0.3-alpine3.19
WORKDIR /app
COPY --from=build /app/build/libs/ConnectUs-v0.0.1.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]