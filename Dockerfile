FROM bellsoft/liberica-openjdk-alpine-musl:17.0.16
COPY /target/spring2026pd.jar /app/app.jar
EXPOSE 7081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]