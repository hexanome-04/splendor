package ca.hexanome04.splendorgame.model;

import java.util.Objects;

/**
 * Class representing a Token.
 */
public class Token {

    private TokenType type;

    /**
     * Creates a token with the given token type.
     *
     * @param type Type of the associated token.
     */
    public Token(TokenType type) {
        this.type = type;
    }

    /**
     * Gets the type (aka gem colour) of this token.
     *
     * @return Type of token.
     */
    public TokenType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token token)) return false;
        return type == token.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}