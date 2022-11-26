package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuyCardAction extends Action {

    private String buyCardID;
    private List<Token> selectedTokens;

    public BuyCardAction(String buyCardID, List<Token> selectedTokens) {
        super(Actions.BUY_CARD);
        this.buyCardID = buyCardID;
        this.selectedTokens = selectedTokens;
    }

    public BuyCardAction() {
        this("", new ArrayList<>());
    }


    @Override
    protected ActionResult run(SplendorGame game, Player player) {

        SplendorBoard board = game.getBoardState();

        // get card from board
        // should be a development card (hopefully)
        RegDevelopmentCard dc = (RegDevelopmentCard) board.getCardFromID(this.buyCardID);
        if(dc == null) {
            throw new RuntimeException("Card with id '" + this.buyCardID + "' does not exist.");
        }

        if (!dc.isPurchasable(player, selectedTokens)) {
            return ActionResult.INVALID_TOKENS_GIVEN;
        }

        // no error handling
        board.takeCard(dc);
        player.buyCard(dc); // will remove tokens from inventory here too

        // TODO: Put tokens into board


        return ActionResult.VALID_ACTION;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.buyCardID = jobj.get("cardId").getAsString();

        JsonArray arr = jobj.get("tokens").getAsJsonArray();
        for(JsonElement e : arr) {
            this.selectedTokens.add(new Token(TokenType.valueOf(e.getAsString())));
        }

        return this;
    }

}
