package ca.hexanome04.splendorgame.model;

/**
 * Class representing a Card object.
 */
public class Card {

    private final Integer prestigePoints;
    private final CostType costType;
    private final CardType cardType;


    // maybe attribute for the actual image of the card to be added?
    // add an attribute for a list of tokens representing the token/bonus cost for this card

    /**
     * Creates a new card with provided prestige point amount, cost type and card type.
     *
     * @param prestigePoints Amount of prestige points associated to this card.
     * @param costType       Cost type associated to this card.
     * @param cardType       Card type associated to this card.
     */
    public Card(Integer prestigePoints, CostType costType, CardType cardType) {
        this.prestigePoints = prestigePoints;
        this.costType = costType;
        this.cardType = cardType;
    }
}
