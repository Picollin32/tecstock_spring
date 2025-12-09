# Multi-stage build para otimizar tamanho da imagem

# Stage 1: Build da aplicação
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar apenas pom.xml primeiro para aproveitar cache do Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fonte e fazer build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Criar usuário não-root para segurança
RUN groupadd -r spring && useradd -r -g spring spring

# Copiar JAR da stage de build
COPY --from=build /app/target/*.jar app.jar

# Definir proprietário do arquivo
RUN chown spring:spring app.jar

# Mudar para usuário não-root
USER spring:spring

# Expor porta
EXPOSE 8081

# Configurações JVM otimizadas para container
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Executar aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
