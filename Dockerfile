# OpenJDK 17 기반 이미지
FROM openjdk:17-jdk-slim

# 작업 디렉토리 생성
WORKDIR /app

# jar, yml, env 복사
COPY build/libs/gabom-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application-dev.yml ./src/main/resources/application-dev.yml
COPY .env .env

# 포트 오픈
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]