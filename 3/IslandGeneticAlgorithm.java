import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementacja algorytmu wyspowego dla problemu TSP.
 */
public class IslandGeneticAlgorithm {
    private TSPProblem problem;
    private int populationSize;
    private int maxGenerations;
    private int progressStep;
    private int islandCount;
    private int migrationInterval;
    private int migrantsCount;
    private SelectionMethod selectionMethod;
    private CrossoverMethod crossoverMethod;
    private MutationOperator mutationOperator;
    private HillClimber hillClimber;
    private boolean useMemeticAlgorithm;
    private int bestGenerationFound;

    public IslandGeneticAlgorithm(TSPProblem problem, int populationSize, int maxGenerations,
                                  int islandCount, int migrationInterval, int migrantsCount,
                                  SelectionMethod selectionMethod, CrossoverMethod crossoverMethod,
                                  MutationOperator mutationOperator, boolean useMemeticAlgorithm) {
        this.problem = problem;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.islandCount = Math.max(2, islandCount);
        this.migrationInterval = Math.max(1, migrationInterval);
        this.migrantsCount = Math.max(1, Math.min(migrantsCount, populationSize / 2));
        this.selectionMethod = selectionMethod;
        this.crossoverMethod = crossoverMethod;
        this.mutationOperator = mutationOperator;
        this.useMemeticAlgorithm = useMemeticAlgorithm;
        this.bestGenerationFound = 0;
        this.progressStep = Math.max(1, maxGenerations / 20);

        if (useMemeticAlgorithm) {
            this.hillClimber = new HillClimber(problem, 3);
        }
    }

    public Individual solve() {
        List<List<Individual>> islands = initializeIslands();
        Individual globalBest = findGlobalBest(islands);

        for (int generation = 0; generation < maxGenerations; generation++) {
            // Ewolucja na każdej wyspie
            for (int i = 0; i < islandCount; i++) {
                islands.set(i, evolveIsland(islands.get(i), generation));
            }

            // Migracja między wyspami
            if (generation > 0 && generation % migrationInterval == 0) {
                islands = migrateIslands(islands);
            }

            // Aktualizacja najlepszego rozwiązania globalnego
            Individual generationBest = findGlobalBest(islands);
            if (generationBest.getTourLength() < globalBest.getTourLength()) {
                globalBest = generationBest.clone();
                bestGenerationFound = generation;
            }

            if (generation % progressStep == 0 || generation == maxGenerations - 1) {
                System.out.printf("Generacja %d/%d: najlepszy globalny wynik = %.2f%n",
                        generation + 1, maxGenerations, globalBest.getTourLength());
            }
        }

        return globalBest;
    }

    private List<List<Individual>> initializeIslands() {
        List<List<Individual>> islands = new ArrayList<>();
        for (int i = 0; i < islandCount; i++) {
            List<Individual> island = new ArrayList<>();
            for (int j = 0; j < populationSize; j++) {
                island.add(new Individual(problem));
            }
            islands.add(island);
        }
        return islands;
    }

    private List<Individual> evolveIsland(List<Individual> island, int generation) {
        evaluatePopulation(island);

        Individual islandBest = island.stream()
                .min(Comparator.comparingDouble(Individual::getTourLength))
                .orElse(island.get(0));

        List<Individual> selected = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            selected.add(selectionMethod.select(island));
        }

        List<Individual> offspring = new ArrayList<>();
        for (int i = 0; i < populationSize - 1; i += 2) {
            Individual child1 = crossoverMethod.crossover(selected.get(i), selected.get(i + 1), problem);
            Individual child2 = crossoverMethod.crossover(selected.get(i + 1), selected.get(i), problem);
            offspring.add(child1);
            offspring.add(child2);
        }
        if (populationSize % 2 == 1) {
            offspring.add(selected.get(populationSize - 1).clone());
        }

        for (Individual individual : offspring) {
            mutationOperator.mutate(individual);
        }

        if (useMemeticAlgorithm && generation % 5 == 0) {
            List<Individual> optimizedOffspring = new ArrayList<>();
            for (Individual individual : offspring) {
                optimizedOffspring.add(hillClimber.optimize(individual));
            }
            offspring = optimizedOffspring;
        }

        island.sort(Comparator.comparingDouble(Individual::getTourLength));
        int eliteCount = Math.max(1, populationSize / 10);
        List<Individual> nextGeneration = new ArrayList<>(island.subList(0, eliteCount));
        for (int i = 0; i < Math.min(populationSize - nextGeneration.size(), offspring.size()); i++) {
            nextGeneration.add(offspring.get(i));
        }
        while (nextGeneration.size() < populationSize) {
            nextGeneration.add(new Individual(problem));
        }

        return nextGeneration;
    }

    private void evaluatePopulation(List<Individual> population) {
        population.forEach(Individual::calculateFitness);
    }

    private List<List<Individual>> migrateIslands(List<List<Individual>> islands) {
        List<List<Individual>> nextIslands = new ArrayList<>();
        for (int i = 0; i < islandCount; i++) {
            nextIslands.add(new ArrayList<>(islands.get(i)));
        }

        for (int i = 0; i < islandCount; i++) {
            List<Individual> source = new ArrayList<>(islands.get(i));
            source.sort(Comparator.comparingDouble(Individual::getTourLength));
            List<Individual> migrants = new ArrayList<>();
            for (int j = 0; j < migrantsCount && j < source.size(); j++) {
                migrants.add(source.get(j).clone());
            }

            int destinationIndex = (i + 1) % islandCount;
            List<Individual> destination = new ArrayList<>(nextIslands.get(destinationIndex));
            destination.sort(Comparator.comparingDouble(Individual::getTourLength).reversed());

            for (int j = 0; j < migrants.size() && j < destination.size(); j++) {
                destination.set(j, migrants.get(j));
            }

            nextIslands.set(destinationIndex, destination);
        }

        return nextIslands;
    }

    private Individual findGlobalBest(List<List<Individual>> islands) {
        Individual best = null;
        for (List<Individual> island : islands) {
            for (Individual individual : island) {
                if (best == null || individual.getTourLength() < best.getTourLength()) {
                    best = individual;
                }
            }
        }
        return best != null ? best.clone() : new Individual(problem);
    }

    public int getBestGenerationFound() {
        return bestGenerationFound;
    }
}
