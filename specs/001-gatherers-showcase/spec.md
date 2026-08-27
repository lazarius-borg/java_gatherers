# Feature Specification: Java Stream Gatherers Showcase & Contrast Suite

**Feature Branch**: `001-gatherers-showcase`

**Created**: 2026-08-27

**Status**: Draft

**Input**: User description: "streams and gatherers - Create tests that illustrate use of gatherers in contrast to streams as defined in src/test/resources/SPEC.md"

## Clarifications

### Session 2026-08-27
- Q: How should the 24 test use cases be organized into test classes and packages? → A: Group into 3 top-level test classes by category (`Category1GatherersShineTest`, `Category2StreamContrastTest`, `Category3OverkillBoundariesTest`).
- Q: Where should custom Gatherer implementations be defined? → A: Place custom gatherers in `src/main/java` in a production-style package structure (e.g., `nl.invokedynamic.demo.java.gatherers.custom.CustomGatherers`).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Advanced Stream Transformations Beyond Standard Streams (Priority: P1)

As a Java developer exploring modern stream processing capabilities, I want clear, runnable, self-documenting test cases illustrating intermediate stateful and windowing operations that are difficult or impossible with standard `java.util.stream.Stream` methods alone, so that I can understand when and how to leverage built-in and custom Gatherers in production code.

**Why this priority**: Demonstrating stateful stream operations (sliding windows, batching, running calculations, bounded-concurrency mapping, adjacency-based grouping, and lookahead logic) represents the core value proposition and primary rationale for the Gatherers API.

**Independent Test**: Can be verified by executing `Category1GatherersShineTest` showcasing 11 distinct stateful stream scenarios using Given-When-Then test bodies with descriptive display names.

**Acceptance Scenarios**:

1. **Given** a stream of numeric measurements, **When** processed with a sliding window of size N, **Then** moving averages, rolling min/max, and trend peaks are computed without collecting all elements upfront.
2. **Given** an arbitrary stream of items, **When** processed with fixed-size batching, **Then** elements are grouped into discrete chunks of size N with the final chunk containing any remaining elements.
3. **Given** a stream of financial or numerical transactions, **When** processed with cumulative operations, **Then** running totals, running maximums, and prefix sums are emitted incrementally downstream.
4. **Given** a stream of items requiring remote or blocking calls, **When** processed with a concurrency cap, **Then** asynchronous tasks execute concurrently up to the limit while preserving original encounter order.
5. **Given** two separate sequences of elements, **When** combined, **Then** a zipping operation pairs corresponding elements by index into a tuple or composite stream.
6. **Given** a stream containing consecutive repeated items or items sharing consecutive keys, **When** deduplicated or grouped by adjacency, **Then** consecutive duplicates/adjacent runs are collapsed while preserving overall stream flow.
7. **Given** a stream requiring lookahead or lookbehind decisions, **When** evaluated, **Then** elements can be inspected relative to their immediate neighbor before emission (such as local peak detection or delimiter insertion).
8. **Given** a stream under dynamic conditions (such as consecutive error count or rolling threshold crossing), **When** a complex condition is met, **Then** the pipeline halts early and short-circuits gracefully.
9. **Given** a flat stream of input tokens, **When** processed through a stateful transition gatherer, **Then** tokens are parsed into structured multi-token events.
10. **Given** a stream of unknown or unbounded length, **When** evaluated for bounded sample or top-K elements, **Then** the current top-K or reservoir sample is maintained in bounded memory.
11. **Given** a sequence of data records, **When** conditional injection rules apply, **Then** synthetic separator markers or checkpoints are inserted after designated intervals or state transitions.

---

### User Story 2 - Side-by-Side Comparison of Standard Stream Operations (Priority: P2)

As a Java developer familiar with classical Stream intermediate operations, I want side-by-side comparative test examples showing standard stream operations (`filter`, `map`, `limit`/`takeWhile`, `flatMap`, `distinct`, `reduce`) reimplemented using custom Gatherers mechanics, so that I can understand the underlying mechanics and contracts of `Integrator`, `Initializer`, `Finisher`, and `Combiner`.

**Why this priority**: Comparative examples bridge the conceptual gap between traditional Stream concepts and the Gatherer functional interface, clarifying mechanics without promoting unnecessary reimplementation.

**Independent Test**: Can be verified by running `Category2StreamContrastTest` comparing standard Stream calls directly against their custom Gatherer counterparts and asserting equivalent downstream results.

**Acceptance Scenarios**:

1. **Given** an input sequence, **When** filtered using standard `filter(Predicate)` versus a stateless custom `Gatherer`, **Then** both pipelines produce identical filtered results.
2. **Given** an input sequence, **When** transformed using standard `map(Function)` versus a stateless custom `Gatherer`, **Then** both pipelines produce identical transformed elements.
3. **Given** an ordered sequence, **When** truncated using standard `limit(N)` or `takeWhile(Predicate)` versus a short-circuiting custom `Gatherer`, **Then** downstream emission terminates at the exact same index.
4. **Given** a sequence of collections, **When** flattened using standard `flatMap(Function)` versus a multi-emitting custom `Gatherer`, **Then** both produce identical flattened streams.
5. **Given** a stream containing duplicates across the entire sequence, **When** deduplicated via standard `distinct()` versus a stateful Set-backed `Gatherer`, **Then** both yield identical distinct encounter-ordered sets.
6. **Given** a sequence of numbers, **When** accumulated using standard `reduce()` versus `Gatherers.fold()`, **Then** the accumulated terminal/emitted result is identical.

---

### User Story 3 - Negative Space, Boundaries & Anti-Pattern Demonstrations (Priority: P3)

As a software architect or developer, I want clear contrast tests and documentation demonstrating scenarios where using Gatherers is unnecessary, inefficient, or an anti-pattern (such as simple stateless mappings, terminal collectors, global sorting, CPU-heavy parallel computing, or multi-pass algorithms), so that team members avoid over-engineering stream pipelines.

**Why this priority**: Educational reference suites must establish clear boundaries and anti-patterns to prevent misapplication of new APIs where standard collectors, built-ins, or alternative abstractions are strictly superior.

**Independent Test**: Can be verified by running `Category3OverkillBoundariesTest` and inspecting accompanying documentation demonstrating standard idiomatic solutions alongside explanations of why Gatherers are ill-suited.

**Acceptance Scenarios**:

1. **Given** a simple stateless data transformation, **When** contrasting standard `map`/`filter` against custom gatherers, **Then** tests and docstrings demonstrate the clarity and performance advantages of built-in methods.
2. **Given** an aggregation requiring a single terminal value (sum, count, joining, average), **When** contrasting `Stream.collect(Collector)` with intermediate gatherers, **Then** tests demonstrate that `Collectors` are the proper terminal abstraction.
3. **Given** full dataset operations requiring complete buffering (e.g. `sorted()`), **When** evaluated, **Then** tests demonstrate that single-pass Gatherers offer no benefit over built-in `sorted()`.
4. **Given** CPU-bound numerical computations, **When** comparing parallel stream processing to `mapConcurrent`, **Then** tests demonstrate that `mapConcurrent` is designed for virtual-thread I/O-bound tasks rather than compute-bound loops.
5. **Given** two-pass or multi-pass algorithms (such as normalizing values relative to a dataset maximum), **When** evaluated, **Then** tests illustrate why multi-pass or collection-based processing is preferable to single-pass forward-only gatherers.
6. **Given** standard pagination (`skip`/`limit`) or basic unordered grouping (`Collectors.groupingBy`), **When** evaluated, **Then** standard library solutions are demonstrated as the idiomatic choice.

---

### Edge Cases

- **Empty Streams**: How do sliding windows, batching, folding, and custom state machines behave when the stream source contains zero elements?
- **Stream Shorter Than Window/Batch Size**: How does `windowFixed` or `windowSliding` handle a stream with fewer elements than the requested window size?
- **Downstream Early Termination**: What happens when a downstream operation (like `findFirst()` or `limit()`) short-circuits while an active `mapConcurrent` or stateful gatherer is processing?
- **Null Element Handling**: How do custom stateful gatherers handle `null` elements in the stream pipeline?
- **Single Element Streams**: Do cumulative calculations, zipping, and adjacency grouping produce expected outputs on singleton streams?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The test suite MUST provide test methods in `Category1GatherersShineTest` for all 11 Category 1 stateful stream scenarios (sliding window, fixed batching, running calculations, bounded-concurrency mapping, zipping, adjacency deduplication, lookahead/lookbehind, stateful early termination, stream state machines, reservoir sampling, and conditional injection).
- **FR-002**: The test suite MUST provide side-by-side comparison test cases in `Category2StreamContrastTest` for all 6 Category 2 standard operations (`filter`, `map`, `limit`/`takeWhile`, `flatMap`, `distinct`, `reduce`) comparing standard Stream methods with custom Gatherer implementations.
- **FR-003**: The test suite MUST provide negative-space / anti-pattern demonstrations in `Category3OverkillBoundariesTest` for all 7 Category 3 scenarios demonstrating idiomatic built-in alternatives and documenting why Gatherers are overkill or inappropriate.
- **FR-004**: Every test method body MUST follow the Given-When-Then structure with explicit comment demarcations (`// Given`, `// When`, `// Then`).
- **FR-005**: Every test method and test class MUST have an informative `@DisplayName` annotation detailing the specific scenario and expected behavior.
- **FR-006**: Test classes MUST be organized into three category classes (`Category1GatherersShineTest`, `Category2StreamContrastTest`, `Category3OverkillBoundariesTest`) residing under package `nl.invokedynamic.demo.java.gatherers` in `src/test/java`.
- **FR-007**: Reusable custom `Gatherer` factory implementations MUST be placed in `src/main/java/nl/invokedynamic/demo/java/gatherers/custom/` (e.g. `CustomGatherers.java`) using clean production-style signatures.
- **FR-008**: All tests MUST compile and pass under Java 26 using the included Maven wrapper (`./mvnw clean test`).
- **FR-009**: External test dependencies MUST be strictly limited to JUnit 5 (Jupiter).

### Key Entities

- **Stream Pipeline**: The sequence of elements flowing from a source through intermediate operations (including `Stream.gather()`) to a terminal operation or collection.
- **Gatherer**: An intermediate stream operation representing an `Initializer` (state creation), `Integrator` (element processing and downstream emission), `Combiner` (parallel merging), and optional `Finisher` (final cleanup / flush).
- **Gatherers (Built-in Factory)**: The standard Java utility class providing built-in gatherers (`windowFixed`, `windowSliding`, `fold`, `scan`, `mapConcurrent`).
- **CustomGatherers**: Production-ready custom gatherer factories defined under `src/main/java` demonstrating custom `Gatherer.of` and `Gatherer.ofSequential` implementations.
- **Downstream**: The target sink/consumer (`Gatherer.Downstream`) through which transformed elements are pushed and short-circuit signals are communicated.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of the 24 specified use cases across Category 1 (11), Category 2 (6), and Category 3 (7) are covered with executable, passing test methods across the 3 test classes.
- **SC-002**: 100% of test methods include explicit Given-When-Then demarcations and descriptive `@DisplayName` annotations.
- **SC-003**: The entire demonstration test suite compiles cleanly without warnings and executes to completion in under 10 seconds on standard developer machines via `./mvnw clean test`.
- **SC-004**: Code is structured modularly with clear category separation allowing a new reader to locate any specific use case from the documentation in under 30 seconds.

## Assumptions

- **Java Version**: Targeted at Java 26 (and compatible with Java 24+ Stream Gatherers JEP 485).
- **Custom Code Placement**: Production-style custom gatherer implementations reside in `src/main/java/nl/invokedynamic/demo/java/gatherers/custom/`.
- **Test Framework**: JUnit 5 (JUnit Jupiter 5.12.0) is used as the test runner and assertion framework.
- **Self-Contained Examples**: Tests showcase clear use cases without requiring external servers or complex mocks.
