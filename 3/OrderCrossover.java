/**
 * Order Crossover (OX)
 * Jedna z najpopularniejszych metod krzyżowania dla TSP
 */
public class OrderCrossover implements CrossoverMethod {
    
    @Override
    public Individual crossover(Individual parent1, Individual parent2, TSPProblem problem) {
        int[] tour1 = parent1.getTour();
        int[] tour2 = parent2.getTour();
        int n = tour1.length;
        
        // Losuj dwa punkty krzyżowania
        int point1 = (int) (Math.random() * n);
        int point2 = (int) (Math.random() * n);
        
        if (point1 > point2) {
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }
        
        int[] offspring = new int[n];
        boolean[] used = new boolean[n];
        
        // Skopiuj segment z pierwszego rodzica
        for (int i = point1; i < point2; i++) {
            offspring[i] = tour1[i];
            used[tour1[i]] = true;
        }
        
        // Wypełnij resztę z drugiego rodzica
        int pos = point2;
        for (int i = 0; i < n; i++) {
            int city = tour2[(point2 + i) % n];
            if (!used[city]) {
                offspring[pos % n] = city;
                used[city] = true;
                pos++;
            }
        }
        
        return new Individual(offspring, problem);
    }

    @Override
    public String getName() {
        return "Order Crossover (OX)";
    }
}
