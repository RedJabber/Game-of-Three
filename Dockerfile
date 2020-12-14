FROM bellsoft/liberica-openjdk-alpine:11.0.9-12
EXPOSE 8080
CMD ["./mvnw", "package"]
COPY target/*.jar application.jar
ENTRYPOINT ["java", "-jar", "/application.jar"]


