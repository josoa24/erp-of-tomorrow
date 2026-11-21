FROM maven:3.9-eclipse-temurin-17 AS build
ARG MODULE_PATH
WORKDIR /build
COPY ${MODULE_PATH}/pom.xml .
RUN mvn -q -B dependency:go-offline
COPY ${MODULE_PATH}/src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
USER nobody
ENTRYPOINT ["java", "-jar", "app.jar"]
