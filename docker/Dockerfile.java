# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-17 AS build
ARG MODULE_PATH
WORKDIR /build
ENV MAVEN_OPTS="-Xmx384m -XX:+UseSerialGC -XX:TieredStopAtLevel=1"
COPY ${MODULE_PATH}/pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -B dependency:go-offline
COPY ${MODULE_PATH}/src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -Dmaven.test.skip=true package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
USER nobody
# Tas borné et rétractable (ramasse-miettes série), métaspace et code cache plafonnés, piles réduites,
# JIT niveau C1, arrêt net en cas de manque de mémoire pour que Docker redémarre le service.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=55 -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=25 -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=48m -XX:+UseSerialGC -Xss512k -XX:TieredStopAtLevel=1 -XX:-UsePerfData -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java", "-jar", "app.jar"]
