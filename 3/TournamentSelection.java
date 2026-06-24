import java.util.List;

/**
 * Selekcja turniejowa
 */
public class TournamentSelection implements SelectionMethod {
    private int tournamentSize;

    public TournamentSelection(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    @Override
    public Individual select(List<Individual> population) {
        Individual best = null;
        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = (int) (Math.random() * population.size());
            Individual candidate = population.get(randomIndex);
            if (best == null || candidate.getFitness() > best.getFitness()) {
                best = candidate;
            }
        }
        return best.clone();
    }

    @Override
    public String getName() {
        return "Tournament Selection (k=" + tournamentSize + ")";
    }
}
