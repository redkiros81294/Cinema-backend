FROM maven:3.9.7-eclipse-temurin-21 AS build


WORKDIR /app


COPY pom.xml .

COPY src ./src


RUN mvn clean package -DskipTests


FROM openjdk:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8101 
ENTRYPOINT ["java", "-jar", "app.jar"]