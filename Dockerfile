# Multi-stage build: compila com javac (sem Maven/Gradle) e roda numa imagem JRE enxuta.

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY build.sh ./
COPY src ./src
RUN chmod +x build.sh && ./build.sh

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /app/out ./out
# Diretório de persistência (arquivo texto append-only usado pelo HistoricoRepository).
RUN mkdir -p data

EXPOSE 8080
ENTRYPOINT ["java", "-cp", "out", "com.joaovitor.validador.Main"]
CMD ["8080"]
