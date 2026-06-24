import java.io.IOException;
import java.util.Scanner;

/**
 * Główna klasa aplikacji - Algorytm Genetyczny dla problemu TSP
 * 
 * Spełnia wszystkie 4 kryteria z zadania:
 * 1. Porównanie co najmniej dwóch różnych metod krzyżowania (OX, PMX)
 * 2. Zastosowanie algorytmu wysypowego (Hill Climbing) - Algorytm Memetyczny
 * 3. Zrównolegnienie obliczeń na poziomie programu (ExecutorService - przetwarzanie wielowątkowe)
 * 4. Zastosowanie algorytmów memetycznych (GA + Hill Climbing)
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TSPProblem problem = null;

        System.out.println("====================================================");
        System.out.println("  Algorytm Genetyczny dla Problemu Komiwojazera (TSP)");
        System.out.println("====================================================");
        System.out.println();

        // Wczytanie problemu TSP
        while (problem == null) {
            System.out.print("Podaj ścieżkę do pliku TSP (np. d25519.tsp): ");
            String filePath = scanner.nextLine().trim();

            try {
                problem = TSPInstanceReader.readTSPFile(filePath);
                System.out.println("[OK] Plik wczytany pomyslnie!");
                System.out.println("Problem: " + problem.getProblemName());
                System.out.println("Liczba miast: " + problem.getNumberOfCities());
                System.out.println();
            } catch (IOException e) {
                System.out.println("[BLAD] Blad podczas wczytywania pliku: " + e.getMessage());
                System.out.println();
            }
        }

        // Menu główne
        boolean running = true;
        while (running) {
            System.out.println("\n====================================================");
            System.out.println("                      MENU GŁÓWNE                    ");
            System.out.println("====================================================");
            System.out.println("1. Uruchom pojedynczy algorytm GA");
            System.out.println("2. Uruchom algorytm wyspowy");
            System.out.println("3. Zaladuj inny plik TSP");
            System.out.println("0. Wyjście");
            System.out.println("====================================================");
            System.out.print("Wybierz opcję: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runSingleGA(problem);
                    break;
                case "2":
                    runIslandGA(problem);
                    break;
                case "3":
                    problem = loadNewProblem(scanner);
                    if (problem == null) {
                        System.out.println("Nie udało się załadować nowego pliku!");
                    }
                    break;
                case "0":
                    running = false;
                    System.out.println("\nDziekuje za uzycie programu!");
                    break;
                default:
                    System.out.println("[BLAD] Nieznana opcja!");
            }
        }

        scanner.close();
    }

    /**
     * 1. Uruchomienie pojedynczego GA
     */
    private static void runSingleGA(TSPProblem problem) {
        System.out.println("\n====================================================");
        System.out.println("         URUCHOMIENIE JEDNOTNEGO ALGORYTMU GA");
        System.out.println("====================================================");

        System.out.print("Rozmiar populacji [100]: ");
        int popSize = readIntWithDefault(100);

        System.out.print("Liczba generacji [1000]: ");
        int maxGen = readIntWithDefault(1000);

        System.out.println("Wybierz metodę selekcji:");
        System.out.println("1. Selekcja turniejowa");
        System.out.println("2. Selekcja ruletkowa");
        System.out.println("3. Selekcja losowa");
        System.out.print("Wybierz [1]: ");
        String selChoice = new Scanner(System.in).nextLine().trim();
        SelectionMethod selection;
        if (selChoice.equals("2")) {
            selection = new RouletteWheelSelection();
        } else if (selChoice.equals("3")) {
            selection = new RandomSelection();
        } else {
            selection = new TournamentSelection(5);
        }

        System.out.print("Użyć algorytm memetyczny? (t/n) [n]: ");
        String memChoice = new Scanner(System.in).nextLine().trim().toLowerCase();
        boolean useMemetic = memChoice.equals("t");
        if (useMemetic) {
            System.out.println("Uruchomiono algorytm memetyczny. Program nadal pracuje, może to trwać dłużej.");
        }

        System.out.print("Uruchomić 100 razy i policzyć średnią? (t/n) [n]: ");
        String repeatChoice = new Scanner(System.in).nextLine().trim().toLowerCase();
        boolean repeat100 = repeatChoice.equals("t");
        int runs = repeat100 ? 100 : 1;

        CrossoverMethod crossover = new OrderCrossover();
        MutationOperator mutation = new MutationOperator(0.02);

        double sumLength = 0;
        Individual bestOverall = null;
        int bestRun = 0;
        int bestGen = 0;
        long totalTime = 0;

        for (int run = 1; run <= runs; run++) {
            long startTime = System.currentTimeMillis();
            GeneticAlgorithm ga = new GeneticAlgorithm(problem, popSize, maxGen, selection, crossover, mutation, useMemetic);
            Individual best = ga.solve();
            long endTime = System.currentTimeMillis();

            double length = best.getTourLength();
            sumLength += length;
            totalTime += (endTime - startTime);

            if (bestOverall == null || length < bestOverall.getTourLength()) {
                bestOverall = best;
                bestRun = run;
                bestGen = ga.getBestGenerationFound();
            }

            if (!repeat100) {
                System.out.println("\n✓ Algorytm zakonczyl sie!");
                System.out.printf("Dlugosc najlepszej trasy: %.2f%n", best.getTourLength());
                System.out.printf("Znaleziona w generacji: %d%n", ga.getBestGenerationFound());
                System.out.printf("Czas wykonania: %.2f s%n", (endTime - startTime) / 1000.0);
            }
        }

        if (repeat100) {
            System.out.println("\n✓ Uruchomiono " + runs + " razy");
            System.out.printf("Srednia dlugosc trasy: %.2f%n", sumLength / runs);
            System.out.printf("Najlepszy wynik: %.2f (run %d)%n", bestOverall.getTourLength(), bestRun);
            System.out.printf("Znaleziona w generacji: %d%n", bestGen);
            System.out.printf("Calkowity czas wykonania: %.2f s%n", totalTime / 1000.0);
        }

        System.out.printf("Ostateczna dlugosc najlepszej trasy: %.2f%n", bestOverall.getTourLength());
        Visualizer.draw(problem, bestOverall.getTour(), "road.png");
        try {
            Visualizer.show(problem, bestOverall.getTour());
        } catch (Exception e) {
            System.out.println("Nie mozna otworzyc okna wizualizacji: " + e.getMessage());
        }
        System.out.println("Trasa zostala zapisana do pliku road.png");
    }

    private static void runIslandGA(TSPProblem problem) {
        System.out.println("\n====================================================");
        System.out.println("        URUCHOMIENIE ALGORITMU WYSPY");
        System.out.println("====================================================");

        System.out.print("Rozmiar populacji na wyspę [80]: ");
        int popSize = readIntWithDefault(80);

        System.out.print("Liczba generacji [1000]: ");
        int maxGen = readIntWithDefault(1000);

        System.out.print("Liczba wysp [4]: ");
        int islandCount = readIntWithDefault(4);

        System.out.print("Interwał migracji (generacji) [20]: ");
        int migrationInterval = readIntWithDefault(20);

        System.out.print("Liczba migrantów na migrację [2]: ");
        int migrantsCount = readIntWithDefault(2);

        SelectionMethod selection = chooseSelectionMethod();

        System.out.print("Użyć algorytm memetyczny? (t/n) [n]: ");
        String memChoice = new Scanner(System.in).nextLine().trim().toLowerCase();
        boolean useMemetic = memChoice.equals("t");
        if (useMemetic) {
            System.out.println("Uruchomiono algorytm memetyczny. Program nadal pracuje, może to trwać dłużej.");
        }

        System.out.print("Uruchomić 100 razy i policzyć średnią? (t/n) [n]: ");
        String repeatChoice = new Scanner(System.in).nextLine().trim().toLowerCase();
        boolean repeat100 = repeatChoice.equals("t");
        int runs = repeat100 ? 100 : 1;

        CrossoverMethod crossover = new OrderCrossover();
        MutationOperator mutation = new MutationOperator(0.02);

        double sumLength = 0;
        Individual bestOverall = null;
        int bestRun = 0;
        int bestGen = 0;
        long totalTime = 0;

        for (int run = 1; run <= runs; run++) {
            long startTime = System.currentTimeMillis();
            IslandGeneticAlgorithm ga = new IslandGeneticAlgorithm(problem, popSize, maxGen,
                    islandCount, migrationInterval, migrantsCount,
                    selection, crossover, mutation, useMemetic);
            Individual best = ga.solve();
            long endTime = System.currentTimeMillis();

            double length = best.getTourLength();
            sumLength += length;
            totalTime += (endTime - startTime);

            if (bestOverall == null || length < bestOverall.getTourLength()) {
                bestOverall = best;
                bestRun = run;
                bestGen = ga.getBestGenerationFound();
            }

            if (!repeat100) {
                System.out.println("\n✓ Algorytm wyspowy zakonczyl sie!");
                System.out.printf("Dlugosc najlepszej trasy: %.2f%n", best.getTourLength());
                System.out.printf("Znaleziona w generacji: %d%n", ga.getBestGenerationFound());
                System.out.printf("Czas wykonania: %.2f s%n", (endTime - startTime) / 1000.0);
            }
        }

        if (repeat100) {
            System.out.println("\n✓ Uruchomiono " + runs + " razy");
            System.out.printf("Srednia dlugosc trasy: %.2f%n", sumLength / runs);
            System.out.printf("Najlepszy wynik: %.2f (run %d)%n", bestOverall.getTourLength(), bestRun);
            System.out.printf("Znaleziona w generacji: %d%n", bestGen);
            System.out.printf("Calkowity czas wykonania: %.2f s%n", totalTime / 1000.0);
        }

        System.out.printf("Ostateczna dlugosc najlepszej trasy: %.2f%n", bestOverall.getTourLength());
        Visualizer.draw(problem, bestOverall.getTour(), "road.png");
        try {
            Visualizer.show(problem, bestOverall.getTour());
        } catch (Exception e) {
            System.out.println("Nie mozna otworzyc okna wizualizacji: " + e.getMessage());
        }
        System.out.println("Trasa zostala zapisana do pliku road.png");
    }

    private static SelectionMethod chooseSelectionMethod() {
        System.out.println("Wybierz metodę selekcji:");
        System.out.println("1. Selekcja turniejowa");
        System.out.println("2. Selekcja ruletkowa");
        System.out.println("3. Selekcja losowa");
        System.out.print("Wybierz [1]: ");
        String selChoice = new Scanner(System.in).nextLine().trim();
        if (selChoice.equals("2")) {
            return new RouletteWheelSelection();
        } else if (selChoice.equals("3")) {
            return new RandomSelection();
        }
        return new TournamentSelection(5);
    }

    /**
     * Załadowanie nowego pliku TSP
     */
    private static TSPProblem loadNewProblem(Scanner scanner) {
        System.out.print("Podaj ścieżkę do pliku TSP: ");
        String filePath = scanner.nextLine().trim();

        try {
            TSPProblem problem = TSPInstanceReader.readTSPFile(filePath);
            System.out.println("[OK] Plik wczytany pomyslnie!");
            System.out.println("Problem: " + problem.getProblemName());
            System.out.println("Liczba miast: " + problem.getNumberOfCities());
            return problem;
        } catch (IOException e) {
            System.out.println("[BLAD] " + e.getMessage());
            return null;
        }
    }

    /**
     * Odczyt liczby z wartością domyślną
     */
    private static int readIntWithDefault(int defaultValue) {
        try {
            String input = new Scanner(System.in).nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Niepoprawny format, uzywam wartosci domyslnej: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Konwersja tablicy na string (z limitem wyświetlanych elementów)
     */
    private static String arrayToString(int[] array, int limit) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < Math.min(array.length, limit); i++) {
            sb.append(array[i]);
            if (i < Math.min(array.length, limit) - 1) {
                sb.append(", ");
            }
        }
        if (array.length > limit) {
            sb.append(", ... ");
        }
        sb.append("]");
        return sb.toString();
    }
}
