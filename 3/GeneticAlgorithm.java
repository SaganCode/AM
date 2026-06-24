import java.util.*;

/**
 * Główna klasa implementująca Algorytm Genetyczny
 */
public class GeneticAlgorithm {
    private TSPProblem problem;
    private int populationSize;
    private int maxGenerations;
    private int progressStep;
    private SelectionMethod selectionMethod;
    private CrossoverMethod crossoverMethod;
    private MutationOperator mutationOperator;
    private HillClimber hillClimber;
    private boolean useMemeticAlgorithm;
    private int bestGenerationFound;
    private List<Double> bestFitnessHistory;
    private List<Double> avgFitnessHistory;

    public GeneticAlgorithm(TSPProblem problem, int populationSize, int maxGenerations,
                           SelectionMethod selection, CrossoverMethod crossover,
                           MutationOperator mutation, boolean useMemeticAlgorithm) {
        this.problem = problem;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.selectionMethod = selection;
        this.crossoverMethod = crossover;
        this.mutationOperator = mutation;
        this.useMemeticAlgorithm = useMemeticAlgorithm;
        this.bestGenerationFound = 0;
        this.bestFitnessHistory = new ArrayList<>();
        this.avgFitnessHistory = new ArrayList<>();
        this.progressStep = Math.max(1, maxGenerations / 20);
        
        if (useMemeticAlgorithm) {
            this.hillClimber = new HillClimber(problem, 3);
        }
    }

    /**
     * Główna metoda uruchamiająca algorytm
     */
    public Individual solve() {
        // 1. Wyznaczenie populacji początkowej
        List<Individual> population = initializePopulation();
        
        Individual bestIndividual = population.stream()
                .min(Comparator.comparingDouble(Individual::getTourLength))
                .orElse(population.get(0));

        // 2-4. Iteracyjne wykonanie algorytmu
        for (int generation = 0; generation < maxGenerations; generation++) {
            // Ocena populacji
            evaluatePopulation(population);

            // Znalezienie najlepszego w tej generacji
            Individual generationBest = population.stream()
                    .min(Comparator.comparingDouble(Individual::getTourLength))
                    .orElse(population.get(0));

            if (generationBest.getTourLength() < bestIndividual.getTourLength()) {
                bestIndividual = generationBest.clone();
                bestGenerationFound = generation;
            }

            // Historia fitness
            double bestFitness = generationBest.getFitness();
            double avgFitness = population.stream()
                    .mapToDouble(Individual::getFitness)
                    .average()
                    .orElse(0);
            bestFitnessHistory.add(bestFitness);
            avgFitnessHistory.add(avgFitness);

            // Selekcja
            List<Individual> selected = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                selected.add(selectionMethod.select(population));
            }

            // Krzyżowanie
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

            // Mutacja
            for (Individual individual : offspring) {
                mutationOperator.mutate(individual);
            }

            // Algorytm memetyczny (Hill Climbing)
            if (useMemeticAlgorithm && generation % 5 == 0) { // Co 5 generacji
                List<Individual> optimizedOffspring = new ArrayList<>();
                for (Individual individual : offspring) {
                    optimizedOffspring.add(hillClimber.optimize(individual));
                }
                offspring = optimizedOffspring;
            }

            // Elitaryzm - zachowaj najlepszych
            population.sort(Comparator.comparingDouble(Individual::getTourLength));
            population = new ArrayList<>(population.subList(0, Math.min(populationSize / 10, population.size())));
            population.addAll(offspring.subList(0, populationSize - population.size()));

            if (generation % progressStep == 0 || generation == maxGenerations - 1) {
                System.out.printf("Generacja %d/%d: najlepszy = %.2f, średnia fitness = %.6f%n",
                        generation + 1, maxGenerations, bestIndividual.getTourLength(), avgFitness);
            }
        }

        return bestIndividual;
    }

    /**
     * 1. Wyznaczenie populacji początkowej
     */
    private List<Individual> initializePopulation() {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(new Individual(problem));
        }
        return population;
    }

    /**
     * Ocena populacji
     */
    private void evaluatePopulation(List<Individual> population) {
        population.forEach(Individual::calculateFitness);
    }

    public List<Double> getBestFitnessHistory() {
        return bestFitnessHistory;
    }

    public List<Double> getAvgFitnessHistory() {
        return avgFitnessHistory;
    }

    public int getBestGenerationFound() {
        return bestGenerationFound;
    }
}
