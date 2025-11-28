FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY frontend ./frontend

RUN mvn clean package -DskipTests

FROM tomcat:10.1.39-jre21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/servlets.war /usr/local/tomcat/webapps/ROOT.war

RUN mkdir -p /usr/local/tomcat/webapps/ROOT && \
    unzip /usr/local/tomcat/webapps/ROOT.war -d /usr/local/tomcat/webapps/ROOT && \
    rm /usr/local/tomcat/webapps/ROOT.war

COPY docker/config/ /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/

EXPOSE 8080
CMD ["catalina.sh", "run"]
