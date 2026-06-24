/**
 * Interfejs dla metod krzyżowania
 */
public interface CrossoverMethod {
    /**
     * Krzyżuje dwa chromosomy i zwraca potomka
     */
    Individual crossover(Individual parent1, Individual parent2, TSPProblem problem);
    String getName();
}
