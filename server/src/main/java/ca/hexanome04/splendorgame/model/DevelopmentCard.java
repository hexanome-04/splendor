package ca.hexanome04.splendorgame.model;

import java.util.HashMap;

/**
 * Abstract class that represents a development card.
 */
public abstract class DevelopmentCard extends Card {

    private TokenType tokenType;
    private final int bonus;
    private final CardTier cardTier;

    /**
     * Creates a development card from the class in which this was called super from.
     *
     * @param tokenType      The type of token associated to this development card.
     * @param bonus          The integer token bonus associated to this development card.
     * @param prestigePoints Amount of prestige points associated to this card.
     * @param costType       Cost type associated to this card.
     * @param tokenCost      Token cost associated to this card.
     * @param id             ID associated to this card.
     * @param cardTier       Tier of the development card (e.g. 1, 2, or 3).
     */
    public DevelopmentCard(CardTier cardTier, TokenType tokenType, int bonus, int prestigePoints, CostType costType,
                           HashMap<TokenType, Integer> tokenCost, String id) {
        super(prestigePoints, costType, tokenCost, id);
        this.tokenType = tokenType;
        this.bonus = bonus;
        this.cardTier = cardTier;
    }

    /**
     * Returns the type of bonus associated with this card.
     *
     * @return The type of token.
     */
    public TokenType getTokenType() {
        return tokenType;
    }

    /**
     * Sets the token type of this card, but only if it's of type satchel to begin with.
     *
     * @param tokenType The type of token to be assigned to this card.
     */

    public void setTokenType(TokenType tokenType) {
        if (this.tokenType == TokenType.Satchel) {
            this.tokenType = tokenType;
        }
    }

    /**
     * Gets the number of bonuses associated to this card.
     *
     * @return The integer value of bonus(es) for this card.
     */
    public int getBonus() {
        return bonus;
    }

}
