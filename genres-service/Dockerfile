FROM bellsoft/liberica-openjdk-alpine-musl:17.0.16
COPY /target/genres-service.jar /app/app.jar
EXPOSE 7082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]