# syntax = docker/dockerfile:1
FROM eclipse-temurin:21-jdk AS dev

WORKDIR /app

CMD ["./gradlew", "run"]

FROM dev AS build

# https://ktor.io/docs/server-fatjar.html#build
RUN --mount=type=bind,source=gradlew,target=gradlew \
    --mount=type=bind,source=gradle,target=gradle \
    --mount=type=bind,source=build.gradle.kts,target=build.gradle.kts \
    --mount=type=bind,source=settings.gradle.kts,target=settings.gradle.kts \
    --mount=type=bind,source=src,target=src \
    ./gradlew buildFatJar

FROM gcr.io/distroless/java21-debian12:nonroot AS runtime

WORKDIR /

COPY --from=build /app/build/libs/app.jar app.jar

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-Xms512m", "-Xmx1g", "-jar", "app.jar"]
