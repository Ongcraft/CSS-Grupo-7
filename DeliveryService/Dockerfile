FROM maven:3.9.14-eclipse-temurin-17 AS builder

WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY Common ./Common
COPY DeliveryService ./DeliveryService

RUN mvn -N clean install -DskipTests
RUN cd Common && mvn clean install -DskipTests
RUN cd DeliveryService && mvn clean package -DskipTests

FROM eclipse-temurin:17

WORKDIR /app
COPY --from=builder /workspace/DeliveryService/target/delivery-service-1.0.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]