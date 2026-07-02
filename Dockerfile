# ==========================================
# STAGE 1: Build WAR file inside Docker
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
WORKDIR /build

# Cache Maven dependencies
COPY pom.xml .
COPY config ./config
RUN mvn dependency:go-offline -B

# Copy source code and build the package
COPY src ./src
RUN mvn package -DskipTests

# ==========================================
# STAGE 2: Deploy WAR file to Tomcat 10
# ==========================================
FROM tomcat:10.1-jdk21-openjdk-slim

# Install curl for HEALTHCHECK and clean up apt cache
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Clean default webapps to optimize memory
RUN rm -rf /usr/local/tomcat/webapps/*

# Create a non-root group and user
RUN groupadd -r tomcat && useradd -r -g tomcat -d /usr/local/tomcat tomcat

# Set ownership of the tomcat directories
RUN chown -R tomcat:tomcat /usr/local/tomcat

# Copy packaged WAR file from Builder stage with correct ownership
COPY --from=builder --chown=tomcat:tomcat /build/target/SpringMVC-Demo.war /usr/local/tomcat/webapps/ROOT.war

USER tomcat

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/login || exit 1

CMD ["catalina.sh", "run"]
