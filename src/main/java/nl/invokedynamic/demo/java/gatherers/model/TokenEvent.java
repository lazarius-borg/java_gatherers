package nl.invokedynamic.demo.java.gatherers.model;

/**
 * A structured domain event produced by the stream-based state machine parser.
 *
 * @param key   the parsed key
 * @param value the parsed value
 */
public record TokenEvent(String key, String value) {}
