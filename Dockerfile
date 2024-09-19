# Base image
FROM bellsoft/liberica-openjdk-alpine:21

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul","-jar","/app.jar"]
