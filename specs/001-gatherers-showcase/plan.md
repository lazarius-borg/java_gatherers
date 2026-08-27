# Implementation Plan: Java Stream Gatherers Showcase & Contrast Suite

**Branch**: `001-gatherers-showcase` | **Date**: 2026-08-27 | **Spec**: [specs/001-gatherers-showcase/spec.md](file:///Users/lazolazarev/projects/java_gatherers/specs/001-gatherers-showcase/spec.md)

**Input**: Feature specification from `/specs/001-gatherers-showcase/spec.md`

## Summary

Implement an educational reference suite showcasing Java Stream Gatherers (introduced in Java 24 / JEP 485, running on Java 26). The project implements 24 concrete use cases structured into three core categories:
1. **Category 1**: Advanced stateful stream operations where Gatherers excel (`Category1GatherersShineTest`).
2. **Category 2**: Side-by-side mechanical comparisons of standard Stream operations reimplemented via custom Gatherers (`Category2StreamContrastTest`).
3. **Category 3**: Negative-space and boundary demonstrations where Gatherers are overkill or inappropriate (`Category3OverkillBoundariesTest`).

Reusable custom Gatherers are organized into a production-style utility class [`CustomGatherers`](file:///Users/lazolazarev/projects/java_gatherers/src/main/java/nl/invokedynamic/demo/java/gatherers/custom/CustomGatherers.java) under `src/main/java`. All tests strictly follow the Given -> When -> Then pattern and include descriptive `@DisplayName` annotations.

## Technical Context

**Language/Version**: Java 26 (OpenJDK / Temurin 26.0.2)

**Primary Dependencies**: Standard Library (`java.util.stream.Gatherer`, `java.util.stream.Gatherers`)

**Storage**: N/A (In-memory stream processing)

**Testing**: JUnit Jupiter 5.12.0 (JUnit 5 BOM)

**Target Platform**: JVM (macOS / Linux / Windows)

**Project Type**: Educational Library & Test Reference Suite

**Performance Goals**: Full test suite execution in < 3 seconds via Maven wrapper

**Constraints**: Zero external runtime dependencies; JUnit 5 sole test dependency; strictly adhere to Given-When-Then test structure and `@DisplayName` annotations

**Scale/Scope**: 24 distinct use case scenarios across 3 test classes and 1 custom gatherers utility class

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Requirement | Status | Verification / Design Alignment |
|---|---|---|---|
| **I. Given-When-Then Structure** | All test bodies strictly structured with `// Given`, `// When`, `// Then` | **PASS** | Enforced across all test methods in all 3 test suites |
| **II. Explicit Test Purpose via `@DisplayName`** | Every test class and method must have informative `@DisplayName` | **PASS** | Every test method explicitly names the scenario and expected outcome |
| **III. Educational Readability First** | Code readable, clear, self-explanatory idioms | **PASS** | Clear record types, clean method naming, descriptive pipelines |
| **IV. Test-Centric Demonstration** | Tests serve as runnable documentation | **PASS** | 24 runnable test cases representing the reference cookbook |
| **V. Modern Java & Minimal Dependencies** | Java 26 + JUnit 5 only | **PASS** | Uses `pom.xml` with `maven.compiler.release` 26 and `junit-jupiter` only |

## Project Structure

### Documentation (this feature)

```text
specs/001-gatherers-showcase/
├── plan.md              # Implementation plan (this file)
├── research.md          # Phase 0 research findings
├── data-model.md        # Phase 1 data model & domain records
├── quickstart.md        # Phase 1 validation and execution guide
├── contracts/
│   └── custom-gatherers-api.md # Phase 1 CustomGatherers API specification
└── checklists/
    └── requirements.md  # Spec quality checklist
```

### Source Code (repository root)

```text
src/
├── main/java/
│   └── nl/invokedynamic/demo/java/gatherers/
│       ├── model/
│       │   ├── Measurement.java
│       │   ├── Pair.java
│       │   ├── Token.java
│       │   ├── TokenEvent.java
│       │   └── Transaction.java
│       └── custom/
│           └── CustomGatherers.java
└── test/java/
    └── nl/invokedynamic/demo/java/gatherers/
        ├── Category1GatherersShineTest.java
        ├── Category2StreamContrastTest.java
        └── Category3OverkillBoundariesTest.java
```

**Structure Decision**: Standard Maven project layout with domain models and custom gatherers in `src/main/java` and the three categorized test suites in `src/test/java`.

## Complexity Tracking

*No constitution violations or unjustified complexities.*
