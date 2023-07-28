FROM amazoncorretto:17-alpine
WORKDIR /app
COPY build/libs/jumarket-api-1.0.0.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]