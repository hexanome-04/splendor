package ca.hexanome04.splendorgame.model;

/**
 * Abstract class that represents a development card.
 */
public abstract class DevelopmentCard implements CardType {
    private final TokenType tokenType;
    private final Integer bonus;

    /**
     * Creates a development card from the class in which this was called super from.
     *
     * @param tokenType The type of token associated to this development card.
     *
     * @param bonus The integer token bonus associated to this development card.
     */
    public DevelopmentCard(TokenType tokenType, Integer bonus) {
        this.tokenType = tokenType;
        this.bonus = bonus;
    }
}
