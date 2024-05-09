FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD usersecurity-0.0.1-SNAPSHOT.jar user-security.jar
COPY application.yml /
COPY logback-spring.xml /
EXPOSE 8082
ENTRYPOINT [ "java","-Xms1024m","-Xmx1024m","-jar","/user-security.jar","--spring.config.location=/application-pro.yml","-c" ]