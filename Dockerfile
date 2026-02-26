FROM eclipse-temurin:21-jre
LABEL authors="kyw10987"

WORKDIR /app

COPY build/libs/*.jar app.jar

# EXECUTE
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar", "-Dserver.port=$SERVER_PORT","-Dserver.ssl.key-store=$KEY_STORE_PATH","-Dserver.ssl.key-store-password=$KEY_STORE_PASSWORD","-Dserver.ssl.key-store-type=$KEY_STORE_TYPE","-Dserver.ssl.key-alias=$KEY_STORE_ALIAS","app.jar"]
