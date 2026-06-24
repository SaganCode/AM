#!/bin/bash
# Build script for TSP Genetic Algorithm on Linux/Mac
# Kompiluje wszystkie pliki Java z obsługą znaków Unicode

echo "Kompilowanie programu TSP GA..."
javac -encoding UTF-8 *.java

if [ $? -eq 0 ]; then
    echo ""
    echo "[OK] Kompilacja zakonczona pomyslnie!"
    echo ""
    echo "Uruchomienie programu..."
    echo ""
    java Main
else
    echo ""
    echo "[BLAD] Kompilacja nie powiodla sie!"
    echo ""
fi
