package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
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
        if (dc == null) {
            throw new RuntimeException("Card with id '" + this.buyCardId + "' does not exist.");
        }

        // for orient, can only buy satchel if you own another card with a bonus
        if (dc.getTokenType() == TokenType.Satchel) {
            boolean hasAnotherBonusCard = false;
            for (DevelopmentCard c : player.getCards()) {
                if (c.getTokenType() != null && c.getTokenType() != TokenType.Satchel
                        && c.getTokenType() != TokenType.Gold) {
                    // doesn't fail the checks above? should be good maybe
                    hasAnotherBonusCard = true;
                    break;
                }
            }
            if (!hasAnotherBonusCard) {
                throw new RuntimeException("Cannot purchase card with satchel bonus "
                        + "without having another purchased card with a bonus.");
            }
        }


        List<ActionResult> result = dc.buyCard(player, game, selectedTokens);

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
