package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;

/**
 * Class representing the orient development cards.
 */
public class OrientDevelopmentCard extends DevelopmentCard {

    private final boolean reserveNoble;
    private CascadeType cascadeType;
    private final ArrayList<TokenType> burnBonusCost;


    /**
     * Creates an orient development card by calling super on the abstract development card class.
     *
     * @param tokenType      The type of token associated to this card.
     * @param bonus          The token bonus associated to this card
     * @param cascadeType    The type of cascade associated to this card.
     * @param reserveNoble   Whether this development card allows you to reserve a noble.
     * @param prestigePoints The number of prestige points associated with this card.
     * @param costType       Whether this card has a cost of tokens or burning bonuses.
     * @param cardType       The type of this card.
     * @param tokenCost      The token cost of this card (if applicable).
     * @param burnBonusCost  The bonus burning of this card (if applicable).
     */
    public OrientDevelopmentCard(TokenType tokenType, int bonus, CascadeType cascadeType, boolean reserveNoble,
                                 int prestigePoints, CostType costType, CardType cardType, ArrayList<TokenType> tokenCost,
                                 ArrayList<TokenType> burnBonusCost) {
        super(tokenType, bonus, prestigePoints, costType, cardType, tokenCost);
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
     * @param cardType       The type of this card.
     * @param tokenCost      The token cost of this card (if applicable).
     * @param burnBonusCost  The bonus burning of this card (if applicable).
     */
    public OrientDevelopmentCard(TokenType tokenType, int bonus, boolean reserveNoble, int prestigePoints,
                                 CostType costType, CardType cardType, ArrayList<TokenType> tokenCost,
                                 ArrayList<TokenType> burnBonusCost) {
        super(tokenType, bonus, prestigePoints, costType, cardType, tokenCost);
        this.reserveNoble = reserveNoble;
        this.burnBonusCost = burnBonusCost;
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
    public ArrayList<TokenType> getBurnBonusCost() {
        ArrayList<TokenType> newList = new ArrayList<>();
        for (TokenType t : burnBonusCost) {
            newList.add(t);
        }
        return newList;
    }

}