# Phase 0 Research: Java Stream Gatherers Implementation

## Research Summary & Findings

### 1. Java Stream Gatherers API Architecture (JEP 485 / Java 24+)
- **Core Abstraction**: `java.util.stream.Gatherer<T, A, R>`
  - `T`: Input element type
  - `A`: Intermediate state type (can be `Gatherer.of` stateless `Void` or mutable object)
  - `R`: Output element type
- **Four Functional Pillars**:
  1. `initializer()`: `Supplier<A>` — creates new private state for each stream evaluation.
  2. `integrator()`: `Integrator<A, T, R>` — processes element `T`, updates state `A`, and emits `R` elements to `Downstream<? super R>` via `downstream.push(r)`. Returns `boolean` (`true` to continue, `false` to short-circuit).
  3. `combiner()`: `BinaryOperator<A>` — merges two states during parallel stream evaluation (omitted in `ofSequential`).
  4. `finisher()`: `BiConsumer<A, Downstream<? super R>>` — executes after the input stream ends, allowing remaining buffered state to be pushed downstream.
- **Factory Helpers**:
  - `Gatherer.ofSequential(...)` for sequential-only stateful gatherers (no combiner needed).
  - `Gatherer.of(...)` for parallel-safe gatherers (stateless or with associative combiners).
  - `Gatherers` built-in factory: `windowFixed`, `windowSliding`, `fold`, `scan`, `mapConcurrent`.

---

### 2. Category 1: Advanced Stateful Stream Transformations

#### 2.1 Sliding Window Analytics (Moving Averages, Rolling Min/Max, Peaks)
- **Approach**: Use `Gatherers.windowSliding(windowSize)` to produce `Stream<List<T>>`.
- **Calculations**:
  - Moving average: map window to `window.stream().mapToDouble(...).average().orElse(0.0)`.
  - Rolling max/min: map window to `Collections.max(window)`.
  - Peak detection: inspect triple `[prev, curr, next]` where `curr > prev && curr > next`.

#### 2.2 Fixed-Size Batching / Chunking
- **Approach**: Use `Gatherers.windowFixed(batchSize)` to chunk stream into discrete `List<T>` batches. Final batch contains remaining elements if stream size % batchSize != 0.

#### 2.3 Cumulative / Running Computations (Prefix Sums, Running Max)
- **Approach**: Use `Gatherers.scan(initialSupplier, scannerFunction)` to emit cumulative running state after every incoming element.

#### 2.4 Bounded-Concurrency Mapping (`Gatherers.mapConcurrent`)
- **Approach**: Use `Gatherers.mapConcurrent(maxConcurrency, mapperFunction)`.
- **Concurrency & Order**: Runs mapping functions asynchronously using Virtual Threads while guaranteeing strict encounter order downstream.
- **Test Strategy**: Use 10ms simulated latency to demonstrate concurrent wall-clock savings while verifying execution in < 1 second.

#### 2.5 Zipping Two Streams
- **Approach**: Custom gatherer `CustomGatherers.zip(Stream<U> other, BiFunction<T, U, R> zipper)`.
- **State**: Holds `Iterator<U>`. For each incoming `T`, if `iterator.hasNext()`, emits `zipper.apply(t, iterator.next())`; otherwise short-circuits.

#### 2.6 Consecutive Deduplication & Adjacency Grouping
- **Approach**:
  - Consecutive Deduplication: `CustomGatherers.dedupConsecutive()` tracking last seen element.
  - Adjacency Grouping: `CustomGatherers.groupConsecutiveBy(Function<T, K> classifier)` collecting adjacent elements sharing identical key into a `List<T>`.

#### 2.7 Lookahead / Lookbehind Logic
- **Approach**: Custom gatherer or sliding window tracking previous and lookahead state to evaluate conditions before emission.

#### 2.8 Stateful Early Termination on Complex Condition
- **Approach**: Custom gatherer tracking dynamic state (e.g. failure count or rolling sum). When condition is reached, returns `false` from integrator to short-circuit immediately.

#### 2.9 Stream-Based State Machine / Tokenizer
- **Approach**: State machine holding current parsing state (e.g. inside block, reading header, reading body) and emitting structured domain events upon transitions.

#### 2.10 Reservoir Sampling / Bounded Top-K
- **Approach**:
  - Bounded Top-K: State maintains a min-heap (`PriorityQueue`) of size K. In finisher, drains sorted heap to downstream.
  - Reservoir Sampling: State maintains reservoir array of size K with index counter; finisher emits sampled elements.

#### 2.11 Conditional Element Injection
- **Approach**: Custom gatherer with counter/predicate that pushes extra synthetic marker/separator elements downstream prior to or after target elements.

---

### 3. Category 2: Standard Operations Reimplemented via Custom Gatherers
- `filter(Predicate)`: Stateless `Gatherer.of((_, element, downstream) -> { if (predicate.test(element)) return downstream.push(element); return true; })`.
- `map(Function)`: Stateless `Gatherer.of((_, element, downstream) -> downstream.push(mapper.apply(element)))`.
- `limit(long max)` / `takeWhile(Predicate)`: Stateful counter/predicate returning `false` once limit reached.
- `flatMap(Function<T, Stream<R>>)`: Iterates inner stream and pushes elements downstream.
- `distinct()`: Stateful `Gatherer.ofSequential(HashSet::new, (seen, element, downstream) -> seen.add(element) ? downstream.push(element) : true)`.
- `reduce(BinaryOperator)`: Evaluated via `Gatherers.fold(initialSupplier, accumulator)`.

---

### 4. Category 3: Boundaries, Overkill & Anti-Patterns
- **Stateless map/filter chains**: Plain `Stream.map().filter()` is cleaner and has lower allocation overhead.
- **Terminal Aggregations**: `Collectors.joining()`, `Collectors.summingInt()` are terminal operations; gatherers are intermediate.
- **Global Sorting**: `Stream.sorted()` is native; a gatherer would buffer everything and duplicate sorting.
- **CPU-Bound Work**: Parallel streams (`.parallel()`) leverage ForkJoinPool; `mapConcurrent` targets blocking I/O on virtual threads.
- **Multi-Pass Algorithms**: Normalizing against global max requires two passes; forcing it into a single forward gatherer requires full memory buffering.
- **Pagination & Basic Grouping**: `skip/limit` and `Collectors.groupingBy` are standard idioms.
