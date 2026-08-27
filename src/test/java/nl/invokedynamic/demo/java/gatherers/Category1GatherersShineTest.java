package nl.invokedynamic.demo.java.gatherers;

import nl.invokedynamic.demo.java.gatherers.custom.CustomGatherers;
import nl.invokedynamic.demo.java.gatherers.model.Measurement;
import nl.invokedynamic.demo.java.gatherers.model.Pair;
import nl.invokedynamic.demo.java.gatherers.model.Token;
import nl.invokedynamic.demo.java.gatherers.model.TokenEvent;
import nl.invokedynamic.demo.java.gatherers.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Category 1 — Where Gatherers Shine (Stateful and Windowing Capabilities)")
class Category1GatherersShineTest {

    @Test
    @DisplayName("1.1 Sliding Window Analytics: Computes moving averages, rolling max/min, and trend peaks")
    void testSlidingWindowAnalytics() {
        // Given: A stream of sequential sensor measurements
        List<Measurement> measurements = List.of(
                new Measurement(1, 10.0),
                new Measurement(2, 20.0),
                new Measurement(3, 40.0),
                new Measurement(4, 30.0),
                new Measurement(5, 10.0)
        );

        // When: Computing 3-element sliding windows for moving average and peak detection
        List<Double> movingAverages = measurements.stream()
                .gather(Gatherers.windowSliding(3))
                .map(window -> window.stream().mapToDouble(Measurement::value).average().orElse(0.0))
                .toList();

        List<Measurement> peaks = measurements.stream()
                .gather(Gatherers.windowSliding(3))
                .filter(w -> w.get(1).value() > w.get(0).value() && w.get(1).value() > w.get(2).value())
                .map(w -> w.get(1))
                .toList();

        // Then: Moving averages and local peaks match expected sliding values
        assertEquals(List.of(70.0 / 3.0, 30.0, 80.0 / 3.0), movingAverages);
        assertEquals(List.of(new Measurement(3, 40.0)), peaks);
    }

    @Test
    @DisplayName("1.2 Fixed-size Batching: Chunks stream into discrete batches with remainder preservation")
    void testFixedSizeBatching() {
        // Given: An ordered list of 7 items
        List<String> items = List.of("item-1", "item-2", "item-3", "item-4", "item-5", "item-6", "item-7");

        // When: Chunking stream into fixed batches of size 3
        List<List<String>> batches = items.stream()
                .gather(Gatherers.windowFixed(3))
                .toList();

        // Then: Stream is partitioned into two full batches and one remainder batch
        assertEquals(List.of(
                List.of("item-1", "item-2", "item-3"),
                List.of("item-4", "item-5", "item-6"),
                List.of("item-7")
        ), batches);
    }

    @Test
    @DisplayName("1.3 Running Computations: Computes prefix sums and running account balance")
    void testRunningComputations() {
        // Given: A stream of financial transactions
        List<Transaction> transactions = List.of(
                new Transaction("tx-1", "INCOME", 100.0),
                new Transaction("tx-2", "EXPENSE", -30.0),
                new Transaction("tx-3", "EXPENSE", -20.0),
                new Transaction("tx-4", "INCOME", 50.0)
        );

        // When: Calculating running balances using Gatherers.scan
        List<Double> runningBalances = transactions.stream()
                .map(Transaction::amount)
                .gather(Gatherers.scan(() -> 0.0, Double::sum))
                .toList();

        // Then: Each step emits the accumulated prefix sum
        assertEquals(List.of(100.0, 70.0, 50.0, 100.0), runningBalances);
    }

    @Test
    @DisplayName("1.4 Bounded-Concurrency Mapping: Executes concurrent tasks while strictly preserving encounter order")
    void testBoundedConcurrencyMapping() {
        // Given: A stream of task IDs
        List<Integer> taskIds = List.of(1, 2, 3, 4, 5, 6, 7, 8);
        AtomicInteger activeThreads = new AtomicInteger(0);

        // When: Mapping tasks with concurrency limit of 4 on virtual threads with simulated I/O delay
        List<String> processed = taskIds.stream()
                .gather(Gatherers.mapConcurrent(4, id -> {
                    activeThreads.incrementAndGet();
                    try {
                        Thread.sleep(5); // Simulated non-blocking virtual thread sleep
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        activeThreads.decrementAndGet();
                    }
                    return "processed-" + id;
                }))
                .toList();

        // Then: Encounter order is strictly preserved
        assertEquals(List.of(
                "processed-1", "processed-2", "processed-3", "processed-4",
                "processed-5", "processed-6", "processed-7", "processed-8"
        ), processed);
    }

    @Test
    @DisplayName("1.5 Zipping Streams: Combines two streams element-by-element into paired tuples")
    void testZippingStreams() {
        // Given: Two separate stream sequences
        List<String> names = List.of("Alice", "Bob", "Charlie");
        Stream<Integer> scores = Stream.of(95, 88, 92, 100); // Has more elements

        // When: Zipping names with scores
        List<Pair<String, Integer>> zipped = names.stream()
                .gather(CustomGatherers.zip(scores, Pair::of))
                .toList();

        // Then: Output stream truncates at the shorter stream's length and pairs items index-wise
        assertEquals(List.of(
                Pair.of("Alice", 95),
                Pair.of("Bob", 88),
                Pair.of("Charlie", 92)
        ), zipped);
    }

    @Test
    @DisplayName("1.6 Consecutive Deduplication & Adjacency Grouping: Collapses adjacent duplicates and groups by key")
    void testConsecutiveDeduplicationAndGrouping() {
        // Given: A sequence with consecutive duplicates and category runs
        List<String> rawTokens = List.of("A", "A", "B", "C", "C", "C", "B", "B");

        // When: Applying consecutive deduplication and adjacency grouping
        List<String> deduplicated = rawTokens.stream()
                .gather(CustomGatherers.dedupConsecutive())
                .toList();

        List<List<String>> grouped = rawTokens.stream()
                .gather(CustomGatherers.groupConsecutiveBy(Function -> Function))
                .toList();

        // Then: Only consecutive duplicates are collapsed
        assertEquals(List.of("A", "B", "C", "B"), deduplicated);
        assertEquals(List.of(
                List.of("A", "A"),
                List.of("B"),
                List.of("C", "C", "C"),
                List.of("B", "B")
        ), grouped);
    }

    @Test
    @DisplayName("1.7 Lookahead & Lookbehind Logic: Inspects adjacent neighbors before deciding downstream emission")
    void testLookaheadLookbehindLogic() {
        // Given: A stream of numeric readings
        List<Integer> numbers = List.of(1, 2, 8, 3, 7, 2);

        // When: Using windowSliding(2) to inspect current and immediate next element
        List<String> transitions = numbers.stream()
                .gather(Gatherers.windowSliding(2))
                .map(pair -> pair.get(0) < pair.get(1) ? "UP(" + pair.get(0) + "->" + pair.get(1) + ")" : "DOWN(" + pair.get(0) + "->" + pair.get(1) + ")")
                .toList();

        // Then: Transitions capture pairwise lookahead comparisons
        assertEquals(List.of("UP(1->2)", "UP(2->8)", "DOWN(8->3)", "UP(3->7)", "DOWN(7->2)"), transitions);
    }

    @Test
    @DisplayName("1.8 Stateful Early Termination: Short-circuits stream after reaching consecutive failure threshold")
    void testStatefulEarlyTermination() {
        // Given: A series of health check response codes
        List<String> serverStatuses = List.of("OK", "FAIL", "FAIL", "FAIL", "OK", "OK");

        // When: Processing statuses with a threshold of 3 consecutive failures
        List<String> captured = serverStatuses.stream()
                .gather(CustomGatherers.takeUntilConsecutiveFailures(status -> "FAIL".equals(status), 3))
                .toList();

        // Then: Pipeline short-circuits on the 3rd failure without processing subsequent items
        assertEquals(List.of("OK", "FAIL", "FAIL"), captured);
    }

    @Test
    @DisplayName("1.9 Stream-based State Machine: Parses a flat token stream into structured TokenEvent objects")
    void testStreamStateMachineTokenizer() {
        // Given: A stream of flat key-delimiter-value tokens
        List<Token> tokenStream = List.of(
                Token.key("host"),
                Token.delimiter(":"),
                Token.value("localhost"),
                Token.key("port"),
                Token.delimiter(":"),
                Token.value("8080"),
                Token.eof()
        );

        // When: Passing tokens through the custom state machine parser gatherer
        List<TokenEvent> events = tokenStream.stream()
                .gather(CustomGatherers.parseTokenEvents())
                .toList();

        // Then: Structured key-value events are produced
        assertEquals(List.of(
                new TokenEvent("host", "localhost"),
                new TokenEvent("port", "8080")
        ), events);
    }

    @Test
    @DisplayName("1.10 Bounded Top-K Maintenance: Extracts top K elements in bounded memory without sorting entire stream")
    void testBoundedTopK() {
        // Given: A stream of unsorted numerical scores
        List<Integer> scores = List.of(12, 45, 7, 89, 34, 99, 23, 67, 1);

        // When: Extracting the top 3 highest scores using custom topK gatherer
        List<Integer> top3 = scores.stream()
                .gather(CustomGatherers.topK(3, Comparator.naturalOrder()))
                .toList();

        // Then: Top 3 items are returned in descending order
        assertEquals(List.of(99, 89, 67), top3);
    }

    @Test
    @DisplayName("1.11 Conditional Element Injection: Injects synthetic marker separators every N elements")
    void testConditionalElementInjection() {
        // Given: A stream of log entries
        List<String> entries = List.of("log-1", "log-2", "log-3", "log-4", "log-5");

        // When: Injecting a separator marker after every 2 items
        List<String> withSeparators = entries.stream()
                .gather(CustomGatherers.injectSeparator(2, () -> "---PAGE-BREAK---"))
                .toList();

        // Then: Separator markers are inserted at positions 2 and 4
        assertEquals(List.of(
                "log-1", "log-2", "---PAGE-BREAK---",
                "log-3", "log-4", "---PAGE-BREAK---",
                "log-5"
        ), withSeparators);
    }
}
