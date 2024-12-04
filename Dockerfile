FROM amazoncorretto:21
COPY build/libs/ConnectUs-v0.0.1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
