import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TspMetaheuristics {
    public static void main(String[] args) throws IOException {
        int smallSize = 200;
        int largeSize = 1200;
        String filePath = args.length > 0 ? args[0] : null;
        TspInstance fileInstance = null;
        if (filePath != null) {
            System.out.println("Wczytywanie instancji z pliku: " + filePath);
            fileInstance = TspInstance.fromTsplibFile(filePath);
            System.out.println("Wczytano instancję o rozmiarze: " + fileInstance.n);
        }

        System.out.println("=== Symulowane wyżarzanie (Simulated Annealing) ===");
        runSaExperiment(smallSize, largeSize, fileInstance);

        System.out.println();
        System.out.println("=== Tabu Search ===");
        runTabuExperiment(smallSize, largeSize, fileInstance);
    }

    private static void runSaExperiment(int smallSize, int largeSize, TspInstance fileInstance) {
        long seed = 123456789L;
        TspInstance smallInstance = fileInstance != null ? fileInstance : TspInstance.randomEuclidean(smallSize, seed);
        TspInstance largeInstance = TspInstance.randomEuclidean(largeSize, seed + 1);

        List<SimulatedAnnealing.Parameters> candidates = Arrays.asList(
            new SimulatedAnnealing.Parameters(1e4, 0.995, 500, 1e-3),
            new SimulatedAnnealing.Parameters(1e4, 0.990, 300, 1e-3),
            new SimulatedAnnealing.Parameters(5e3, 0.995, 500, 1e-3),
            new SimulatedAnnealing.Parameters(1e3, 0.998, 800, 1e-3)
        );

        System.out.println("Parametry testowe (małe dane):");
        double bestAvg = Double.POSITIVE_INFINITY;
        SimulatedAnnealing.Parameters bestParams = null;
        for (SimulatedAnnealing.Parameters params : candidates) {
            double avg = evaluateSaOnInstance(smallInstance, params, 10, seed);
            System.out.printf("  %s -> średnia: %.4f%n", params, avg);
            if (avg < bestAvg) {
                bestAvg = avg;
                bestParams = params;
            }
        }

        System.out.printf("Wybrane parametry: %s (średnia %.4f)%n", bestParams, bestAvg);

        System.out.println("Wyniki dla dużych danych (100 prób):");
        evaluateSaOnLargeInstance(largeInstance, bestParams, 100, seed + 2);
    }

    private static double evaluateSaOnInstance(TspInstance instance, SimulatedAnnealing.Parameters params, int runs, long baseSeed) {
        double total = 0.0;
        for (int run = 0; run < runs; run++) {
            SimulatedAnnealing sa = new SimulatedAnnealing(instance, params, baseSeed + run);
            Solution result = sa.solve();
            total += result.cost;
        }
        return total / runs;
    }

    private static void evaluateSaOnLargeInstance(TspInstance instance, SimulatedAnnealing.Parameters params, int runs, long baseSeed) {
        double best = Double.POSITIVE_INFINITY;
        double sum = 0.0;
        for (int run = 0; run < runs; run++) {
            SimulatedAnnealing sa = new SimulatedAnnealing(instance, params, baseSeed + run);
            Solution result = sa.solve();
            best = Math.min(best, result.cost);
            sum += result.cost;
            System.out.printf("  próba %3d: koszt %.4f%n", run + 1, result.cost);
        }
        System.out.printf("Najlepsze rozwiązanie: %.4f%n", best);
        System.out.printf("Średni koszt: %.4f%n", sum / runs);
    }

    private static void runTabuExperiment(int smallSize, int largeSize, TspInstance fileInstance) {
        long seed = 987654321L;
        TspInstance smallInstance = fileInstance != null ? fileInstance : TspInstance.randomEuclidean(smallSize, seed);
        TspInstance largeInstance = TspInstance.randomEuclidean(largeSize, seed + 1);

        List<TabuSearch.Parameters> candidates = Arrays.asList(
            new TabuSearch.Parameters(15, 1200, 80),
            new TabuSearch.Parameters(20, 1000, 100),
            new TabuSearch.Parameters(25, 800, 120),
            new TabuSearch.Parameters(10, 1500, 60)
        );

        System.out.println("Parametry testowe (małe dane):");
        double bestAvg = Double.POSITIVE_INFINITY;
        TabuSearch.Parameters bestParams = null;
        for (TabuSearch.Parameters params : candidates) {
            double avg = evaluateTabuOnInstance(smallInstance, params, 10, seed);
            System.out.printf("  %s -> średnia: %.4f%n", params, avg);
            if (avg < bestAvg) {
                bestAvg = avg;
                bestParams = params;
            }
        }

        System.out.printf("Wybrane parametry: %s (średnia %.4f)%n", bestParams, bestAvg);

        System.out.println("Wyniki dla dużych danych (100 prób):");
        evaluateTabuOnLargeInstance(largeInstance, bestParams, 100, seed + 2);
    }

    private static double evaluateTabuOnInstance(TspInstance instance, TabuSearch.Parameters params, int runs, long baseSeed) {
        double total = 0.0;
        for (int run = 0; run < runs; run++) {
            TabuSearch ts = new TabuSearch(instance, params, baseSeed + run);
            Solution result = ts.solve();
            total += result.cost;
        }
        return total / runs;
    }

    private static void evaluateTabuOnLargeInstance(TspInstance instance, TabuSearch.Parameters params, int runs, long baseSeed) {
        double best = Double.POSITIVE_INFINITY;
        double sum = 0.0;
        for (int run = 0; run < runs; run++) {
            TabuSearch ts = new TabuSearch(instance, params, baseSeed + run);
            Solution result = ts.solve();
            best = Math.min(best, result.cost);
            sum += result.cost;
            System.out.printf("  próba %3d: koszt %.4f%n", run + 1, result.cost);
        }
        System.out.printf("Najlepsze rozwiązanie: %.4f%n", best);
        System.out.printf("Średni koszt: %.4f%n", sum / runs);
    }

    public static class TspInstance {
        public final int n;
        public final double[][] dist;

        private TspInstance(int n, double[][] dist) {
            this.n = n;
            this.dist = dist;
        }

        public static TspInstance randomEuclidean(int n, long seed) {
            Random random = new Random(seed);
            double[] x = new double[n];
            double[] y = new double[n];
            for (int i = 0; i < n; i++) {
                x[i] = random.nextDouble();
                y[i] = random.nextDouble();
            }
            double[][] dist = new double[n][n];
            for (int i = 0; i < n; i++) {
                dist[i][i] = 0.0;
                for (int j = i + 1; j < n; j++) {
                    double dx = x[i] - x[j];
                    double dy = y[i] - y[j];
                    double d = Math.hypot(dx, dy);
                    dist[i][j] = d;
                    dist[j][i] = d;
                }
            }
            return new TspInstance(n, dist);
        }

        public static TspInstance fromTsplibFile(String filePath) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            List<double[]> coords = new ArrayList<>();
            boolean inSection = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!inSection) {
                    if (line.startsWith("NODE_COORD_SECTION")) {
                        inSection = true;
                    }
                    continue;
                }
                if (line.equals("EOF")) {
                    break;
                }
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    coords.add(new double[]{x, y});
                }
            }
            reader.close();

            int n = coords.size();
            double[] x = new double[n];
            double[] y = new double[n];
            for (int i = 0; i < n; i++) {
                x[i] = coords.get(i)[0];
                y[i] = coords.get(i)[1];
            }
            double[][] dist = new double[n][n];
            for (int i = 0; i < n; i++) {
                dist[i][i] = 0.0;
                for (int j = i + 1; j < n; j++) {
                    double dx = x[i] - x[j];
                    double dy = y[i] - y[j];
                    double d = Math.hypot(dx, dy);
                    dist[i][j] = d;
                    dist[j][i] = d;
                }
            }
            return new TspInstance(n, dist);
        }

        public Solution randomSolution(long seed) {
            int[] tour = new int[n];
            for (int i = 0; i < n; i++) {
                tour[i] = i;
            }
            Random random = new Random(seed);
            for (int i = n - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int tmp = tour[i];
                tour[i] = tour[j];
                tour[j] = tmp;
            }
            double cost = evaluateTour(tour);
            return new Solution(tour, cost);
        }

        public double evaluateTour(int[] tour) {
            double sum = 0.0;
            for (int i = 0; i < tour.length; i++) {
                int a = tour[i];
                int b = tour[(i + 1) % tour.length];
                sum += dist[a][b];
            }
            return sum;
        }
    }

    public static class Solution {
        public final int[] tour;
        public final double cost;

        public Solution(int[] tour, double cost) {
            this.tour = tour;
            this.cost = cost;
        }
    }

    public static class SimulatedAnnealing {
        public final Parameters params;
        private final TspInstance instance;
        private final Random random;

        public SimulatedAnnealing(TspInstance instance, Parameters params, long seed) {
            this.instance = instance;
            this.params = params;
            this.random = new Random(seed);
        }

        public Solution solve() {
            int n = instance.n;
            int[] currentTour = instance.randomSolution(random.nextLong()).tour;
            double currentCost = instance.evaluateTour(currentTour);
            int[] bestTour = Arrays.copyOf(currentTour, n);
            double bestCost = currentCost;
            double temperature = params.initialTemperature;

            while (temperature > params.minTemperature) {
                for (int step = 0; step < params.iterationsPerTemperature; step++) {
                    int i = random.nextInt(n);
                    int j = random.nextInt(n);
                    if (i == j) {
                        continue;
                    }
                    double delta = deltaSwap(currentTour, i, j, instance.dist);
                    if (delta < 0 || random.nextDouble() < Math.exp(-delta / temperature)) {
                        swap(currentTour, i, j);
                        currentCost += delta;
                        if (currentCost < bestCost) {
                            bestCost = currentCost;
                            bestTour = Arrays.copyOf(currentTour, n);
                        }
                    }
                }
                temperature *= params.coolingRate;
            }
            return new Solution(bestTour, bestCost);
        }

        private static void swap(int[] tour, int i, int j) {
            int t = tour[i];
            tour[i] = tour[j];
            tour[j] = t;
        }

        private static double deltaSwap(int[] tour, int i, int j, double[][] dist) {
            int n = tour.length;
            int a = tour[i];
            int b = tour[j];
            int aPrev = tour[(i - 1 + n) % n];
            int aNext = tour[(i + 1) % n];
            int bPrev = tour[(j - 1 + n) % n];
            int bNext = tour[(j + 1) % n];

            if (i + 1 == j) {
                return (dist[aPrev][b] + dist[a][bNext]) - (dist[aPrev][a] + dist[b][bNext]);
            }
            if (j + 1 == i) {
                return (dist[bPrev][a] + dist[b][aNext]) - (dist[bPrev][b] + dist[a][aNext]);
            }
            double removed = dist[aPrev][a] + dist[a][aNext] + dist[bPrev][b] + dist[b][bNext];
            double added = dist[aPrev][b] + dist[b][aNext] + dist[bPrev][a] + dist[a][bNext];
            if (aPrev == b || aNext == b || bPrev == a || bNext == a) {
                removed = dist[aPrev][a] + dist[a][b] + dist[b][bNext];
                added = dist[aPrev][b] + dist[b][a] + dist[a][bNext];
            }
            return added - removed;
        }

        public static class Parameters {
            public final double initialTemperature;
            public final double coolingRate;
            public final int iterationsPerTemperature;
            public final double minTemperature;

            public Parameters(double initialTemperature, double coolingRate, int iterationsPerTemperature, double minTemperature) {
                this.initialTemperature = initialTemperature;
                this.coolingRate = coolingRate;
                this.iterationsPerTemperature = iterationsPerTemperature;
                this.minTemperature = minTemperature;
            }

            @Override
            public String toString() {
                return String.format("T0=%.0f, alpha=%.3f, iterTemp=%d", initialTemperature, coolingRate, iterationsPerTemperature);
            }
        }
    }

    public static class TabuSearch {
        public final Parameters params;
        private final TspInstance instance;
        private final Random random;

        public TabuSearch(TspInstance instance, Parameters params, long seed) {
            this.instance = instance;
            this.params = params;
            this.random = new Random(seed);
        }

        public Solution solve() {
            int n = instance.n;
            int[] current = instance.randomSolution(random.nextLong()).tour;
            double currentCost = instance.evaluateTour(current);
            int[] bestTour = Arrays.copyOf(current, n);
            double bestCost = currentCost;
            int[][] tabu = new int[n][n];
            int iteration = 0;

            while (iteration < params.maxIterations) {
                int bestI = -1;
                int bestJ = -1;
                double bestDelta = Double.POSITIVE_INFINITY;
                for (int candidate = 0; candidate < params.candidateSamples; candidate++) {
                    int i = random.nextInt(n);
                    int j = random.nextInt(n);
                    if (i == j) {
                        continue;
                    }
                    if (i > j) {
                        int tmp = i;
                        i = j;
                        j = tmp;
                    }
                    double delta = deltaSwap(current, i, j, instance.dist);
                    boolean isTabu = tabu[current[i]][current[j]] > iteration;
                    if (isTabu && currentCost + delta >= bestCost) {
                        continue;
                    }
                    if (delta < bestDelta) {
                        bestDelta = delta;
                        bestI = i;
                        bestJ = j;
                    }
                }

                if (bestI < 0) {
                    break;
                }

                swap(current, bestI, bestJ);
                currentCost += bestDelta;
                int a = current[bestI];
                int b = current[bestJ];
                tabu[a][b] = iteration + params.tabuTenure;
                tabu[b][a] = iteration + params.tabuTenure;

                if (currentCost < bestCost) {
                    bestCost = currentCost;
                    bestTour = Arrays.copyOf(current, n);
                }
                iteration++;
            }

            return new Solution(bestTour, bestCost);
        }

        private static void swap(int[] tour, int i, int j) {
            int t = tour[i];
            tour[i] = tour[j];
            tour[j] = t;
        }

        private static double deltaSwap(int[] tour, int i, int j, double[][] dist) {
            int n = tour.length;
            int a = tour[i];
            int b = tour[j];
            int aPrev = tour[(i - 1 + n) % n];
            int aNext = tour[(i + 1) % n];
            int bPrev = tour[(j - 1 + n) % n];
            int bNext = tour[(j + 1) % n];

            if (i + 1 == j) {
                return (dist[aPrev][b] + dist[a][bNext]) - (dist[aPrev][a] + dist[b][bNext]);
            }
            if (j + 1 == i) {
                return (dist[bPrev][a] + dist[b][aNext]) - (dist[bPrev][b] + dist[a][aNext]);
            }
            double removed = dist[aPrev][a] + dist[a][aNext] + dist[bPrev][b] + dist[b][bNext];
            double added = dist[aPrev][b] + dist[b][aNext] + dist[bPrev][a] + dist[a][bNext];
            if (aPrev == b || aNext == b || bPrev == a || bNext == a) {
                removed = dist[aPrev][a] + dist[a][b] + dist[b][bNext];
                added = dist[aPrev][b] + dist[b][a] + dist[a][bNext];
            }
            return added - removed;
        }

        public static class Parameters {
            public final int tabuTenure;
            public final int maxIterations;
            public final int candidateSamples;

            public Parameters(int tabuTenure, int maxIterations, int candidateSamples) {
                this.tabuTenure = tabuTenure;
                this.maxIterations = maxIterations;
                this.candidateSamples = candidateSamples;
            }

            @Override
            public String toString() {
                return String.format("tenur=%d, maxIter=%d, cand=%d", tabuTenure, maxIterations, candidateSamples);
            }
        }
    }
}
