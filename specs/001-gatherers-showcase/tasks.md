# Tasks: Java Stream Gatherers Showcase & Contrast Suite

**Feature**: Java Stream Gatherers Showcase & Contrast Suite  
**Branch**: `001-gatherers-showcase`  
**Input**: [specs/001-gatherers-showcase/spec.md](file:///Users/lazolazarev/projects/java_gatherers/specs/001-gatherers-showcase/spec.md) | [specs/001-gatherers-showcase/plan.md](file:///Users/lazolazarev/projects/java_gatherers/specs/001-gatherers-showcase/plan.md)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure verification

- [X] T001 Verify project structure and Maven build configuration in `pom.xml`

---

## Phase 2: Foundational (Domain Models & Shared Records)

**Purpose**: Core domain records required across showcase tests and custom gatherers

**⚠️ CRITICAL**: Must complete before user story test and gatherer implementations

- [X] T002 [P] Create `Measurement` record in `src/main/java/nl/invokedynamic/demo/java/gatherers/model/Measurement.java`
- [X] T003 [P] Create `Transaction` record in `src/main/java/nl/invokedynamic/demo/java/gatherers/model/Transaction.java`
- [X] T004 [P] Create `Pair` generic record in `src/main/java/nl/invokedynamic/demo/java/gatherers/model/Pair.java`
- [X] T005 [P] Create `Token` and `TokenEvent` records in `src/main/java/nl/invokedynamic/demo/java/gatherers/model/Token.java`

**Checkpoint**: Foundation ready — domain records available for custom gatherers and test suites.

---

## Phase 3: User Story 1 - Advanced Stream Transformations Beyond Standard Streams (Priority: P1) 🎯 MVP

**Goal**: Implement and demonstrate the 11 Category 1 stateful stream scenarios (sliding window, fixed batching, cumulative computations, bounded-concurrency mapping, zipping, adjacency deduplication, lookahead/lookbehind, stateful early termination, state machines, reservoir sampling, conditional injection).

**Independent Test**: Execute `Category1GatherersShineTest` via `./mvnw test -Dtest=Category1GatherersShineTest` and verify all 11 stateful tests pass with Given-When-Then structure and `@DisplayName` annotations.

### Implementation for User Story 1

- [X] T006 [US1] Implement Category 1 custom Gatherer utilities (`zip`, `dedupConsecutive`, `groupConsecutiveBy`, `takeUntilConsecutiveFailures`, `parseTokenEvents`, `topK`, `injectSeparator`) in `src/main/java/nl/invokedynamic/demo/java/gatherers/custom/CustomGatherers.java`
- [X] T007 [US1] Implement Category 1 test suite illustrating all 11 stateful scenarios following Given-When-Then structure in `src/test/java/nl/invokedynamic/demo/java/gatherers/Category1GatherersShineTest.java`

**Checkpoint**: User Story 1 complete and independently testable via `./mvnw test -Dtest=Category1GatherersShineTest`.

---

## Phase 4: User Story 2 - Side-by-Side Comparison of Standard Stream Operations (Priority: P2)

**Goal**: Implement and demonstrate side-by-side comparisons of standard Stream operations (`filter`, `map`, `limit`/`takeWhile`, `flatMap`, `distinct`, `reduce`) reimplemented via custom Gatherers.

**Independent Test**: Execute `Category2StreamContrastTest` via `./mvnw test -Dtest=Category2StreamContrastTest` and verify all 6 comparison test cases pass with Given-When-Then assertions.

### Implementation for User Story 2

- [X] T008 [US2] Implement Category 2 custom Gatherer reimplementations (`filter`, `map`, `limit`, `flatMap`, `distinct`) in `src/main/java/nl/invokedynamic/demo/java/gatherers/custom/CustomGatherers.java`
- [X] T009 [US2] Implement Category 2 test suite comparing standard Stream operations against custom Gatherers in `src/test/java/nl/invokedynamic/demo/java/gatherers/Category2StreamContrastTest.java`

**Checkpoint**: User Story 1 and 2 complete and testable independently.

---

## Phase 5: User Story 3 - Negative Space, Boundaries & Anti-Pattern Demonstrations (Priority: P3)

**Goal**: Implement test cases and documentation demonstrating the 7 scenarios where Gatherers are overkill or an anti-pattern compared to standard Streams, `Collectors`, parallel streams, or multi-pass algorithms.

**Independent Test**: Execute `Category3OverkillBoundariesTest` via `./mvnw test -Dtest=Category3OverkillBoundariesTest` and verify all 7 boundary test cases pass with Given-When-Then structure and clear documentation.

### Implementation for User Story 3

- [X] T010 [US3] Implement Category 3 boundary and anti-pattern demonstration test suite in `src/test/java/nl/invokedynamic/demo/java/gatherers/Category3OverkillBoundariesTest.java`

**Checkpoint**: All 24 use cases across Categories 1, 2, and 3 are implemented and independently verifiable.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cleanup obsolete boilerplate, align documentation, and validate full suite execution

- [X] T011 [P] Remove initial placeholder `GatherersTest.java` in `src/test/java/nl/invokedynamic/demo/java/gatherers/GatherersTest.java`
- [X] T012 [P] Update `README.md` with complete categorization, usage guidelines, and test execution instructions in `README.md`
- [X] T013 Execute full validation suite via `./mvnw clean test` and verify execution speed under 3 seconds

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately.
- **Foundational (Phase 2)**: Depends on Phase 1 completion — blocks User Stories.
- **User Stories (Phases 3, 4, 5)**:
  - User Story 1 (Phase 3): Depends on Phase 2 (Foundational).
  - User Story 2 (Phase 4): Depends on Phase 2 (Foundational).
  - User Story 3 (Phase 5): Depends on Phase 2 (Foundational).
- **Polish (Phase 6)**: Depends on all User Stories completion.

### Parallel Opportunities

- Foundational tasks T002, T003, T004, T005 can all execute in parallel.
- Polish tasks T011 and T012 can execute in parallel before final test run T013.

---

## Implementation Strategy

### MVP First (User Story 1 Only)
1. Complete Setup (T001) + Foundational models (T002–T005).
2. Implement Custom Gatherers utilities (T006) and Category 1 test suite (T007).
3. Validate via `./mvnw test -Dtest=Category1GatherersShineTest`.

### Incremental Delivery
1. Add User Story 2 custom gatherer reimplementations (T008) and contrast tests (T009).
2. Add User Story 3 boundary and anti-pattern demonstrations (T010).
3. Perform final polish, README documentation, and full `./mvnw clean test` verification (T011–T013).
