package ca.hexanome04.splendorgame.model.action;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorGame;

public abstract class Action {

    public abstract ActionResult executeAction(SplendorGame game, Player p);

    // TODO: decodeAction(actionName)

}
