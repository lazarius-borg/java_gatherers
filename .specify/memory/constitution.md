<!--
Sync Impact Report:
- Version change: Initial Template -> 1.0.0
- List of principles:
  - PRINCIPLE_1: I. Given-When-Then Test Structure
  - PRINCIPLE_2: II. Explicit Test Purpose via @DisplayName
  - PRINCIPLE_3: III. Educational Readability and Simplicity First
  - PRINCIPLE_4: IV. Test-Centric Demonstration Suite
  - PRINCIPLE_5: V. Modern Java & Minimal Dependencies
- Added sections:
  - Code Organization & Test Conventions
  - Development & Verification Workflow
  - Governance
- Removed sections: None
- Deferred items: None
-->

# java.gatherers Constitution

## Core Principles

### I. Given-When-Then Test Structure
All test bodies MUST strictly follow the Given -> When -> Then (Arrange-Act-Assert) pattern. Each phase MUST be explicitly structured and demarcated (e.g., using `// Given`, `// When`, `// Then` comments or explicit block structuring) so that setup, action, and assertions are unambiguous for educational review.

### II. Explicit Test Purpose via @DisplayName
Every test class and test method MUST include a clear, human-readable `@DisplayName` annotation. The annotation MUST concisely explain the educational intent, the specific gatherer or operation under demonstration, and the expected behavior or edge case being illustrated.

### III. Educational Readability and Simplicity First
All code MUST prioritize readability, clean idioms, and simplicity over brevity or micro-optimizations. Tests serve as runnable documentation for developers learning Java Stream Gatherers; variables, method signatures, and stream pipelines MUST be self-explanatory and easy to follow.

### IV. Test-Centric Demonstration Suite
The project is educational and test-centric; tests represent the primary documentation and usage examples. Standalone application entry points (`main` methods) or runnable binaries are unnecessary unless specifically required for interactive benchmarks.

### V. Modern Java & Minimal Dependencies
Code MUST utilize modern Java (Java 24+ Stream Gatherers API, specifically `java.util.stream.Gatherer` and `java.util.stream.Gatherers`). External dependencies MUST be restricted to the bare minimum required for testing (JUnit 5 as the sole test framework dependency).

## Code Organization & Test Conventions

- **Package Hierarchy**: All classes and tests MUST reside under the root package namespace `nl.invokedynamic.demo.java.gatherers` (or logical sub-packages like `.builtin` and `.custom` when categorizing gatherers).
- **Categorization**: Tests should be organized cleanly by theme (e.g., built-in gatherers: `windowFixed`, `windowSliding`, `fold`, `scan`, `mapConcurrent`, vs. custom `Gatherer` implementations).
- **Assertions**: Assertions SHOULD use standard JUnit Jupiter assertions (`assertEquals`, `assertIterableEquals`, etc.) with clear expected vs. actual values.

## Development & Verification Workflow

- **Build Tool**: The project uses Maven with the provided Maven wrapper (`./mvnw`).
- **Verification**: All examples and tests MUST compile cleanly with target Java 26 / release 26 and pass all tests via `./mvnw clean test`.
- **Zero Warnings**: Code should remain free of compiler warnings and deprecated constructs.

## Governance

- **Supremacy**: This constitution defines the non-negotiable architectural and stylistic standards for the `java.gatherers` repository.
- **Amendments**: Amendments require updating this document with an incremented version number and a documented rationale in the Sync Impact Report.
- **Versioning Policy**:
  - **MAJOR**: Incompatible governance shifts, removal of core test/style requirements.
  - **MINOR**: Addition of new principles, architectural guidelines, or expanded conventions.
  - **PATCH**: Clarifications, wording refinements, and typo fixes.
- **Compliance Review**: All incoming PRs and generated features must be checked for adherence to Given-When-Then structure, `@DisplayName` presence, and readability standards.

**Version**: 1.0.0 | **Ratified**: 2026-08-27 | **Last Amended**: 2026-08-27
