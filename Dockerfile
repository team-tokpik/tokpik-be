FROM openjdk:17

WORKDIR /app

COPY build/libs/*.jar tokpik_be.jar

EXPOSE 8080

# 백그라운드에서 실행하고 로그를 파일에 저장
CMD nohup java -jar -Dspring.profiles.active=dev tokpik_be.jar > application.log 2>&1
