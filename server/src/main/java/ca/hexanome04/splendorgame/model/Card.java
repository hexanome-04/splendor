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


            if (tokens.get(TokenType.Gold) != null) {
                if (player instanceof TradingPostsPlayer p && p.goldTokenWorthTwoTokens.isUnlocked()) {

                    for (int i = 0; i < tokens.get(TokenType.Gold); i++) {
                        for (TokenType type : cost.keySet()) {
                            if (cost.get(type) > 0) {
                                cost.put(type, cost.get(type) - 2);
                                break;
                            }
                        }
                    }


                } else {
                    for (int i = 0; i < tokens.get(TokenType.Gold); i++) {
                        for (TokenType type : cost.keySet()) {
                            if (cost.get(type) > 0) {
                                cost.put(type, cost.get(type) - 1);
                                break;
                            }
                        }
                    }
                }
            }

            int doubleGoldTokenAmt = player.getBonuses().get(TokenType.Gold);
            int doubleGoldTokensUsed = 0;
            // If a player is not a trading post player, or if he is, doesn't have it unlocked, enter if true branch
            if (!(player instanceof TradingPostsPlayer) || !((TradingPostsPlayer) player).goldTokenWorthTwoTokens.isUnlocked()) {
                for (int i = 0; i < doubleGoldTokenAmt; i++) {
                    for (TokenType type : cost.keySet()) {
                        if (cost.get(type) > 0) {
                            cost.put(type, cost.get(type) - 1);
                            doubleGoldTokensUsed++;
                            break;
                        }
                    }
                }



            // Otherwise, he is a trading post player AND he has it unlocked
            } else {
                // My logic is, for each individual gold bonus, remove 2 of a random color until it's zero or less
                for (int i = 0; i < doubleGoldTokenAmt; i++) {
                    for (TokenType type : cost.keySet()) {
                        if (cost.get(type) > 0) {
                            cost.put(type, cost.get(type) - 2);
                            doubleGoldTokensUsed++;
                            break;
                        }
                    }
                }

            }

            if (doubleGoldTokenAmt > 0) {
                for (TokenType type : cost.keySet()) {
                    if (cost.get(type) > 0) {
                        purchasable = false;
                    }
                }

                HashMap<TokenType, Integer> toRemove = new HashMap<>();

                // Getting the amount of double gold token cards to remove, rounding up if you wasted one by using half
                if (doubleGoldTokensUsed % 2 == 1) {
                    doubleGoldTokensUsed++;
                }
                toRemove.put(TokenType.Gold, doubleGoldTokensUsed);
                player.removeBonuses(toRemove);
                doubleGoldTokensUsed = doubleGoldTokensUsed / 2;

                for (int i = 0; i < doubleGoldTokensUsed; i++) {
                    for (DevelopmentCard c : player.getDevCards()) {
                        if (c.getTokenType() == TokenType.Gold) {
                            player.removeCard(c);
                            break;
                        }
                    }
                }
            }

            // Final check for any remaining tokens in cost, if there are, then it's not purchasable
            for (Integer value : cost.values()) {
                if (value > 0) {
                    purchasable = false;
                    break;
                }
            }


        } else if (this.costType == CostType.Bonus) {
            HashMap<TokenType, Integer> cost = new HashMap<>(this.tokenCost);
            cost.forEach((key, value) -> cost.put(key, value - bonuses.get(key)));

            // final check to ensure that we fulfilled the cost for the card, if passed then can purchase
            for (HashMap.Entry<TokenType, Integer> entry : cost.entrySet()) {
                if (entry.getValue() > 0) {
                    purchasable = false;
                    break;
                }
            }

        }

        return purchasable;
    }

}
