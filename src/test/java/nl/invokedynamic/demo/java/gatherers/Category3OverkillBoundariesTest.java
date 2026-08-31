package nl.invokedynamic.demo.java.gatherers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Category 3 — Where Gatherers Would Be Overkill or the Wrong Tool (Negative-Space Boundaries)")
class Category3OverkillBoundariesTest {

    @Test
    @DisplayName("3.1 Stateless map/filter chains: Built-in Stream methods are strictly superior to custom gatherers")
    void testStatelessChainsOverkill() {
        // Given: A collection of raw strings
        List<String> names = List.of("alice", "bob", "alexander", "charlie");

        // When: Filtering by length and capitalizing using built-in methods (idiomatic)
        List<String> idiomaticResult = names.stream()
                .filter(name -> name.startsWith("a"))
                .map(String::toUpperCase)
                .toList();

        // Then: Standard fluent operations are concise, expressive, and have zero custom gatherer ceremony
        assertEquals(List.of("ALICE", "ALEXANDER"), idiomaticResult);
    }

    @Test
    @DisplayName("3.2 Terminal Aggregations: Collectors is the proper abstraction for terminal reduction")
    void testTerminalAggregationsOverkill() {
        // Given: A list of words to concatenate with formatting
        List<String> words = List.of("Java", "Stream", "Gatherers");

        // When: Joining using Collectors.joining (idiomatic terminal reduction)
        String joined = words.stream()
                .collect(Collectors.joining(", ", "[", "]"));

        long count = words.stream().count();

        // Then: Collectors produce terminal values directly without intermediate gatherer wrapping
        assertEquals("[Java, Stream, Gatherers]", joined);
        assertEquals(3L, count);
    }

    @Test
    @DisplayName("3.3 Global Dataset Sorting: Built-in sorted() is optimal since sorting inherently requires full buffering")
    void testGlobalSortingOverkill() {
        // Given: An unsorted list of integers
        List<Integer> unsorted = List.of(5, 1, 4, 2, 8);

        // When: Sorting using standard Stream.sorted()
        List<Integer> sorted = unsorted.stream()
                .sorted()
                .toList();

        // Then: Single-pass gatherers offer no algorithmic advantage over native sorted() for full-dataset sorting
        assertEquals(List.of(1, 2, 4, 5, 8), sorted);
    }

    @Test
    @DisplayName("3.4 CPU-bound Parallel Numeric Work: Parallel streams are designed for compute-heavy tasks over mapConcurrent")
    void testCpuBoundParallelOverkill() {
        // Given: A numeric range of integers
        List<Integer> numbers = IntStream.rangeClosed(1, 100).boxed().toList();

        // When: Computing sums of heavy calculations using parallel streams (ForkJoinPool)
        int sumOfSquares = numbers.parallelStream()
                .mapToInt(n -> n * n)
                .sum();

        // Then: Parallel streams leverage CPU cores for compute-heavy tasks, while mapConcurrent targets virtual-thread blocking I/O
        assertEquals(338350, sumOfSquares);
    }

    @Test
    @DisplayName("3.5 Multi-pass Normalization: Pre-calculating dataset metadata in collection phase is preferable to single-pass buffering")
    void testMultiPassAlgorithmsBoundary() {
        // Given: A collection of numerical values needing normalization against the dataset maximum
        List<Double> scores = List.of(20.0, 50.0, 100.0, 80.0);

        // When: Computing maximum first, then normalizing elements in a second pass (idiomatic two-pass)
        double maxScore = Collections.max(scores);
        List<Double> normalized = scores.stream()
                .map(score -> score / maxScore)
                .toList();

        // Then: Multi-pass approach is explicit and avoids forcing entire dataset into custom stateful gatherer buffers
        assertEquals(List.of(0.2, 0.5, 1.0, 0.8), normalized);
    }

    @Test
    @DisplayName("3.6 Basic Pagination: Standard skip and limit are trivial and idiomatic")
    void testPaginationOverkill() {
        // Given: A list representing 10 database records
        List<String> records = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> "record-" + i)
                .toList();

        // When: Paginating page 2 with page size 3 using built-in skip and limit
        List<String> page2 = records.stream()
                .skip(3)
                .limit(3)
                .toList();

        // Then: Standard skip/limit operations handle pagination cleanly without stateful gatherer overhead
        assertEquals(List.of("record-4", "record-5", "record-6"), page2);
    }

    @Test
    @DisplayName("3.7 Unordered Key Grouping: Collectors.groupingBy is the standard solution for independent grouping")
    void testUnorderedGroupingBoundary() {
        // Given: A list of items with categories
        List<String> words = List.of("ant", "bear", "ape", "bee", "cat");

        // When: Grouping by starting character across the entire stream using Collectors.groupingBy
        Map<Character, List<String>> groupedByInitial = words.stream()
                .collect(Collectors.groupingBy(s -> s.charAt(0)));

        // Then: Global grouping is simpler with Collectors.groupingBy compared to consecutive/adjacent stream gatherers
        assertEquals(Map.of(
                'a', List.of("ant", "ape"),
                'b', List.of("bear", "bee"),
                'c', List.of("cat")
        ), groupedByInitial);
    }
}
