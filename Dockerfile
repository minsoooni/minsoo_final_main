FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY build/libs/*[^plain].jar app.jar

EXPOSE 8001

ENTRYPOINT ["java","-jar","app.jar"]