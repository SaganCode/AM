import java.util.List;
import java.util.Random;

/**
 * Selekcja losowa (Random Selection)
 */
public class RandomSelection implements SelectionMethod {
    private Random random = new Random();

    @Override
    public Individual select(List<Individual> population) {
        int index = random.nextInt(population.size());
        return population.get(index).clone();
    }

    @Override
    public String getName() {
        return "Random Selection";
    }
}
