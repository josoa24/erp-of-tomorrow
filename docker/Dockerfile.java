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
# JVM adaptée aux conteneurs : tas proportionnel à la limite mémoire du conteneur, ramasse-miettes
# série (peu de threads, adapté aux petits tas), piles de threads réduites, compilation JIT limitée au
# niveau C1 (démarrage environ 2 fois plus rapide, environ 20 % de mémoire en moins), arrêt net en cas
# de manque de mémoire pour que Docker redémarre le service.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k -XX:TieredStopAtLevel=1 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java", "-jar", "app.jar"]
