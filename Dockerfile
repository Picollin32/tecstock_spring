# Stage 1: Build da aplicação
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# ### CORREÇÃO 1: Instalar curl para o Healthcheck
# Imagens JRE minimalistas muitas vezes não têm curl.
USER root
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN groupadd -r spring && useradd -r -g spring spring

COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar

USER spring:spring

# Expor porta
EXPOSE 8081

# Configurações JVM otimizadas para containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

# Forçar porta 8081
ENV SERVER_PORT=8081

# Health check simples sem actuator - verifica se a porta está respondendo
# Aumentado start-period para 90s para dar tempo do Spring Boot inicializar completamente
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
  CMD curl -f http://localhost:8081/ || exit 1

# Executar aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]