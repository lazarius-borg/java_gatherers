# Quickstart: Java Stream Gatherers Showcase

## Prerequisites
- **JDK**: Java 26 (Adoptium / Temurin or compatible)
- **Maven**: Maven 3.9+ (or use the included Maven wrapper `./mvnw`)

---

## Running the Showcase Test Suite

### 1. Run All Tests
To execute all 24 use cases across Category 1, Category 2, and Category 3:
```bash
./mvnw clean test
```

### 2. Run Category 1 Tests (Where Gatherers Shine)
```bash
./mvnw test -Dtest=Category1GatherersShineTest
```
Covers sliding windows, fixed batching, cumulative computations, bounded-concurrency mapping, zipping, adjacency grouping, lookahead logic, state machines, reservoir sampling, and element injection.

### 3. Run Category 2 Tests (Stream Reimplementations Contrast)
```bash
./mvnw test -Dtest=Category2StreamContrastTest
```
Covers side-by-side comparisons of `filter`, `map`, `limit`, `flatMap`, `distinct`, and `reduce`.

### 4. Run Category 3 Tests (Boundaries & Overkill Demonstrations)
```bash
./mvnw test -Dtest=Category3OverkillBoundariesTest
```
Demonstrates idiomatic usage of built-in Streams, `Collectors`, parallel streams, and multi-pass algorithms where Gatherers are unnecessary or an anti-pattern.

---

## Expected Validation Output
- All tests pass with zero failures or errors.
- Test output clearly displays readable `@DisplayName` labels for each educational scenario.
- Total execution time is under 3 seconds.
