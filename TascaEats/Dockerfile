FROM maven:3.9.14-eclipse-temurin-17 AS builder

WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY Common ./Common
COPY TascaEats ./TascaEats

RUN mvn -N clean install -DskipTests
RUN cd Common && mvn clean install -DskipTests
RUN cd TascaEats && mvn clean package -DskipTests

FROM eclipse-temurin:17

WORKDIR /app
COPY --from=builder /workspace/TascaEats/target/tascaeats-1.0.jar app.jar

EXPOSE 8080 9090

ENTRYPOINT ["java", "-jar", "app.jar"]