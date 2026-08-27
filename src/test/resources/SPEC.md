# Java 24 Gatherers Showcase — Use Case Requirements

## Purpose
Educational, public reference project demonstrating the `Stream.gather()` API introduced in Java 24 (JEP 485). No sample "business domain" application — the project consists of test classes, each organized around a use case, serving as a living reference for when and how to reach for Gatherers.

## Scope Notes
- No implementation guidance included here — deliberately left for the build phase.
- Organize as one test class (or a small group of them) per use case below, grouped by category.
- Favor clarity of intent in test/method naming over cleverness — this is a reference, not a puzzle.

---

## Category 1 — Where Gatherers Shine (Streams alone are hard or impossible)

These rely on Gatherers' core capabilities: carrying state across elements and controlling emission timing within a pipeline.

1. Sliding window analytics — moving average, moving max/min, trend/peak detection over a window of N elements
2. Fixed-size batching — chunking a stream into batches for bulk inserts / batch API calls
3. Running/cumulative computations — running total, running max, cumulative standard deviation, prefix sums
4. Bounded-concurrency mapping with order preservation — slow I/O calls (HTTP, DB) per element with a concurrency cap, results in encounter order
5. Zipping two streams — pairing elements from two sources index-wise
6. Consecutive deduplication / adjacency-based grouping — collapse consecutive duplicates, or group consecutive elements sharing a key (distinct from `Collectors.groupingBy`)
7. Lookahead/lookbehind logic — decisions that need to peek at the next element before acting on the current one (e.g. detect local maxima, insert separators between differing elements)
8. Stateful early termination on a complex condition — e.g. stop after 3 consecutive failures, or once a rolling average crosses a threshold
9. Simple stream-based state machines / tokenizers — parsing a flat token sequence where transitions depend on prior tokens
10. Reservoir sampling / bounded top-K maintenance — running bounded sample or running top-K without a full sort
11. Conditional injection based on position or history — insert a separator/marker every N elements, or after a state change

## Category 2 — Standard Stream Operations Reimplemented via Gatherers (for contrast)

Side-by-side comparisons: built-in Stream operation vs. the same behavior expressed as a custom Gatherer. Demonstrates mechanics, not superiority.

1. `filter` reimplemented as a stateless gatherer
2. `map` reimplemented as a stateless gatherer
3. `limit` / `takeWhile` reimplemented via a gatherer tracking a counter/predicate with short-circuit
4. `flatMap` reimplemented via a gatherer emitting 0..n elements per input (compare with `mapMulti`)
5. Whole-stream `distinct()` reimplemented using a `Set` as gatherer state
6. `reduce` reimplemented via `Gatherers.fold` (note: fold emits one value downstream, unlike a terminal reduce)

## Category 3 — Where Gatherers Would Be Overkill or Wrong Tool

Negative-space examples establishing the boundaries of appropriate use.

1. Simple stateless `map`/`filter` chains — a custom gatherer is pure ceremony over built-ins
2. Terminal aggregations (`sum`, `count`, `average`, `joining`) — that's what `Collectors` is for; Gatherers are intermediate operations, not a Collector replacement
3. Plain `sorted()` — sorting needs the whole dataset anyway; no benefit from forcing it through a single-pass abstraction
4. CPU-bound parallel numeric work — `Gatherers.mapConcurrent` targets I/O-bound blocking calls, not CPU-heavy computation (use parallel streams / structured concurrency instead)
5. Multi-pass or random/backward-access requirements — Gatherers are single-pass, forward-only; e.g. two-pass normalization against a whole-dataset max is an awkward fit
6. Basic pagination (`skip`/`limit`) — already trivial with built-ins
7. One-off, no-state grouping with no ordering/adjacency requirement — plain `Collectors.groupingBy` is simpler and clearer

---

## Deliverable Structure (suggested, non-binding)
- One test class per use case, or grouped by category as three top-level suites
- Each test/method name should state the use case, not the mechanism
- README should map back to this document's category structure for navigation