package nl.invokedynamic.demo.java.gatherers;

import nl.invokedynamic.demo.java.gatherers.custom.CustomGatherers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Category 2 — Standard Stream Operations Reimplemented via Gatherers (Mechanical Contrast)")
class Category2StreamContrastTest {

    @Test
    @DisplayName("2.1 filter contrast: Standard Stream.filter vs custom stateless Gatherer")
    void testFilterContrast() {
        // Given: A sequence of integers containing evens and odds
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);

        // When: Filtering even numbers using standard Stream.filter and custom Gatherer
        List<Integer> standardResult = numbers.stream()
                .filter(n -> n % 2 == 0)
                .toList();

        List<Integer> gathererResult = numbers.stream()
                .gather(CustomGatherers.filter(n -> n % 2 == 0))
                .toList();

        // Then: Both produce identical filtered results
        assertEquals(List.of(2, 4, 6), standardResult);
        assertEquals(standardResult, gathererResult);
    }

    @Test
    @DisplayName("2.2 map contrast: Standard Stream.map vs custom stateless Gatherer")
    void testMapContrast() {
        // Given: A list of lowercase words
        List<String> words = List.of("java", "stream", "gatherer");

        // When: Transforming words to uppercase using standard Stream.map and custom Gatherer
        List<String> standardResult = words.stream()
                .map(String::toUpperCase)
                .toList();

        List<String> gathererResult = words.stream()
                .gather(CustomGatherers.map(String::toUpperCase))
                .toList();

        // Then: Both produce identical uppercase transformed strings
        assertEquals(List.of("JAVA", "STREAM", "GATHERER"), standardResult);
        assertEquals(standardResult, gathererResult);
    }

    @Test
    @DisplayName("2.3 limit contrast: Standard Stream.limit vs custom short-circuiting Gatherer")
    void testLimitContrast() {
        // Given: An ordered sequence of numbers
        List<Integer> numbers = List.of(10, 20, 30, 40, 50);

        // When: Truncating the stream to 3 elements using Stream.limit and custom Gatherer
        List<Integer> standardResult = numbers.stream()
                .limit(3)
                .toList();

        List<Integer> gathererResult = numbers.stream()
                .gather(CustomGatherers.limit(3))
                .toList();

        // Then: Both truncate the stream at exactly 3 elements
        assertEquals(List.of(10, 20, 30), standardResult);
        assertEquals(standardResult, gathererResult);
    }

    @Test
    @DisplayName("2.4 flatMap contrast: Standard Stream.flatMap vs custom Gatherer emitting 0..N elements")
    void testFlatMapContrast() {
        // Given: A list of sentences
        List<String> sentences = List.of("hello world", "java 24 gatherers");

        // When: Splitting sentences into individual words using Stream.flatMap and custom Gatherer
        List<String> standardResult = sentences.stream()
                .flatMap(s -> Stream.of(s.split(" ")))
                .toList();

        List<String> gathererResult = sentences.stream()
                .gather(CustomGatherers.flatMap(s -> Stream.of(s.split(" "))))
                .toList();

        // Then: Both produce the same flattened sequence of words
        assertEquals(List.of("hello", "world", "java", "24", "gatherers"), standardResult);
        assertEquals(standardResult, gathererResult);
    }

    @Test
    @DisplayName("2.5 distinct contrast: Standard Stream.distinct vs custom Set-backed Gatherer")
    void testDistinctContrast() {
        // Given: A list with multiple duplicates across different positions
        List<String> items = List.of("apple", "banana", "apple", "cherry", "banana", "date");

        // When: Deduplicating using standard Stream.distinct and custom Set-backed Gatherer
        List<String> standardResult = items.stream()
                .distinct()
                .toList();

        List<String> gathererResult = items.stream()
                .gather(CustomGatherers.distinct())
                .toList();

        // Then: Both produce identical encounter-ordered distinct lists
        assertEquals(List.of("apple", "banana", "cherry", "date"), standardResult);
        assertEquals(standardResult, gathererResult);
    }

    @Test
    @DisplayName("2.6 reduce vs fold contrast: Terminal Stream.reduce vs intermediate Gatherers.fold")
    void testReduceVsFoldContrast() {
        // Given: A list of numbers to aggregate
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // When: Summing via terminal Stream.reduce and intermediate Gatherers.fold
        Optional<Integer> standardReduced = numbers.stream()
                .reduce(Integer::sum);

        List<Integer> foldedResult = numbers.stream()
                .gather(Gatherers.fold(() -> 0, Integer::sum))
                .toList();

        // Then: Terminal reduce returns single Optional value while fold emits one element downstream
        assertEquals(Optional.of(15), standardReduced);
        assertEquals(List.of(15), foldedResult);
        assertEquals(standardReduced.get(), foldedResult.getFirst());
    }
}
