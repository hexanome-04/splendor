package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a Card object.
 */
public class Card {

    private final Integer prestigePoints;
    private final CostType costType;
    private final CardType cardType;
    private final ArrayList<Token> tokenCost;


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
    public Card(Integer prestigePoints, CostType costType, CardType cardType,
                ArrayList<Token> tokenCost) {
        this.prestigePoints = prestigePoints;
        this.costType = costType;
        this.cardType = cardType;
        this.tokenCost = tokenCost;
    }

    /**
     * Return the prestige point associated with the card instance.
     *
     * @return prestige point of the card
     */
    public int getPrestigePoints() {
        return prestigePoints;
    }

    /**
     * Return the cost type of the card instance.
     *
     * @return cost type of the card
     */
    public CostType getCostType() {
        return costType;
    }

    /**
     * Return the type of the card instance.
     *
     * @return the card type
     */
    public CardType getCardType() {
        return cardType;
    }

    /**
     * Return the cost of the card instance.
     *
     * @return cost of the card
     */
    public List<Token> getCost() {
        return Collections.unmodifiableList(tokenCost);
    }
}
