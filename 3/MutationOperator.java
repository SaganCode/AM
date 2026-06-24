/**
 * Operator mutacji dla TSP
 * Implementuje mutację przez inwersję (inversion mutation)
 */
public class MutationOperator {
    private double mutationRate;

    public MutationOperator(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    /**
     * Mutacja przez inwersję - odwróć segment trasy
     */
    public void mutate(Individual individual) {
        int[] tour = individual.getTour();
        
        if (Math.random() < mutationRate) {
            // Losuj dwa punkty
            int point1 = (int) (Math.random() * tour.length);
            int point2 = (int) (Math.random() * tour.length);
            
            if (point1 > point2) {
                int temp = point1;
                point1 = temp;
                point2 = point1;
            }
            
            // Odwróć segment
            while (point1 < point2) {
                int temp = tour[point1];
                tour[point1] = tour[point2];
                tour[point2] = temp;
                point1++;
                point2--;
            }
            
            individual.setTour(tour);
        }
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double rate) {
        this.mutationRate = rate;
    }
}
