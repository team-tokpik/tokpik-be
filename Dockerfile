FROM openjdk:17

WORKDIR /app

COPY build/libs/*.jar tokpik_be.jar

EXPOSE 8080

# 로그 디렉토리 생성 및 권한 부여
RUN mkdir -p /logs && chmod 755 /logs

# 백그라운드에서 실행하고 로그를 파일에 저장
CMD java -jar -Dspring.profiles.active=dev tokpik_be.jar > /logs/application.log 2>&1
