FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk17

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=builder /app/target/servlets.war /usr/local/tomcat/webapps/ROOT.war

RUN mkdir -p /usr/local/tomcat/webapps/ROOT && \
    cd /usr/local/tomcat/webapps/ROOT && \
    jar xf /usr/local/tomcat/webapps/ROOT.war \
    && rm /usr/local/tomcat/webapps/ROOT.war

COPY docker/config/app-prod.yml /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/app-prod.yml
COPY docker/config/app-dev.yml /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/app-dev.yml

EXPOSE 8080
CMD ["catalina.sh", "run"]
