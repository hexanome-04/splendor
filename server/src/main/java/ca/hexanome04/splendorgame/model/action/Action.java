package ca.hexanome04.splendorgame.model.action;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorGame;
import com.google.gson.JsonObject;

public abstract class Action {

    private final Actions actionType;

    public Action(Actions actionType) {
        this.actionType = actionType; // just to force every child to use one enum
    }

    public abstract ActionResult executeAction(SplendorGame game, Player p);

    public abstract Action decodeAction(JsonObject jobj);

}
