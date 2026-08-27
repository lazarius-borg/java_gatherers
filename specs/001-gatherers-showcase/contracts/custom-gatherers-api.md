# Contract: Custom Gatherers Public API

## Class: `nl.invokedynamic.demo.java.gatherers.custom.CustomGatherers`

`CustomGatherers` is a production-style utility class providing factory methods for custom `Gatherer` instances demonstrating custom `Gatherer.of(...)` and `Gatherer.ofSequential(...)` patterns.

---

### Method Signatures

#### 1. `zip`
Combines an input stream element-by-element with another stream.
```java
public static <T, U, R> Gatherer<T, ?, R> zip(
    Stream<U> other, 
    BiFunction<? super T, ? super U, ? extends R> zipper
)
```

#### 2. `dedupConsecutive`
Removes adjacent duplicate elements from a stream.
```java
public static <T> Gatherer<T, ?, T> dedupConsecutive()
```

#### 3. `groupConsecutiveBy`
Groups consecutive elements sharing the same key extracted by a classifier.
```java
public static <T, K> Gatherer<T, ?, List<T>> groupConsecutiveBy(
    Function<? super T, ? extends K> classifier
)
```

#### 4. `takeUntil` / `takeWhileWithFailureLimit`
Halts stream early when a dynamic stopping condition or error threshold is reached.
```java
public static <T> Gatherer<T, ?, T> takeUntilConsecutiveFailures(
    Predicate<? super T> isFailure, 
    int maxConsecutiveFailures
)
```

#### 5. `parseTokenEvents`
Parses flat tokens into structured `TokenEvent` instances using a stream state machine.
```java
public static Gatherer<Token, ?, TokenEvent> parseTokenEvents()
```

#### 6. `topK`
Maintains the top-K elements in bounded memory without sorting the entire dataset.
```java
public static <T> Gatherer<T, ?, T> topK(
    int k, 
    Comparator<? super T> comparator
)
```

#### 7. `injectSeparator`
Conditionally injects a separator element after every N elements.
```java
public static <T> Gatherer<T, ?, T> injectSeparator(
    int everyN, 
    Supplier<T> separatorSupplier
)
```

#### 8. Category 2 Reimplemented Operations
- `filter(Predicate<T>)`
- `map(Function<T, R>)`
- `limit(long maxSize)`
- `flatMap(Function<T, Stream<R>>)`
- `distinct()`
