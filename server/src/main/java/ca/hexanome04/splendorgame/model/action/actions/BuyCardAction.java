package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorGame;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;

public class BuyCardAction extends Action {

    private final String buyCardID;

    public BuyCardAction(String buyCardID) {
        this.buyCardID = buyCardID;
    }


    @Override
    public ActionResult executeAction(SplendorGame game, Player player) {


        return null;
    }

}
