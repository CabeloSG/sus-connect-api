# ============================================================
# SUS Connect API
# Build Stage
# ============================================================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copia Maven Wrapper e POM primeiro para aproveitar cache
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

# Baixa as dependências antes de copiar o código-fonte
RUN ./mvnw dependency:go-offline -B

# Copia o código
COPY src ./src

# Gera o JAR
RUN ./mvnw clean package -DskipTests -B


# ============================================================
# Runtime Stage
# ============================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia somente o artefato gerado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]