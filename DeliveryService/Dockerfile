FROM maven:3.9.14-eclipse-temurin-17 as builder
COPY . /workspace
WORKDIR /workspace
RUN mvn clean package

FROM docker.io/eclipse-temurin:17
VOLUME /tmp
RUN mkdir -p /app/
RUN mkdir -p /app/logs/
COPY --from=builder /target/tascaeats-1.0.jar app/app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar", "/app/app.jar"]