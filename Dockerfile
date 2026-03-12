FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 9080

ENTRYPOINT ["java","-jar","app.jar"]