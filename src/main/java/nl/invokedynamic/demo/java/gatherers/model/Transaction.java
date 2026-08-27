package nl.invokedynamic.demo.java.gatherers.model;

/**
 * A financial or numerical transaction used in running totals and cumulative computations.
 *
 * @param id       unique transaction identifier
 * @param category transaction category
 * @param amount   monetary amount
 */
public record Transaction(String id, String category, double amount) {}
