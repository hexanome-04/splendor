package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsPlayer;
import java.util.HashMap;
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

            // Subtract the bonuses a player has
            // Easier way than the method below this forEach block
            cost.forEach((key, value) -> {
                cost.put(key, value - bonuses.get(key));
            });

            // Subtract the tokens provided
            for (TokenType t : tokens.keySet()) {
                if (t == TokenType.Gold) {
                    continue;
                }
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

            if (tokens.get(TokenType.Gold) != null) {
                if (player instanceof TradingPostsPlayer p && p.goldTokenWorthTwoTokens.isUnlocked()) {
                    // keep track of the total amount of gold tokens left
                    int goldTokenTotal = tokens.get(TokenType.Gold);
                    for (HashMap.Entry<TokenType, Integer> entry : cost.entrySet()) {
                        if (goldTokenTotal > 0) {
                            int currCost = entry.getValue();
                            if (currCost > 0) {
                                int missing = (int) Math.ceil((double) currCost / 2);
                                if (missing <= goldTokenTotal) {
                                    goldTokenTotal -= missing;
                                    missingTokenCount -= currCost;
                                } else {
                                    // not enough gold tokens to cover costs
                                    // but subtract as much as possible from missing token count
                                    missingTokenCount -= goldTokenTotal * 2;
                                    goldTokenTotal = 0;
                                    break;
                                }
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    missingTokenCount -= tokens.get(TokenType.Gold);
                }
            }

            if (bonuses.get(TokenType.Gold) < missingTokenCount) {
                purchasable = false;

            } else {
                HashMap<TokenType, Integer> toRemove = new HashMap<>();

                if (missingTokenCount % 2 == 0) {
                    toRemove.put(TokenType.Gold, missingTokenCount);

                } else {
                    // This is when a player wastes a double gold token by only using one of them
                    toRemove.put(TokenType.Gold, missingTokenCount + 1);
                }

                player.removeBonuses(toRemove);

                // Remove double gold bonus cards from player's inventory if used
                for (int i = 0; i < Math.round((double) missingTokenCount / 2); i++) {
                    for (DevelopmentCard c : player.getDevCards()) {
                        if (c.getTokenType() == TokenType.Gold) {
                            player.removeCard(c);
                            break;
                        }
                    }
                }
            }

        } else if (this.costType == CostType.Bonus) {
            // TODO : Remove cards if their bonus is spent
            HashMap<TokenType, Integer> cost = new HashMap<>(this.tokenCost);
            HashMap<TokenType, Integer> bonusesToRemove = new HashMap<>(this.tokenCost);

            cost.forEach((key, value) -> {
                cost.put(key, value - bonuses.get(key));
            });

            // final check to ensure that we fulfilled the cost for the card, if passed then can purchase
            for (HashMap.Entry<TokenType, Integer> entry : cost.entrySet()) {
                if (entry.getValue() > 0) {
                    purchasable = false;
                    break;
                }
            }

            // Removes correct amount of cards that have their bonus match with the bonus cost
            // TODO: Currently does NOT make the best selection
            // (i.e. between a 2PP blue card and a 0PP blue card, it may take the 2PP one if it is found first)
            bonusesToRemove.forEach((key, value) -> {
                for (int i = 0; i < value; i++) {
                    for (DevelopmentCard c : player.getDevCards()) {
                        if (c.getTokenType() == key) {
                            player.removeCard(c);
                        }
                    }
                }
            });


        }

        return purchasable;
    }

}
