package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.RegDevelopmentCard;
import ca.hexanome04.splendorgame.model.SplendorBoard;
import ca.hexanome04.splendorgame.model.SplendorGame;
import ca.hexanome04.splendorgame.model.TokenType;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;

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
    protected ActionResult run(SplendorGame game, Player player) {

        SplendorBoard board = game.getBoardState();

        // get card from board
        // should be a development card (hopefully)
        RegDevelopmentCard dc = (RegDevelopmentCard) board.getCardFromId(this.buyCardId);
        if (dc == null) {
            throw new RuntimeException("Card with id '" + this.buyCardId + "' does not exist.");
        }

        if (!dc.isPurchasable(player, selectedTokens)) {
            return ActionResult.INVALID_TOKENS_GIVEN;
        }

        // no error handling
        board.takeCard(dc);
        player.buyCard(dc); // will remove tokens from inventory here too

        // TODO: Put tokens into board
        board.addTokens(dc.getTokenCost());

        return ActionResult.VALID_ACTION;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.buyCardId = jobj.get("cardId").getAsString();

        JsonArray arr = jobj.get("tokens").getAsJsonArray();
        for (JsonElement e : arr) {
            TokenType type = TokenType.valueOf(e.getAsString());
            selectedTokens.put(type, selectedTokens.get(type) + 1);
            //this.selectedTokens.add(new Token(TokenType.valueOf(e.getAsString())));
        }

        return this;
    }

}
