# INSTRUKCJA OBSŁUGI PROGRAMU

## Wymagania

- Java 8 lub nowsza
- Windows, Linux lub macOS

## Instalacja i uruchomienie

### Opcja 1: Szybki start (Windows)

1. Rozpakuj pliki do folderu `c:\Studia\SVN-AM\3`
2. Kliknij dwukrotnie `build.bat`
3. Program się uruchomi automatycznie

### Opcja 2: Szybki start (Linux/Mac)

```bash
cd c:\Studia\SVN-AM\3
chmod +x build.sh
./build.sh
```

### Opcja 3: Manualna kompilacja i uruchomienie

```bash
cd c:\Studia\SVN-AM\3
javac -encoding UTF-8 *.java
java Main
```

## Krok po kroku: Pierwsze uruchomienie

### 1. Załadowanie pliku TSP

Po uruchomieniu programu zobaczysz:
```
====================================================
  Algorytm Genetyczny dla Problemu Komiwojazera (TSP)
====================================================

Podaj ścieżkę do pliku TSP (np. d25519.tsp):
```

**Wpisz**: `test25.tsp` (plik testowy)

Program załaduje plik i wyświetli:
```
[OK] Plik wczytany pomyslnie!
Problem: test25
Liczba miast: 25
```

### 2. Menu główne

Zobaczysz menu z 5 opcjami. Zacznij od opcji 1:

```
Wybierz opcję: 1
```

### 3. Uruchomienie pojedynczego algorytmu

Program zapyta o parametry:

```
Rozmiar populacji [100]: 
```
Naciśnij ENTER aby zaakceptować wartość domyślną 100.

```
Liczba generacji [1000]: 
```
Naciśnij ENTER aby zaakceptować 1000 generacji.

```
Użyć algorytm memetyczny? (t/n) [n]: 
```
Wpisz `n` aby testować standardowy GA, lub `t` aby testować algorytm memetyczny.

### 4. Obserwowanie wyniku

Program pokaże postęp co 50 generacji:
```
Generacja 50: Best: 4595,21, Avg: 0,00
Generacja 100: Best: 3862,95, Avg: 0,00
...
```

Po zakończeniu zobaczysz wynik:
```
[OK] Algorytm zakonczyl sie!
Dlugosc najlepszej trasy: 3457,15
Znaleziona w generacji: 258
Czas wykonania: 0,11 s
Najlepsza trasa: [13, 1, 24, 6, 14, 0, 2, 18, ...]
```

## Opcje menu - szczegółowy opis

### Opcja 1: Uruchom pojedynczy algorytm GA

Uruchamia jedną instancję algorytmu z wybranymi parametrami.

**Zalecaną konfigurację**:
- Rozmiar populacji: 100 (mniejsze = szybciej, większe = lepiej)
- Liczba generacji: 1000 (więcej generacji = lepsze rozwiązania)
- Algorytm memetyczny: Zalecane TAK dla lepszych wyników

### Opcja 2: Porównaj metody selekcji

Uruchamia 100 eksperymentów:
- 100x Selekcja turniejowa
- 100x Selekcja ruletką

Pokazuje statystyki obu metod, pozwala wybrać lepszą.

```
Liczba eksperymentów [100]: 
```

Po zakończeniu zobaczysz:
```
========== PORÓWNANIE METOD SELEKCJI ==========
--- Tournament Selection (k=5) ---
Najlepszy wynik                 : 3500.45
Najgorszy wynik                 : 4200.50
Średnia                         : 3750.25
Mediana                         : 3700.00
Odchylenie standardowe          : 150.30
Wariancja                       : 22590.09

--- Roulette Wheel Selection ---
Najlepszy wynik                 : 3450.20
...
```

### Opcja 3: Porównaj metody krzyżowania

Porównuje dwie metody krzyżowania:
- Order Crossover (OX)
- Partially Mapped Crossover (PMX)

Uruchamia 100 eksperymentów dla każdej metody.

### Opcja 4: Porównaj GA vs Algorytm Memetyczny

Pokazuje wpływ optymalizacji lokalnej (Hill Climbing) na wyniki.

- Первый test: standardowy GA (tylko selekcja + krzyżowanie + mutacja)
- Drugi test: GA + Hill Climbing (algorytm memetyczny)

Wyniki powinny pokazać, że algoritm memetyczny znajduje lepsze rozwiązania.

### Opcja 5: Załaduj inny plik TSP

Pozwala zmienić problem na inny plik:

```
Podaj ścieżkę do pliku TSP: moj_plik.tsp
```

Może to być:
- Plik lokalny: `test25.tsp`
- Pełna ścieżka: `C:\path\do\plik.tsp`

### Opcja 0: Wyjście

Zamyka program.

## Tworzenie własnych plików TSP

Format pliku TSP (przykład dla 5 miast):

```
NAME: moja_instancja
DIMENSION: 5
EDGE_WEIGHT_TYPE: EUC_2D
NODE_COORD_SECTION
 1 100 100
 2 200 150
 3 300 200
 4 250 100
 5 150 50
EOF
```

**Objaśnienie**:
- NAME: nazwa problemu
- DIMENSION: liczba miast
- EDGE_WEIGHT_TYPE: typ wag (EUC_2D = odległość euklidesowa)
- NODE_COORD_SECTION: współrzędne miast (id x y)
- EOF: koniec pliku

## Interpretacja wyników

### Długość trasy
- Im mniejsza = lepsze rozwiązanie

### Generacja znalezienia
- Numer generacji, w której znaleziono najlepsze rozwiązanie
- Niska liczba = algorytm szybko znalazł rozwiązanie
- Wysoka liczba = algorytm długo szukał

### Odchylenie standardowe
- Mała wartość = konsystentne rozwiązania
- Duża wartość = duże różnice między eksperymentami

## Problemy i rozwiązania

### Problem: Błąd "File not found"
**Rozwiązanie**: Sprawdź ścieżkę do pliku. Może być relatywna (test25.tsp) lub bezwzględna (C:\...\test25.tsp)

### Problem: Błąd kodowania (unmappable character)
**Rozwiązanie**: Upewnij się, że kompilujesz z flagą `-encoding UTF-8`:
```bash
javac -encoding UTF-8 *.java
```

### Problem: Algorytm bardzo powoli
**Rozwiązanie**: Zmniejsz liczbę generacji lub rozmiar populacji. Dla 25 miast to normalne.

## Porady optymalizacyjne

1. **Dla szybkich testów**: Populacja 50, Generacje 100
2. **Dla dobrych wyników**: Populacja 200, Generacje 1000
3. **Dla bardzo dobrych wyników**: Użyj algorytmu memetycznego (opcja +Hill Climbing)
4. **Dla eksperymentów**: Uruchom porównania (opcje 2, 3, 4)

## Wymagania systemowe

- **Procesor**: Dowolny (program używa multi-threading)
- **RAM**: Minimum 512MB
- **Dysk**: Minimum 10MB
- **System**: Windows, Linux lub macOS

## Kontakt i problemy

Jeśli napotkasz problemy:
1. Sprawdź czy Java jest zainstalowana: `java -version`
2. Sprawdź czy kompilacja powiodła się: czy widać pliki .class?
3. Sprawdź czy plik TSP ma prawidłowy format
