package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
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
    public ActionResult executeAction(SplendorGame game, Player player) {

        SplendorBoard board = game.getBoardState();

        // get card from board
        // should be a development card (hopefully)
        RegDevelopmentCard dc = (RegDevelopmentCard) board.getCardFromID(this.buyCardID);

        if (!dc.isPurchasable(player, selectedTokens)) {
            return ActionResult.NOT_ENOUGH_TOKENS;
        }

        // no error handling
        board.takeCard(dc);
        player.buyCard(dc);

        return ActionResult.VALID_ACTION;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception

        return this;
    }

}
