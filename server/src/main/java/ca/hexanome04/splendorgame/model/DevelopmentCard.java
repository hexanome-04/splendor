package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.ActionResult;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class that represents a development card.
 */
public abstract class DevelopmentCard extends Card {

    private final TokenType tokenType;
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
     * Gets the number of bonuses associated to this card.
     *
     * @return The integer value of bonus(es) for this card.
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * Allows a player to buy this card.
     *
     * @param p Player buying this card
     * @param b splendor board
     * @param tokensToPayWith tokens the player has chosen to pay with
     * @return result of buy card action
     */
    public ActionResult buyCard(Player p, SplendorBoard b, HashMap<TokenType, Integer> tokensToPayWith) {

        ActionResult result = ActionResult.VALID_ACTION;

        // remove card cost (if token) from player
        if (this.getCostType() == CostType.Token) {
            // make hashmap with each TokenType set to 0 to initialize other hashmaps
            HashMap<TokenType, Integer> emptyHashmap = new HashMap<>();
            for (TokenType t : TokenType.values()) {
                emptyHashmap.put(t, 0);
            }

            // keep track of how many tokens still need to be paid
            HashMap<TokenType, Integer> tokensLeftToPay = this.getTokenCost();

            for (Map.Entry<TokenType, Integer> currTokenAndPrice : this.getTokenCost().entrySet()) {
                TokenType currentToken = currTokenAndPrice.getKey();

                if (this.getTokenCost().get(currentToken) == 0) {
                    continue;
                }

                // remove bonuses from cost (e.g. tokensLeftToPay -= bonus)
                tokensLeftToPay.put(currentToken, tokensLeftToPay.get(currentToken) - p.getBonuses().get(currentToken));

                int tokenToPayWithValue = tokensToPayWith.get(currentToken) == null ? 0 : tokensToPayWith.get(currentToken);
                // make sure player has enough tokens to pay
                int tokensUnderpaid = tokensLeftToPay.get(currentToken) - tokenToPayWithValue;
                if (tokensUnderpaid > 0) {
                    // check for gold if they didn't pay full price in normal tokens
                    if (tokensToPayWith.get(TokenType.Gold) != null
                            && tokensToPayWith.get(TokenType.Gold) >= tokensUnderpaid) {
                        tokensLeftToPay.put(currentToken, tokensLeftToPay.get(currentToken) - tokensUnderpaid);

                        HashMap<TokenType, Integer> tokensToRemoveFromPlayer = new HashMap<>(emptyHashmap);
                        tokensToRemoveFromPlayer.put(TokenType.Gold, tokensUnderpaid);

                        tokensToRemoveFromPlayer.put(currentToken, tokensLeftToPay.get(currentToken));
                        tokensLeftToPay.put(currentToken, tokensLeftToPay.get(currentToken)
                                - tokenToPayWithValue);

                        p.removeTokens(tokensToRemoveFromPlayer);
                    } else {
                        return ActionResult.INVALID_TOKENS_GIVEN;
                    }
                } else {
                    HashMap<TokenType, Integer> tokensToRemoveFromPlayer = new HashMap<>(emptyHashmap);
                    tokensToRemoveFromPlayer.put(currentToken, tokensLeftToPay.get(currentToken));

                    tokensLeftToPay.put(currentToken, tokensLeftToPay.get(currentToken)
                            - tokensToPayWith.get(currentToken));

                    p.removeTokens(tokensToRemoveFromPlayer);
                }
            }
        }

        // TODO: handle orient double gold token cards later
        if (this.getClass().equals(OrientDevelopmentCard.class)) {
            OrientDevelopmentCard orientCard = (OrientDevelopmentCard) this;
            if (orientCard.getCostType() == CostType.Bonus) {
                p.removeBonuses(orientCard.getBurnBonusCost());
            }
            if (orientCard.getReserveNoble()) {
                result = ActionResult.MUST_RESERVE_NOBLE;
            }
            if (orientCard.getCascadeType() != CascadeType.None) {
                result = ActionResult.MUST_CHOOSE_CASCADE_CARD;
            }
            if (orientCard.getTokenType() == TokenType.Satchel) {
                result = ActionResult.MUST_CHOOSE_TOKEN_TYPE;
            }
        }

        b.addTokens(this.getTokenCost());

        p.addCard(this);
        b.takeCard(this);

        p.addBonus(this.getTokenType(), this.getBonus());
        p.addPrestigePoints(this.getPrestigePoints());

        return result;

    }

}
