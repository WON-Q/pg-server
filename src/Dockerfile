# 1단계: JAR 빌드용 이미지
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build -x test

# 2단계: 실행용 이미지
FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY --from=build /home/gradle/project/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]