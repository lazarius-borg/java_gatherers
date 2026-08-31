package nl.invokedynamic.demo.java.gatherers.model;

/**
 * A token element used in stream-based tokenizer and state machine parsing.
 *
 * @param type  the token classification
 * @param value the string value
 */
public record Token(TokenType type, String value) {

    public enum TokenType {
        KEY,
        VALUE,
        DELIMITER,
        EOF
    }

    public static Token key(String value) {
        return new Token(TokenType.KEY, value);
    }

    public static Token value(String value) {
        return new Token(TokenType.VALUE, value);
    }

    public static Token delimiter(String value) {
        return new Token(TokenType.DELIMITER, value);
    }

    public static Token eof() {
        return new Token(TokenType.EOF, "");
    }
}
