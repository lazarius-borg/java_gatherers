# Java Stream Gatherers Showcase

An educational reference suite demonstrating the **Stream Gatherers API** introduced in Java 24 (JEP 485) and running on Java 26.

This project illustrates the intermediate transformation capabilities of `Stream.gather()` in contrast with classical Java Streams.

---

## Structure & Categories

The demonstration suite is organized into three main categories across dedicated test classes in package `nl.invokedynamic.demo.java.gatherers`:

### [Category 1 — Where Gatherers Shine](src/test/java/nl/invokedynamic/demo/java/gatherers/Category1GatherersShineTest.java)
Demonstrates stateful intermediate transformations that are difficult or impossible with traditional `Stream` methods:
1. **Sliding window analytics** (`Gatherers.windowSliding`): Moving averages, rolling extrema, and trend peak detection.
2. **Fixed-size batching** (`Gatherers.windowFixed`): Chunking streams into discrete batches with remainder preservation.
3. **Running/cumulative computations** (`Gatherers.scan`): Running totals, running max, and prefix sums.
4. **Bounded-concurrency mapping** (`Gatherers.mapConcurrent`): Non-blocking asynchronous task execution with strict encounter-order preservation.
5. **Stream zipping** (`CustomGatherers.zip`): Pairing elements from two streams index-wise.
6. **Consecutive deduplication & adjacency grouping** (`CustomGatherers.dedupConsecutive`, `CustomGatherers.groupConsecutiveBy`): Collapsing adjacent duplicates without requiring full dataset buffering.
7. **Lookahead / lookbehind logic**: Peeking at neighbors before emitting downstream.
8. **Stateful early termination** (`CustomGatherers.takeUntilConsecutiveFailures`): Halting stream processing on complex dynamic thresholds.
9. **Stream-based state machines / tokenizers** (`CustomGatherers.parseTokenEvents`): Parsing flat token streams into structured domain events.
10. **Reservoir sampling / bounded top-K** (`CustomGatherers.topK`): Extracting top-K elements in bounded memory without sorting the entire dataset.
11. **Conditional element injection** (`CustomGatherers.injectSeparator`): Inverting or inserting synthetic markers based on element intervals.

### [Category 2 — Standard Stream Operations Reimplemented](src/test/java/nl/invokedynamic/demo/java/gatherers/Category2StreamContrastTest.java)
Side-by-side mechanical comparisons demonstrating the functional contracts of `Integrator`, `Initializer`, `Finisher`, and short-circuiting:
1. `filter` vs stateless gatherer
2. `map` vs stateless gatherer
3. `limit` vs short-circuiting gatherer
4. `flatMap` vs multi-emitting gatherer
5. `distinct` vs stateful Set-backed gatherer
6. `reduce` vs intermediate `Gatherers.fold`

### [Category 3 — Boundaries, Overkill & Anti-Patterns](src/test/java/nl/invokedynamic/demo/java/gatherers/Category3OverkillBoundariesTest.java)
Negative-space reference examples illustrating where Gatherers are unnecessary or an anti-pattern:
1. **Stateless map/filter chains**: Built-in streams are cleaner and lower overhead.
2. **Terminal aggregations**: `Collectors` are the proper terminal abstraction (`sum`, `count`, `joining`).
3. **Global sorting**: `Stream.sorted()` is native and inherently requires full buffering.
4. **CPU-bound numeric work**: Parallel streams (`.parallel()`) leverage `ForkJoinPool` for CPU bound tasks, while `mapConcurrent` targets virtual thread blocking I/O.
5. **Multi-pass algorithms**: Pre-calculating metadata in two passes is cleaner than single-pass buffering.
6. **Basic pagination**: `skip` and `limit` are standard and idiomatic.
7. **Unordered grouping**: `Collectors.groupingBy` is preferred for non-adjacent grouping.

---

## Reusable Custom Gatherers

Custom gatherer factory implementations reside in [`CustomGatherers`](src/main/java/nl/invokedynamic/demo/java/gatherers/custom/CustomGatherers.java) using modern `Gatherer.of(...)` and `Gatherer.ofSequential(...)` contracts.

---

## Running the Tests

Ensure you have Java 26 installed. Use the provided Maven wrapper to run the test suite:

```bash
# Run the entire showcase suite (all 24 use cases)
./mvnw clean test

# Run individual categories
./mvnw test -Dtest=Category1GatherersShineTest
./mvnw test -Dtest=Category2StreamContrastTest
./mvnw test -Dtest=Category3OverkillBoundariesTest
```