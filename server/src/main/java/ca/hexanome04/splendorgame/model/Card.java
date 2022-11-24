package ca.hexanome04.splendorgame.model;

import java.util.HashMap;

/**
 * Class representing a Card object.
 */
public abstract class Card {

    private final String ID;
    private final int prestigePoints;
    private final CostType costType;
    private final HashMap<TokenType, Integer> tokenCost;

    // maybe attribute for the actual image of the card to be added?
    // add an attribute for a list of tokens representing the token/bonus cost for this card

    /**
     * Creates a new card with provided prestige point amount, cost type and card type.
     *
     * @param prestigePoints Amount of prestige points associated to this card.
     * @param costType       Cost type associated to this card.
     * @param tokenCost      Token cost associated to this card.
     */
    public Card(int prestigePoints, CostType costType, HashMap<TokenType, Integer> tokenCost, int cardNumber) {
        this.prestigePoints = prestigePoints;
        this.costType = costType;
        this.tokenCost = tokenCost;
        this.ID = generateID(cardNumber);
    }

    private String generateID(int cardNumber) {
        String s = "";
        // Should look like: (is orient, [0,1]) (tier, [1,3]) (card number, [1,30])


        // Check if orient
        if(this instanceof OrientDevelopmentCard) {
            s += "1";
        } else {
            s += "0";
        }



        return s;
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

    /**
     * Returns the cost (in tokens) of the Card.
     *
     * @return Returns new list of TokenTypes.
     */
    public HashMap<TokenType, Integer> getTokenCost() {
        return new HashMap<TokenType, Integer>(tokenCost);
    }

    /**
     * Get ID of this card.
     *
     * @return id of card
     */
    public String getID() {
        return this.ID;
    }

}
