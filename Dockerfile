FROM eclipse-temurin:17-jdk-alpine
LABEL maintainer="Nícolas Basilio"

WORKDIR /home/registryapi

ENV PORT=8080

COPY target/*.jar app.jar
COPY src/main/resources/application-prod.properties application.properties

EXPOSE ${PORT}

CMD [ "java", "-Dspring.profiles.active=prod", "-jar", "./app.jar" ]
