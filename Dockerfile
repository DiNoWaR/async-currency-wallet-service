FROM openjdk:21-jdk-slim AS builder

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew test
RUN ./gradlew bootJar

FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
 && rm -rf /var/lib/apt/lists/*

ENV LIQUIBASE_VERSION=4.32.0
RUN curl -L https://github.com/liquibase/liquibase/releases/download/v${LIQUIBASE_VERSION}/liquibase-${LIQUIBASE_VERSION}.zip -o liquibase.zip \
 && unzip liquibase.zip -d /opt/liquibase \
 && ln -s /opt/liquibase/liquibase /usr/local/bin/liquibase \
 && rm liquibase.zip

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
