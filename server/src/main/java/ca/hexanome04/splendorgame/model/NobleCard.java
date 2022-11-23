package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;

/**
 * Class representing the noble cards.
 */
public class NobleCard extends Card implements CardType {

    /**
     * Creates a new Noble card with provided prestige point amount, cost type and card type.
     *
     * @param prestigePoints Amount of prestige points associated to this card.
     * @param costType       Cost type associated to this card.
     * @param cardType       Card type associated to this card.
     * @param tokenCost      Token cost associated to this card.
     */
    public NobleCard(int prestigePoints, CostType costType, CardType cardType, ArrayList<TokenType> tokenCost) {
        super(prestigePoints, costType, cardType, tokenCost);
    }
}

