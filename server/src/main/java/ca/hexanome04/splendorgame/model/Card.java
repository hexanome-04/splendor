package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a Card object.
 */
public abstract class Card {

    private final String id;
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
     * @param id             ID Associated to this card.
     */
    public Card(int prestigePoints, CostType costType, HashMap<TokenType, Integer> tokenCost, String id) {
        this.prestigePoints = prestigePoints;
        this.costType = costType;
        this.tokenCost = tokenCost;
        this.id = id;
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
    public String getId() {
        return this.id;
    }

    /**
     * Check if this card can be purchased by the player.
     *
     * @param player player that wants to purchase this card
     * @param tokensToUse tokens requested to use
     * @return true if player has the funds (and/or bonuses) and no excess tokens given
     */
    public boolean isPurchasable(Player player, HashMap<TokenType, Integer> tokensToUse) {

        boolean purchasable = true;
        HashMap<TokenType, Integer> tokens = new HashMap<>(tokensToUse);
        Map<TokenType, Integer> bonuses = player.getBonuses();

        // ensure player has the tokens to use
        if (!player.hasTokens(tokensToUse)) {
            return false;
        }

        if (this.costType == CostType.Token) {
            // clone so we don't modify the card's data
            HashMap<TokenType, Integer> cost = new HashMap<>(this.tokenCost);

            // Subtract the bonuses a player
            // Easier way than the method below this forEach block
            cost.forEach((key, value) -> {
                cost.put(key, value - bonuses.get(key));
            });


            // Subtract the tokens provided
            for (TokenType t : tokens.keySet()) {
                int costAmt = cost.get(t);
                cost.put(t, costAmt - tokens.get(t));
            }

            int missingTokenCount = 0;
            // final check to ensure that we fulfilled the cost for the card accounting for gold tokens
            for (HashMap.Entry<TokenType, Integer> entry : cost.entrySet()) {
                if (entry.getValue() > 0) {
                    missingTokenCount += entry.getValue();
                }
            }

            if (tokens.get(TokenType.Gold) == null) {
                if (missingTokenCount > 0) {
                    purchasable = false;
                }
            } else {
                if (missingTokenCount > tokens.get(TokenType.Gold)) {
                    purchasable = false;
                }
            }

        } else if (this.costType == CostType.Bonus) {
            // TODO : does this mean the card is only purchasable with bonuses?

        }

        return purchasable;
    }

}
