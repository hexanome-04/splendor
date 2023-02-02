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
    protected ActionResult run(Game game, Player player) {

        // get card from board
        // should be a development card (hopefully)
        DevelopmentCard dc = (DevelopmentCard) game.getCardFromId(this.buyCardId);
        if (dc == null) {
            throw new RuntimeException("Card with id '" + this.buyCardId + "' does not exist.");
        }

        if (!dc.isPurchasable(player, selectedTokens)) {
            return ActionResult.INVALID_TOKENS_GIVEN;
        }

        // no error handling
        ActionResult result = dc.buyCard(player, game, selectedTokens); // will remove tokens from inventory here too

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
