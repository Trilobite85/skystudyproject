FROM openjdk:17-jdk-slim

ADD target/skystudyproject.jar /app/

EXPOSE 8080
WORKDIR /app
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Xms512m", "-Xmx1g", "-jar", "skystudyproject.jar"]
