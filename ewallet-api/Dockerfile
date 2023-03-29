FROM openjdk:11 AS build

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:resolve

COPY src src
RUN ./mvnw package -DskipTests

FROM openjdk:11
WORKDIR ewallet-api
COPY --from=build target/*.jar ewallet-api.jar
ENTRYPOINT ["java", "-jar", "ewallet-api.jar"]
EXPOSE 8095
