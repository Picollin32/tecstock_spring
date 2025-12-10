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

# Configurações JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# ### CORREÇÃO 2: Forçar o Spring a rodar na porta 8081
# Isso garante que a porta do app bata com o EXPOSE e o Healthcheck
ENV SERVER_PORT=8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Executar aplicação
# Adicionei o argumento explícito da porta apenas para garantir, embora a ENV acima já resolva
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=8081"]