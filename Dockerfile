FROM openjdk:11
MAINTAINER Amir Muminov
ADD /target/delivery-service-0.0.1-SNAPSHOT.jar delivery-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "delivery-service-0.0.1-SNAPSHOT.jar"]

EXPOSE 8085
