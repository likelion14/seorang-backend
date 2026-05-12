FROM eclipse-temurin:17-jdk-focal AS build
WORKDIR /app

COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

FROM eclipse-temurin:17-jre-focal
WORKDIR /app

COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-jar", "app.jar"]