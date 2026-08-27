package nl.invokedynamic.demo.java.gatherers.model;

/**
 * A generic 2-tuple record used in stream zipping and paired computations.
 *
 * @param <L> left element type
 * @param <R> right element type
 */
public record Pair<L, R>(L left, R right) {

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }
}
