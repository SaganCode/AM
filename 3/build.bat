@echo off
REM Build script for TSP Genetic Algorithm
REM Kompiluje wszystkie pliki Java z obsługą znaków Unicode

echo Kompilowanie programu TSP GA...
javac -encoding UTF-8 *.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [OK] Kompilacja zakonczona pomyslnie!
    echo.
    echo Uruchomienie programu...
    echo.
    java Main
) else (
    echo.
    echo [BLAD] Kompilacja nie powiodla sie!
    echo.
)

pause
