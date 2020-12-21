FROM openjdk:8-jre-alpine
COPY scheduler-0.0.4-SNAPSHOT.jar /scheduler.jar
ENTRYPOINT ["java"]
CMD ["-jar", "/scheduler.jar"]
EXPOSE 8080
