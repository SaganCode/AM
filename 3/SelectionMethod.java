import java.util.List;

/**
 * Interfejs dla metod selekcji
 */
public interface SelectionMethod {
    Individual select(List<Individual> population);
    String getName();
}
