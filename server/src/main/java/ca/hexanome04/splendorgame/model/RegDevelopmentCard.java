package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;

/**
 * Class representing a regular development card (non-orient).
 */
public class RegDevelopmentCard extends DevelopmentCard {

    /**
     * Creates a regular development card by calling super on the abstract development card class.
     *
     * @param tokenType The type of token associated to this card.
     * @param bonus The integer token bonus associated to this card.
     * @param prestigePoints Amount of prestige points associated to this card.
     * @param costType       Cost type associated to this card.
     * @param cardType       Card type associated to this card.
     * @param tokenCost      Token cost associated to this card.
     */
    public RegDevelopmentCard(TokenType tokenType, int bonus, int prestigePoints,
                              CostType costType, CardType cardType,
                              ArrayList<TokenType> tokenCost) {
        super(tokenType, bonus, prestigePoints, costType, cardType, tokenCost);
    }

}
