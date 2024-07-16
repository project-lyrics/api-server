FROM openjdk:21-jdk-slim

ARG JAR_FILE=./build/libs/*.jar

COPY ${JAR_FILE} server.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "server.jar"]
