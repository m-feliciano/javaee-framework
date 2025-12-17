FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .

ARG MVN_PROFILE=prod
ENV MVN_PROFILE=${MVN_PROFILE}

COPY src ./src
COPY frontend ./frontend

RUN mvn clean package -P${MVN_PROFILE} -DskipTests=false

FROM tomcat:10.1.50-jre21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=builder /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
