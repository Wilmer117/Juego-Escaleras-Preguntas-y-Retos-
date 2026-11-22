@echo off
chcp 65001 > nul
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > fuentes.txt
javac -encoding UTF-8 -d out @fuentes.txt
del fuentes.txt
echo.
echo 1) Corrida de verificacion en consola
echo 2) Interfaz grafica
set /p opcion=Elige 1 o 2: 
if "%opcion%"=="1" (java -cp out escalera.app.MainConsola aleatoria) else (java -cp out escalera.app.MainGrafico)
pause
