FROM ubuntu:latest
LABEL authors="btole"

ENTRYPOINT ["top", "-b"]

# syntax=docker/dockerfile:1

# ==========================================
# ETAPA 1: BUILD DA APLICACAO
# ==========================================

FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace

# Copia os arquivos do Maven Wrapper
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Permite executar o Maven Wrapper
RUN chmod +x mvnw

# Baixa antecipadamente as dependencias
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests dependency:go-offline

# Copia o codigo-fonte
COPY src/ src/

# Compila e gera o JAR
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests package \
    && cp target/*.jar /workspace/app.jar


# ==========================================
# ETAPA 2: AMBIENTE DE EXECUCAO
# ==========================================

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Cria usuario sem privilegios administrativos
RUN addgroup -S spring \
    && adduser -S spring -G spring

# Copia somente o JAR gerado
COPY --from=build --chown=spring:spring \
    /workspace/app.jar /app/app.jar

USER spring

# Porta da aplicacao
EXPOSE 8080

# Inicia a aplicacao
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
