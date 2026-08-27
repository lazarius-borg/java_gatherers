package nl.invokedynamic.demo.java.gatherers.model;

/**
 * A time-series or sequential measurement used in sliding window analytics,
 * moving averages, and local peak/trend detection.
 *
 * @param timestamp epoch millisecond or sequential index
 * @param value     numerical measurement reading
 */
public record Measurement(long timestamp, double value) {}
