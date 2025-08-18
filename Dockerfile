FROM openjdk:17-jdk-slim
WORKDIR /app

# JAR 복사
COPY build/libs/gabom-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application-dev.yml ./src/main/resources/application-dev.yml
COPY .env .env

# 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]