FROM gradle:jdk-21-and-22-alpine AS build
WORKDIR /app

COPY build.gradle settings.gradle /app/
RUN gradle dependencies --no-daemon || return 0

COPY . /app
RUN gradle clean build --no-daemon -x test

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/server.jar

EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "server.jar"]
