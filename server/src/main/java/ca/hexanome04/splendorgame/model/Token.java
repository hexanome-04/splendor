package ca.hexanome04.splendorgame.model;

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

}