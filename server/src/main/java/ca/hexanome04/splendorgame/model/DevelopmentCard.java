package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;

/**
 * Abstract class that represents a development card.
 */
public abstract class DevelopmentCard extends Card implements CardType {
    private final TokenType tokenType;
    private final int bonus;

    /**
     * Creates a development card from the class in which this was called super from.
     *
     * @param tokenType      The type of token associated to this development card.
     * @param bonus          The integer token bonus associated to this development card.
     * @param prestigePoints Amount of prestige points associated to this card.
     * @param costType       Cost type associated to this card.
     * @param cardType       Card type associated to this card.
     * @param tokenCost      Token cost associated to this card.
     */
    public DevelopmentCard(TokenType tokenType, int bonus, int prestigePoints, CostType costType, CardType cardType,
                           ArrayList<TokenType> tokenCost) {
        super(prestigePoints, costType, cardType, tokenCost);
        this.tokenType = tokenType;
        this.bonus = bonus;
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
     * Gets the number of bonuses associated to this card.
     *
     * @return The integer value of bonus(es) for this card.
     */
    public int getBonus() {
        return bonus;
    }
}
