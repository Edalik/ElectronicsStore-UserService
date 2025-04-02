FROM amazoncorretto:17.0.14-alpine
EXPOSE 8080
COPY ./build/libs/*.jar user-service.jar
ENTRYPOINT ["java", "-jar", "/user-service.jar"]