/**
 * Algorytm wysypowy (Hill Climbing) - optymalizacja lokalna
 * Używany w algorytmach memetycznych
 */
public class HillClimber {
    private TSPProblem problem;
    private int maxIterations;

    public HillClimber(TSPProblem problem, int maxIterations) {
        this.problem = problem;
        this.maxIterations = maxIterations;
    }

    /**
     * Optymalizuje rozwiązanie metodą hill climbing
     */
    public Individual optimize(Individual individual) {
        Individual current = individual.clone();
        int iterations = 0;
        boolean improved = true;

        while (improved && iterations < maxIterations) {
            improved = false;
            Individual best = current.clone();

            // Przeszukaj sąsiadztwo przez 2-opt
            int[] tour = current.getTour();
            int n = tour.length;

            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 2; j < n; j++) {
                    // Wykonaj swap 2-opt
                    int[] newTour = tour.clone();
                    reverse(newTour, i, j);

                    Individual neighbor = new Individual(newTour, problem);
                    if (neighbor.getTourLength() < best.getTourLength()) {
                        best = neighbor;
                        improved = true;
                    }
                }
            }

            current = best;
            iterations++;
        }

        return current;
    }

    /**
     * Odwraca segment trasy między indeksami i i j
     */
    private void reverse(int[] tour, int i, int j) {
        while (i < j) {
            int temp = tour[i];
            tour[i] = tour[j];
            tour[j] = temp;
            i++;
            j--;
        }
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int max) {
        this.maxIterations = max;
    }
}
