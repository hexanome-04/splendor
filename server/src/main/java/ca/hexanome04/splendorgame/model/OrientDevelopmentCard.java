package ca.hexanome04.splendorgame.model;

import java.util.HashMap;

/**
 * Class representing the orient development cards.
 */
public class OrientDevelopmentCard extends DevelopmentCard {

    private final boolean reserveNoble;
    private CascadeType cascadeType;
    private final HashMap<TokenType, Integer> burnBonusCost;


    /**
     * Creates an orient development card by calling super on the abstract development card class.
     *
     * @param tokenType      The type of token associated to this card.
     * @param bonus          The token bonus associated to this card
     * @param cascadeType    The type of cascade associated to this card.
     * @param reserveNoble   Whether this development card allows you to reserve a noble.
     * @param prestigePoints The number of prestige points associated with this card.
     * @param costType       Whether this card has a cost of tokens or burning bonuses.
     * @param tokenCost      The token cost of this card (if applicable).
     * @param burnBonusCost  The bonus burning of this card (if applicable).
     */
    public OrientDevelopmentCard(CardTier cardTier, TokenType tokenType, int bonus, CascadeType cascadeType, boolean reserveNoble,
                                 int prestigePoints, CostType costType, HashMap<TokenType, Integer> tokenCost,
                                 HashMap<TokenType, Integer> burnBonusCost, int cardNumber) {
        super(cardTier, tokenType, bonus, prestigePoints, costType, tokenCost, cardNumber);
        this.reserveNoble = reserveNoble;
        this.cascadeType = cascadeType;
        this.burnBonusCost = burnBonusCost;
    }

    /**
     * Creates an orient development card by calling super on the abstract development card class.
     *
     * @param tokenType      The type of token associated to this card.
     * @param bonus          The token bonus associated to this card
     * @param reserveNoble   Whether this development card allows you to reserve a noble.
     * @param prestigePoints The number of prestige points associated with this card.
     * @param costType       Whether this card has a cost of tokens or burning bonuses.
     * @param tokenCost      The token cost of this card (if applicable).
     * @param burnBonusCost  The bonus burning of this card (if applicable).
     */
    public OrientDevelopmentCard(CardTier cardTier, TokenType tokenType, int bonus, boolean reserveNoble, int prestigePoints,
                                 CostType costType, HashMap<TokenType, Integer> tokenCost,
                                 HashMap<TokenType, Integer> burnBonusCost, int cardNumber) {
        this(cardTier, tokenType, bonus, CascadeType.None, reserveNoble, prestigePoints, costType, tokenCost, burnBonusCost, cardNumber);
    }

    /**
     * Get whether this card allows the player to reserve a noble.
     *
     * @return Whether it is a reserve noble card.
     */
    public boolean getReserveNoble() {
        return reserveNoble;
    }

    /**
     * Get whether this card allows the player to select another lower tier card in a cascade.
     *
     * @return Whether it is a cascade card.
     */
    public boolean isCascade() {
        return !(cascadeType == CascadeType.None);
    }

    /**
     * Get the cascade tier (and whether this card is even a cascade).
     *
     * @return The tier of cascade and if there is a cascade.
     */
    public CascadeType getCascadeType() {
        return cascadeType;
    }

    /**
     * Get the cost for cards that require burning bonuses for purchase.
     *
     * @return The bonuses that must be burned.
     */
    public HashMap<TokenType, Integer> getBurnBonusCost() {
        return new HashMap<TokenType, Integer>(burnBonusCost);
    }

}
