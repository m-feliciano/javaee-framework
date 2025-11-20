# Etapa de build Maven
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the project; do not bake runtime DB/secret values into the artifact
RUN mvn clean package -DskipTests

# Etapa de runtime Tomcat
FROM tomcat:9.0-jdk17

# Remove default web applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from the builder stage (finalName=servlets -> servlets.war)
COPY --from=builder /app/target/servlets.war /usr/local/tomcat/webapps/ROOT.war

# Explode the WAR file so we can allow runtime mounts/overrides
RUN mkdir -p /usr/local/tomcat/webapps/ROOT && \
    cd /usr/local/tomcat/webapps/ROOT && \
    jar xf /usr/local/tomcat/webapps/ROOT.war

COPY docker/config/app-prod.yml /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/app-prod.yml
COPY docker/config/app-dev.yml /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/app-dev.yml

EXPOSE 8080
CMD ["catalina.sh", "run"]
