#!/usr/bin/env bash
# Compila src/main + src/test e roda o TestRunner (harness de testes próprio, sem
# JUnit/Maven — ver a seção "Testes" do README para o porquê). Sai com código != 0
# se qualquer teste falhar, para que ./ci.yml marque o build como quebrado.
set -e
cd "$(dirname "$0")"
rm -rf out-test
mkdir -p out-test
find src/main src/test -name "*.java" > /tmp/validador-java-test-sources.txt
javac -d out-test @/tmp/validador-java-test-sources.txt
java -cp out-test com.joaovitor.validador.TestRunner
