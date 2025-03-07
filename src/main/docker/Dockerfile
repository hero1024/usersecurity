FROM openjdk:8-jdk-alpine
VOLUME /data
VOLUME /config
ADD usersecurity-0.0.1-SNAPSHOT.jar user-security.jar
COPY ./config /config
EXPOSE 8082
ENTRYPOINT [ "java","-Xms1024m","-Xmx1024m","-jar","/user-security.jar","--spring.config.location=/config/application-pro.yml","-c" ]