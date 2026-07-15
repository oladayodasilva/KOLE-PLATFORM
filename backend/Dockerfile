FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw --batch-mode dependency:go-offline

COPY src src

RUN ./mvnw --batch-mode clean package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

RUN groupadd --system kole \
    && useradd --system \
        --gid kole \
        --home-dir /app \
        kole

COPY --from=build \
    /workspace/target/*.jar \
    /app/kole-backend.jar

USER kole

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/kole-backend.jar"]