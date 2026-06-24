# Algorytm Genetyczny dla Problemu Komiwojażera (TSP)

## Opisanie projektu

Program implementuje Algorytm Genetyczny (GA) do rozwiązywania problemu Komiwojażera (Traveling Salesman Problem - TSP). Projekt spełnia wszystkie 4 kryteria zadania:

1. **Porównanie metod krzyżowania** - implementuje Order Crossover (OX) i Partially Mapped Crossover (PMX)
2. **Algorytm wysypowy (Hill Climbing)** - zastosowany w algorytmie memetycznym
3. **Zrównolegnienie obliczeń** - użycie ExecutorService do przetwarzania wielowątkowego
4. **Algorytmy memetyczne** - połączenie GA z optymalizacją lokalną

## Struktura projektu

### Klasy główne:

- **Main.java** - interfejs użytkownika z menu
- **GeneticAlgorithm.java** - główna implementacja algorytmu genetycznego
- **Individual.java** - reprezentacja chromosomu (rozwiązania TSP)
- **TSPProblem.java** - problem TSP i obliczanie odległości
- **TSPInstanceReader.java** - czytanie plików TSP

### Metody selekcji:

- **SelectionMethod.java** - interfejs dla metod selekcji
- **TournamentSelection.java** - selekcja turniejowa
- **RouletteWheelSelection.java** - selekcja ruletką

### Metody krzyżowania:

- **CrossoverMethod.java** - interfejs dla metod krzyżowania
- **OrderCrossover.java** - Order Crossover (OX)
- **PartiallyMappedCrossover.java** - Partially Mapped Crossover (PMX)

### Optymalizacja:

- **MutationOperator.java** - operator mutacji (inwersja segmentu)
- **HillClimber.java** - algorytm hill climbing (2-opt)
- **ExperimentRunner.java** - narzędzie do eksperymentów porównawczych

## Kompilacja

```bash
cd c:\Studia\SVN-AM\3
javac -encoding UTF-8 *.java
```

## Uruchomienie

```bash
java Main
```

Program zapyta o ścieżkę do pliku TSP, np:
```
test25.tsp
```

## Format pliku TSP

Program obsługuje format TSP z sekcją NODE_COORD_SECTION:

```
NAME: test25
DIMENSION: 25
EDGE_WEIGHT_TYPE: EUC_2D
NODE_COORD_SECTION
 1 200 800
 2 429 553
 ...
EOF
```

## Menu aplikacji

```
1. Uruchom pojedynczy algorytm GA
   - Uruchamia jedną instancję algorytmu
   - Pozwala wybrać parametry populacji i generacji
   - Opcja algorytmu memetycznego

2. Porównaj metody selekcji (100 eksperymentów)
   - Porównuje selekcję turniejową vs ruletką
   - Wyświetla statystyki: min, max, średnia, mediana, odch. stand.

3. Porównaj metody krzyżowania (100 eksperymentów)
   - Porównuje Order Crossover vs Partially Mapped Crossover
   - Wyświetla porównawczą analizę wydajności

4. Porównaj GA vs Algorytm Memetyczny (100 eksperymentów)
   - Pokazuje wpływ hill climbingu na jakość rozwiązań
   - Wyświetla zbiorczą statystykę

5. Załaduj inny plik TSP
   - Pozwala zmienić problem podczas działania
```

## Parametry algoritmu

Domyślne parametry (można zmienić w menu):
- **Rozmiar populacji**: 100
- **Liczba generacji**: 1000
- **Prawdopodobieństwo mutacji**: 0.02 (2%)
- **Rozmiar turnieju**: 5 (dla selekcji turniejowej)
- **Hill climbing**: co 5 generacji (dla algorytmu memetycznego)

## Opisy algorytmów

### 1. Wyznaczenie populacji początkowej (Krok 1)
- Generuje losowe permutacje miast
- Rozmiar populacji: konfigurowalny

### 2. Ocena i selekcja (Krok 2)
- Fitness = 1.0 / (1.0 + długość_trasy)
- Dwie metody selekcji:
  - **Turniejowa**: losuje k osobników, wybiera najlepszego
  - **Ruletka**: szansa proporcjonalna do fitness

### 3. Krzyżowanie (Krok 2)
- **Order Crossover (OX)**: zachowuje porządek miast
- **Partially Mapped Crossover (PMX)**: mapuje segmenty

### 4. Mutacja (Krok 4)
- **Inwersja**: odwraca losowy segment trasy
- Prawdopodobieństwo: 2%

### 5. Algorytm memetyczny (Krok 3)
- Stosuje hill climbing (2-opt) co 5 generacji
- Poprawia lokalne rozwiązania
- Nie powinno jednak się zagnieżdżać zgadzić?

## Przykład wyniku

```
Algorytm zakonczyl sie!
Dlugosc najlepszej trasy: 3457,15
Znaleziona w generacji: 258
Czas wykonania: 0,11 s
Najlepsza trasa: [13, 1, 24, 6, 14, 0, 2, 18, 4, 7, 22, 3, 23, 10, 20, 15, 21, 8, 19, 12, 11, 16, 9, 17, 5]
```

## Statystyki eksperymentów

Program wyświetla:
- **Najlepszy wynik** - minimum znalezione w serii eksperymentów
- **Najgorszy wynik** - maksimum znalezione
- **Średnia** - średnia arytmetyczna
- **Mediana** - wartość środkowa
- **Odchylenie standardowe** - miara rozproszenia
- **Wariancja** - rozproszenie do kwadratu

## Cechy techniczne

- **Języki**: Java 8+
- **Wielowątkowość**: ExecutorService (liczba wątków = liczba procesorów)
- **Wydajność**: 
  - Kompilacja: ~0.5s
  - Pojedynczy GA (25 miast, 1000 gen): ~0.1s
  - 100 eksperymentów: ~10s

## Testy

Przygotowany plik testowy: `test25.tsp` (25 miast)

Aby testować z własnym plikiem:
1. Przygotuj plik w formacie TSP
2. Uruchom program
3. Podaj ścieżkę do pliku

## Przydatne linki

- TSP TSPLIB: http://www.math.uwaterloo.ca/tsp/
- Implementacja GA: teoria algorytmów genetycznych

## Autor

Program napisany w Java jako implementacja algorytmów genetycznych dla problemu TSP.
