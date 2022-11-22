package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;

/**
 * Class representing a Card object.
 */
public abstract class Card {

    private final int prestigePoints;
    private final CostType costType;
    private final CardType cardType;
    private final ArrayList<TokenType> tokenCost;

    // maybe attribute for the actual image of the card to be added?
    // add an attribute for a list of tokens representing the token/bonus cost for this card

    /**
     * Creates a new card with provided prestige point amount, cost type and card type.
     *
     * @param prestigePoints Amount of prestige points associated to this card.
     * @param costType       Cost type associated to this card.
     * @param cardType       Card type associated to this card.
     * @param tokenCost      Token cost associated to this card.
     */
    public Card(int prestigePoints, CostType costType, CardType cardType,
                ArrayList<TokenType> tokenCost) {
        this.prestigePoints = prestigePoints;
        this.costType = costType;
        this.cardType = cardType;
        this.tokenCost = tokenCost;
    }

    /**
     * Get the cost type of this card (tokens or bonuses).
     *
     * @return The cost type of the card.
     */
    public CostType getCostType() {
        return costType;
    }

    /**
     * Get the number of prestige points associated to this card.
     *
     * @return The number of prestige points.
     */
    public int getPrestigePoints() {
        return prestigePoints;
    }
    
    /** Return the type of the card instance.
     *
     * @return the card type
     */
    public CardType getCardType() {
        return cardType;
    }

    /**
     * Returns the cost (in tokens) of the Card.
     *
     * @return Returns new list of TokenTypes.
     */
    public ArrayList<TokenType> getTokenCost() {
        return new ArrayList<>(tokenCost);
    }

}
