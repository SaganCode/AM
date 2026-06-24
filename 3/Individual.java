import java.util.Arrays;

/**
 * Klasa reprezentująca chromosom (rozwiązanie TSP)
 */
public class Individual implements Comparable<Individual> {
    private int[] tour;
    private double fitness;
    private TSPProblem problem;
    private boolean fitnessCalculated = false;

    public Individual(int[] tour, TSPProblem problem) {
        this.tour = tour.clone();
        this.problem = problem;
    }

    public Individual(TSPProblem problem) {
        this.problem = problem;
        this.tour = new int[problem.getNumberOfCities()];
        
        // Inicjalizacja losową permutacją
        for (int i = 0; i < tour.length; i++) {
            tour[i] = i;
        }
        
        // Shuffle - permutacja losowa
        for (int i = tour.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            int temp = tour[i];
            tour[i] = tour[j];
            tour[j] = temp;
        }
    }

    /**
     * Oblicza fitness (odwrotność długości trasy)
     */
    public double calculateFitness() {
        double tourLength = problem.calculateTourLength(tour);
        this.fitness = 1.0 / (1.0 + tourLength); // Normalizacja do [0,1]
        this.fitnessCalculated = true;
        return fitness;
    }

    public double getFitness() {
        if (!fitnessCalculated) {
            calculateFitness();
        }
        return fitness;
    }

    public double getTourLength() {
        return problem.calculateTourLength(tour);
    }

    public int[] getTour() {
        return tour.clone();
    }

    public void setTour(int[] newTour) {
        this.tour = newTour.clone();
        this.fitnessCalculated = false;
    }

    @Override
    public int compareTo(Individual other) {
        return Double.compare(other.getFitness(), this.getFitness()); // Malejący porządek
    }

    @Override
    public String toString() {
        return String.format("Tour length: %.2f, Fitness: %.6f", getTourLength(), getFitness());
    }

    public Individual clone() {
        Individual copy = new Individual(tour, problem);
        copy.fitness = this.fitness;
        copy.fitnessCalculated = this.fitnessCalculated;
        return copy;
    }
}
