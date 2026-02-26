FROM eclipse-temurin:21-jre
LABEL authors="kyw10987"

WORKDIR /app

COPY build/libs/*.jar app.jar

# EXECUTE
ENTRYPOINT ["java", "-jar", "app.jar"]
