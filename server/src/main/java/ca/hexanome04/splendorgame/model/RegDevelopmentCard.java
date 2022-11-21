package ca.hexanome04.splendorgame.model;

/**
 * Class representing a regular development card (non-orient).
 */
public class RegDevelopmentCard extends DevelopmentCard {

    /**
     * Creates a regular development card by calling super on the abstract development card class.
     *
     * @param tokenType The type of token associated to this card.
     *
     * @param bonus The integer token bonus associated to this card.
     */
    public RegDevelopmentCard(TokenType tokenType, Integer bonus) {
        super(tokenType, bonus);
    }

}
