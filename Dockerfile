FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle.kts settings.gradle.kts ./
COPY src/ src/
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
