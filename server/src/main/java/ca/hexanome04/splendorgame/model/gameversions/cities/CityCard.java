package ca.hexanome04.splendorgame.model.gameversions.cities;

import ca.hexanome04.splendorgame.model.Card;
import ca.hexanome04.splendorgame.model.CostType;
import ca.hexanome04.splendorgame.model.TokenType;
import java.util.HashMap;

/**
 * Class representing the city cards.
 */
public class CityCard extends Card {

    private int numSameBonuses;

    /**
     * Creates a new City card with provided prestige point amount, cost type and card type.
     *
     * @param prestigePoints Amount of prestige points associated to this card.
     * @param tokenCost      Token cost associated to this card.
     * @param id             Id associated to this card.
     * @param numSameBonuses Number of bonuses of the same type player must have to qualify for card.
     */
    public CityCard(int prestigePoints, HashMap<TokenType, Integer> tokenCost, String id, int numSameBonuses) {
        super(prestigePoints, CostType.Bonus, tokenCost, id);
        this.numSameBonuses = numSameBonuses;
    }

    /**
     * Returns the number of bonuses of the same type needed to get this card.
     *
     * @return number of bonuses of same type needed to get this card
     */
    public int getNumSameBonuses() {
        return numSameBonuses;
    }

}

