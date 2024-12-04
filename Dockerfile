# Build aşaması: Uygulamanın Gradle ile build edilmesi
FROM gradle:jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Çalıştırma aşaması: Uygulamanın Amazon Corretto JDK imajı ile çalıştırılması
FROM amazoncorretto:21.0.3-alpine3.19
WORKDIR /app
COPY --from=build /app/build/libs/ConnectUs-v0.0.1.jar app.jar
EXPOSE 8080  # Render platformunda genellikle port 8080 kullanılır
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
