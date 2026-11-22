#!/bin/sh
rm -rf out && mkdir out
javac -encoding UTF-8 -d out $(find src -name '*.java')
if [ "$1" = "consola" ]; then
  java -cp out escalera.app.MainConsola aleatoria
else
  java -cp out escalera.app.MainGrafico
fi
