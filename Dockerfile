FROM openjdk:17-jdk-slim
WORKDIR /app

# curl 설치 추가
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# JAR 복사
COPY build/libs/gabom-0.0.1-SNAPSHOT.jar app.jar
# COPY .env .env

# 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]