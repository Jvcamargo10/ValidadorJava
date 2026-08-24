#!/usr/bin/env bash
# Compila o projeto (sem Maven/Gradle — apenas javac, sem dependências externas).
# Escopo restrito a src/main: os testes (src/test/) têm seu próprio script, ./test.sh,
# para que o artefato de produção (e a imagem Docker) nunca inclua código de teste.
set -e
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
find src/main -name "*.java" > /tmp/validador-java-sources.txt
javac -d out @/tmp/validador-java-sources.txt
echo "Build OK -> ./out"
