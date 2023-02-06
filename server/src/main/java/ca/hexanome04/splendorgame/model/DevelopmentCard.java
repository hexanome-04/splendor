package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.gameversions.Game;
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
     * @param g splendor game + board
     * @param tokensToPayWith tokens the player has chosen to pay with
     * @return result of buy card action
     */
    public ActionResult buyCard(Player p, Game g, HashMap<TokenType, Integer> tokensToPayWith) {

        ActionResult result = ActionResult.VALID_ACTION;

        if (!isPurchasable(p, tokensToPayWith)) {
            result = ActionResult.INVALID_TOKENS_GIVEN;
        }

        p.addCard(this);
        g.takeCard(this);

        p.addBonus(this.getTokenType(), this.getBonus());
        p.addPrestigePoints(this.getPrestigePoints());

        g.addTokens(this.getTokenCost());

        // TODO: handle orient double gold token cards later
        if (this.getClass().equals(OrientDevelopmentCard.class)) {
            OrientDevelopmentCard orientCard = (OrientDevelopmentCard) this;
            if (orientCard.getCostType() == CostType.Bonus) {
                p.removeBonuses(orientCard.getTokenCost());
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

        return result;

    }

}
