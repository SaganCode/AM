import java.util.List;

/**
 * Selekcja ruletką (Roulette Wheel Selection)
 */
public class RouletteWheelSelection implements SelectionMethod {
    
    @Override
    public Individual select(List<Individual> population) {
        // Oblicz całkowitą fitness
        double totalFitness = 0;
        for (Individual individual : population) {
            totalFitness += individual.getFitness();
        }

        if (totalFitness == 0) {
            return population.get((int) (Math.random() * population.size())).clone();
        }

        // Ruletka
        double spin = Math.random() * totalFitness;
        double current = 0;
        
        for (Individual individual : population) {
            current += individual.getFitness();
            if (current >= spin) {
                return individual.clone();
            }
        }
        
        return population.get(population.size() - 1).clone();
    }

    @Override
    public String getName() {
        return "Roulette Wheel Selection";
    }
}
