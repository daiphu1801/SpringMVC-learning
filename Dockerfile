# ==========================================
# STAGE 1: Build WAR file inside Docker
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
WORKDIR /build

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the package
COPY src ./src
RUN mvn package -DskipTests

# ==========================================
# STAGE 2: Deploy WAR file to Tomcat 10
# ==========================================
FROM tomcat:10.1-jdk21-openjdk-slim

# Clean default webapps to optimize memory
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy packaged WAR file from Builder stage
COPY --from=builder /build/target/SpringMVC-Demo.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
