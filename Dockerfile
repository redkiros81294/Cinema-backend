FROM openjdk:21-jdk AS build


WORKDIR /app


COPY pom.xml .

COPY src ./src


RUN ./mvnw clean package -DskipTests -Dmaven.test.skip=true || mvn clean package -DskipTests -Dmaven.test.skip=true


FROM openjdk:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8101 
ENTRYPOINT ["java", "-jar", "app.jar"]