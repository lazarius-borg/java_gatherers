package nl.invokedynamic.demo.java.gatherers.custom;

import nl.invokedynamic.demo.java.gatherers.model.Token;
import nl.invokedynamic.demo.java.gatherers.model.TokenEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/**
 * Production-style factory utility providing reusable custom {@link Gatherer} implementations.
 * Demonstrates custom stateful and stateless stream gatherers using {@code Gatherer.of}
 * and {@code Gatherer.ofSequential}.
 */
public final class CustomGatherers {

    private CustomGatherers() {
        // Utility class
    }

    // =========================================================================
    // Category 1: Advanced Stateful Gatherers
    // =========================================================================

    /**
     * Pairs elements from the upstream stream with elements from another stream index-wise.
     */
    public static <T, U, R> Gatherer<T, ?, R> zip(
            Stream<U> other,
            BiFunction<? super T, ? super U, ? extends R> zipper) {
        Objects.requireNonNull(other, "other stream must not be null");
        Objects.requireNonNull(zipper, "zipper function must not be null");

        class ZipState {
            final Iterator<U> iterator = other.iterator();
        }

        return Gatherer.ofSequential(
                ZipState::new,
                Gatherer.Integrator.of((state, element, downstream) -> {
                    if (state.iterator.hasNext()) {
                        R result = zipper.apply(element, state.iterator.next());
                        return downstream.push(result);
                    }
                    return false;
                })
        );
    }

    /**
     * Removes consecutive duplicate elements from the stream, preserving encounter order.
     */
    public static <T> Gatherer<T, ?, T> dedupConsecutive() {
        class DedupState {
            boolean hasPrevious;
            T previous;
        }

        return Gatherer.ofSequential(
                DedupState::new,
                Gatherer.Integrator.of((state, element, downstream) -> {
                    if (!state.hasPrevious || !Objects.equals(state.previous, element)) {
                        state.hasPrevious = true;
                        state.previous = element;
                        return downstream.push(element);
                    }
                    return true;
                })
        );
    }

    /**
     * Groups consecutive adjacent elements that share the same classifier key.
     */
    public static <T, K> Gatherer<T, ?, List<T>> groupConsecutiveBy(
            Function<? super T, ? extends K> classifier) {
        Objects.requireNonNull(classifier, "classifier must not be null");

        class GroupState {
            boolean hasCurrent;
            K currentKey;
            final List<T> currentGroup = new ArrayList<>();
        }

        return Gatherer.ofSequential(
                GroupState::new,
                Gatherer.Integrator.of((state, element, downstream) -> {
                    K key = classifier.apply(element);
                    if (!state.hasCurrent) {
                        state.hasCurrent = true;
                        state.currentKey = key;
                        state.currentGroup.add(element);
                        return true;
                    } else if (Objects.equals(state.currentKey, key)) {
                        state.currentGroup.add(element);
                        return true;
                    } else {
                        List<T> batch = new ArrayList<>(state.currentGroup);
                        state.currentKey = key;
                        state.currentGroup.clear();
                        state.currentGroup.add(element);
                        return downstream.push(batch);
                    }
                }),
                (state, downstream) -> {
                    if (state.hasCurrent && !state.currentGroup.isEmpty()) {
                        downstream.push(new ArrayList<>(state.currentGroup));
                    }
                }
        );
    }

    /**
     * Halts stream evaluation early when consecutive elements matching the failure predicate
     * reach the specified limit.
     */
    public static <T> Gatherer<T, ?, T> takeUntilConsecutiveFailures(
            Predicate<? super T> isFailure,
            int maxConsecutiveFailures) {
        Objects.requireNonNull(isFailure, "isFailure predicate must not be null");

        class FailureState {
            int consecutiveFailures;
        }

        return Gatherer.ofSequential(
                FailureState::new,
                Gatherer.Integrator.of((state, element, downstream) -> {
                    if (isFailure.test(element)) {
                        state.consecutiveFailures++;
                        if (state.consecutiveFailures >= maxConsecutiveFailures) {
                            return false; // Short-circuit
                        }
                    } else {
                        state.consecutiveFailures = 0;
                    }
                    return downstream.push(element);
                })
        );
    }

    /**
     * Parses a flat stream of tokens into structured {@link TokenEvent} instances
     * using a stream-based state machine.
     */
    public static Gatherer<Token, ?, TokenEvent> parseTokenEvents() {
        class ParserState {
            String pendingKey;
        }

        return Gatherer.ofSequential(
                ParserState::new,
                Gatherer.Integrator.of((state, token, downstream) -> {
                    if (token.type() == Token.TokenType.KEY) {
                        state.pendingKey = token.value();
                        return true;
                    } else if (token.type() == Token.TokenType.VALUE && state.pendingKey != null) {
                        TokenEvent event = new TokenEvent(state.pendingKey, token.value());
                        state.pendingKey = null;
                        return downstream.push(event);
                    }
                    return true;
                })
        );
    }

    /**
     * Retains the top K elements in bounded memory without sorting the whole dataset,
     * emitting them downstream in descending order upon completion.
     */
    public static <T> Gatherer<T, ?, T> topK(int k, Comparator<? super T> comparator) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be greater than 0");
        }
        Objects.requireNonNull(comparator, "comparator must not be null");

        return Gatherer.ofSequential(
                () -> new PriorityQueue<T>(k, comparator),
                Gatherer.Integrator.of((heap, element, downstream) -> {
                    heap.offer(element);
                    if (heap.size() > k) {
                        heap.poll();
                    }
                    return true;
                }),
                (heap, downstream) -> {
                    List<T> sorted = new ArrayList<>(heap);
                    sorted.sort(comparator.reversed());
                    for (T item : sorted) {
                        if (!downstream.push(item)) {
                            break;
                        }
                    }
                }
        );
    }

    /**
     * Injects a synthetic separator element downstream after every N elements.
     */
    public static <T> Gatherer<T, ?, T> injectSeparator(int everyN, Supplier<T> separatorSupplier) {
        if (everyN <= 0) {
            throw new IllegalArgumentException("everyN must be greater than 0");
        }
        Objects.requireNonNull(separatorSupplier, "separatorSupplier must not be null");

        class CounterState {
            int count;
        }

        return Gatherer.ofSequential(
                CounterState::new,
                Gatherer.Integrator.of((state, element, downstream) -> {
                    if (!downstream.push(element)) {
                        return false;
                    }
                    state.count++;
                    if (state.count % everyN == 0) {
                        return downstream.push(separatorSupplier.get());
                    }
                    return true;
                })
        );
    }

    // =========================================================================
    // Category 2: Reimplemented Standard Operations (Mechanical Contrasts)
    // =========================================================================

    /**
     * Reimplementation of {@code Stream.filter(Predicate)} as a stateless custom gatherer.
     */
    public static <T> Gatherer<T, Void, T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate must not be null");
        return Gatherer.of(
                Gatherer.Integrator.of((_, element, downstream) -> {
                    if (predicate.test(element)) {
                        return downstream.push(element);
                    }
                    return true;
                })
        );
    }

    /**
     * Reimplementation of {@code Stream.map(Function)} as a stateless custom gatherer.
     */
    public static <T, R> Gatherer<T, Void, R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        return Gatherer.of(
                Gatherer.Integrator.of((_, element, downstream) ->
                        downstream.push(mapper.apply(element)))
        );
    }

    /**
     * Reimplementation of {@code Stream.limit(long)} as a stateful short-circuiting gatherer.
     */
    public static <T> Gatherer<T, ?, T> limit(long maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException("maxSize cannot be negative");
        }

        class LimitState {
            long count;
        }

        return Gatherer.ofSequential(
                LimitState::new,
                Gatherer.Integrator.of((state, element, downstream) -> {
                    if (state.count < maxSize) {
                        state.count++;
                        boolean pushed = downstream.push(element);
                        return pushed && state.count < maxSize;
                    }
                    return false;
                })
        );
    }

    /**
     * Reimplementation of {@code Stream.flatMap(Function)} as a custom gatherer emitting 0..N elements.
     */
    public static <T, R> Gatherer<T, Void, R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> mapper) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        return Gatherer.of(
                Gatherer.Integrator.of((_, element, downstream) -> {
                    try (Stream<? extends R> innerStream = mapper.apply(element)) {
                        if (innerStream != null) {
                            Iterator<? extends R> iterator = innerStream.iterator();
                            while (iterator.hasNext()) {
                                if (!downstream.push(iterator.next())) {
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                })
        );
    }

    /**
     * Reimplementation of {@code Stream.distinct()} using a stateful Set accumulator.
     */
    public static <T> Gatherer<T, ?, T> distinct() {
        return Gatherer.ofSequential(
                HashSet<T>::new,
                Gatherer.Integrator.of((Set<T> seen, T element, Gatherer.Downstream<? super T> downstream) -> {
                    if (seen.add(element)) {
                        return downstream.push(element);
                    }
                    return true;
                })
        );
    }
}
