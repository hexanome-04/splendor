package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsPlayer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.*;

/**
 * Perform the buy card action.
 */
public class BuyCardAction extends Action {

    private String buyCardId;
    private HashMap<TokenType, Integer> selectedTokens;

    /**
     * Construct a buy card action.
     *
     * @param buyCardId card id to be bought from game
     * @param selectedTokens tokens that are used to buy the card
     */
    public BuyCardAction(String buyCardId, HashMap<TokenType, Integer> selectedTokens) {
        super(Actions.BUY_CARD);
        this.buyCardId = buyCardId;
        this.selectedTokens = selectedTokens;
    }

    /**
     * Construct a buy card action (to be filled with info from decoder).
     */
    public BuyCardAction() {
        this("", new HashMap<>());
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {

        // get card from board
        // should be a development card (hopefully)
        DevelopmentCard dc = (DevelopmentCard) game.getCardFromId(this.buyCardId);

        // try reserved
        boolean wasReserved = false;
        if (dc == null) {
            dc = player.getReservedDevelopmentCard(this.buyCardId);
            wasReserved = dc != null;
        }

        if (dc == null) {
            throw new SplendorException("Card with id '" + this.buyCardId + "' does not exist.");
        }

        // for orient, can only buy satchel if you own another card with a bonus
        if (dc.getTokenType() == TokenType.Satchel) {
            boolean hasAnotherBonusCard = false;
            for (DevelopmentCard c : player.getDevCards()) {
                if (c.getTokenType() != null && c.getTokenType() != TokenType.Satchel
                        && c.getTokenType() != TokenType.Gold) {
                    // doesn't fail the checks above? should be good maybe
                    hasAnotherBonusCard = true;
                    break;
                }
            }
            if (!hasAnotherBonusCard) {
                throw new SplendorException("Cannot purchase card with satchel bonus "
                        + "without having another purchased card with a bonus.");
            }
        }


        ArrayList<ActionResult> result = new ArrayList<>();

        if (!dc.isPurchasable(player, selectedTokens)) {
            result.add(ActionResult.INVALID_TOKENS_GIVEN);
            return result;
        }

        player.addCard(dc);
        if (wasReserved) {
            player.removeReservedCard(dc);
        } else {
            game.takeCard(dc);
        }

        if (dc.getTokenType() != TokenType.Satchel) {
            player.addBonus(dc.getTokenType(), dc.getBonus());
        }
        player.addPrestigePoints(dc.getPrestigePoints());

        game.addTokens(selectedTokens);
        player.removeTokens(selectedTokens);

        if (dc.getClass().equals(OrientDevelopmentCard.class)) {
            OrientDevelopmentCard orientCard = (OrientDevelopmentCard) dc;
            if (orientCard.getCostType() == CostType.Bonus) {
                player.burnBonuses(orientCard.getTokenCost());
            }
            if (orientCard.getReserveNoble() && game instanceof OrientGame og && og.getNobles().size() > 0) {
                result.add(ActionResult.MUST_RESERVE_NOBLE);
            }
            if (orientCard.getCascadeType() == CascadeType.Tier1) {
                result.add(ActionResult.MUST_CHOOSE_CASCADE_CARD_TIER_1);

                // Differentiation between up and down needed? Unclear
            } else if (orientCard.getCascadeType() == CascadeType.Tier2) {
                result.add(ActionResult.MUST_CHOOSE_CASCADE_CARD_TIER_2);

            }
            if (orientCard.getTokenType() == TokenType.Satchel) {
                result.add(ActionResult.MUST_CHOOSE_TOKEN_TYPE);
            }
        }

        // Take extra token if player has unlocked power 1
        if (player instanceof TradingPostsPlayer tpp) {
            if (tpp.extraTokenAfterPurchase.isUnlocked()) {
                result.add(ActionResult.MUST_TAKE_EXTRA_TOKEN_AFTER_PURCHASE);
            }
        }

        if (result.size() == 0) {
            result.add(ActionResult.TURN_COMPLETED);
        }

        if (game.getCurValidActions().contains(Actions.BUY_CARD)) {
            result.add(ActionResult.VALID_ACTION);
        }

        // clear list of current player valid actions
        game.clearValidActions();

        return result;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.buyCardId = jobj.get("cardId").getAsString();

        JsonObject tokens = jobj.get("tokens").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : tokens.entrySet()) {
            TokenType type = TokenType.valueOf(entry.getKey());
            int amount = entry.getValue().getAsInt();

            selectedTokens.put(type, amount);
        }

        return this;
    }

}
