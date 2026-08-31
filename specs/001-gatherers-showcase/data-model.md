# Data Model: Java Stream Gatherers Showcase

## Overview
This document specifies the lightweight domain records, test data structures, and state model entities used across the showcase and contrast test suites.

---

## 1. Showcase Domain Records

### 1.1 `Transaction`
Used in cumulative calculations and running aggregations (Category 1).
```java
public record Transaction(String id, String category, double amount) {}
```
- **Fields**:
  - `id`: Unique identifier (e.g., `"tx-001"`)
  - `category`: Category group (e.g., `"GROCERY"`, `"ELECTRONICS"`)
  - `amount`: Monetary value

### 1.2 `Measurement`
Used in sliding window analytics, moving averages, and local peak detection (Category 1).
```java
public record Measurement(long timestamp, double value) {}
```
- **Fields**:
  - `timestamp`: Epoch millisecond or sequence index
  - `value`: Numerical reading (e.g. temperature, sensor reading)

### 1.3 `Token` & `TokenEvent`
Used in stream-based state machine / tokenizer parsing (Category 1).
```java
public record Token(TokenType type, String value) {
    public enum TokenType { KEY, VALUE, DELIMITER, EOF }
}

public record TokenEvent(String key, String value) {}
```

### 1.4 `Pair<L, R>`
Used in stream zipping operations (Category 1).
```java
public record Pair<L, R>(L left, R right) {}
```

---

## 2. Gatherer State Model Entities

### 2.1 `ConsecutiveDedupState<T>`
Maintains single-element lookbehind state for consecutive deduplication.
- **State**: `T lastElement` (or `AtomicReference<T>` / `boolean hasElement`)

### 2.2 `ConsecutiveGroupingState<T, K>`
Maintains current grouping key and accumulator list for adjacent grouping.
- **State**: `K currentKey`, `List<T> currentGroup`

### 2.3 `TopKState<T>`
Maintains bounded min-heap for Top-K extraction without full sort.
- **State**: `PriorityQueue<T> heap`, `int k`, `Comparator<T> comparator`

### 2.4 `StateMachineParserState`
Maintains internal token accumulator and transition state for stream parsing.
- **State**: `String pendingKey`, `List<TokenEvent> completedEvents`
