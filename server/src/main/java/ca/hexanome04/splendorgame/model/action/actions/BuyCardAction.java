package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;

public class BuyCardAction extends Action {

    private final String buyCardID;

    public BuyCardAction(String buyCardID) {
        this.buyCardID = buyCardID;
    }


    @Override
    public ActionResult executeAction(SplendorGame game, Player player) {

        SplendorBoard board = game.getBoardState();

        // get card from board
        // should be a development card (hopefully)
        RegDevelopmentCard dc = (RegDevelopmentCard) board.getCardFromID(this.buyCardID);

        // no error handling
        board.takeCard(dc);
        player.buyCard(dc);

        return ActionResult.VALID_ACTION;
    }

}
