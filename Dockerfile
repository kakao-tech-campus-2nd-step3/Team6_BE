# Base image
FROM bellsoft/liberica-openjdk-alpine:21

# ⭐ 'ARG' 예약어를 통해 인자로 전달 받아야 한다.
ARG CLIENT_ID \
    REDIRECT_URI \
    JWT_SECRET \
    DB_URL \
    DB_USERNAME \
    DB_PASSWORD \
    REDIS_HOST  \
    REDIS_PORT \
    AWS_YOUR_REGION \
    AWS_ACCESS_KEY \
    AWS_SECRET_KEY \
    S3_BUCKET_NAME


# ⭐ 'ENV' 예약어를 통해 전달받은 값을 실제 값과 매칭시켜야 한다.
ENV CLIENT_ID=${CLIENT_ID} \
    REDIRECT_URI=${REDIRECT_URI} \
    JWT_SECRET=${JWT_SECRET} \
    DB_URL=${DB_URL} \
    DB_USERNAME=${DB_USERNAME} \
    DB_PASSWORD=${DB_PASSWORD} \
    REDIS_HOST=${REDIS_HOST} \
    REDIS_PORT=${REDIS_PORT} \
    AWS_YOUR_REGION=${AWS_YOUR_REGION} \
    AWS_ACCESS_KEY=${AWS_ACCESS_KEY} \
    AWS_SECRET_KEY=${AWS_SECRET_KEY} \
    S3_BUCKET_NAME=${S3_BUCKET_NAME}


# dev로 실행
ENV SPRING_PROFILES_ACTIVE=dev

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul","-jar","/app.jar"]
