#!/usr/bin/env bash
# Roda o servidor (porta 8080 por padrão, ou a porta passada como argumento).
set -e
cd "$(dirname "$0")"
if [ ! -d out ]; then
  ./build.sh
fi
java -cp out com.joaovitor.validador.Main "${1:-8080}"
