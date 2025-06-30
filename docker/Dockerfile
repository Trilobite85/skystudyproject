FROM openjdk:20-jdk

VOLUME /tmp
ADD skystudyproject.jar /opt/skystudyproject/
EXPOSE 8080
WORKDIR /opt/skystudyproject/
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Xms512m", "-Xmx1g", "-jar", "skystudyproject.jar"]
