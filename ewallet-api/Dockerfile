FROM openjdk:11 AS build

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:resolve

COPY src src
RUN ./mvnw package

FROM openjdk:11
WORKDIR ewallet
COPY --from=build target/*.jar ewallet.jar
ENTRYPOINT ["java", "-jar", "ewallet.jar"]
EXPOSE 8095