package ca.hexanome04.splendorgame.model;

import java.util.HashMap;

/**
 * Class representing the orient development cards.
 */
public class OrientDevelopmentCard extends DevelopmentCard {

    private final boolean reserveNoble;
    private final boolean isSatchel;
    private CascadeType cascadeType;

    /**
     * Creates an orient development card by calling super on the abstract development card class.
     *
     * @param tokenType      The type of token associated to this card.
     * @param bonus          The token bonus associated to this card
     * @param cascadeType    The type of cascade associated to this card.
     * @param reserveNoble   Whether this development card allows you to reserve a noble.
     * @param prestigePoints The number of prestige points associated with this card.
     * @param costType       Whether this card has a cost of tokens or burning bonuses.
     * @param tokenCost      The token cost of this card
     * @param id             ID associated to this card.
     * @param cardTier       Tier of the development card (e.g. 1, 2, or 3).
     * @param isSatchel      Boolean on whether this card used to be a satchel.
     */
    public OrientDevelopmentCard(CardTier cardTier, TokenType tokenType, int bonus, CascadeType cascadeType, boolean reserveNoble,
                                 int prestigePoints, CostType costType, HashMap<TokenType, Integer> tokenCost, String id,
                                 boolean isSatchel) {
        super(cardTier, tokenType, bonus, prestigePoints, costType, tokenCost, id);
        this.reserveNoble = reserveNoble;
        this.cascadeType = cascadeType;
        this.isSatchel = isSatchel;
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
     * Return a bool depending on if the card used to be a satchel.
     *
     * @return true or false depending on if the card used to be a satchel.
     */
    public boolean isSatchel() {
        return this.isSatchel;
    }


}
