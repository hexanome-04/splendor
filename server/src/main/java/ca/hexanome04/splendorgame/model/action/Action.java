package ca.hexanome04.splendorgame.model.action;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorGame;
import com.google.gson.JsonObject;

public abstract class Action {

    private final Actions actionType;

    public Action(Actions actionType) {
        this.actionType = actionType; // just to force every child to use one enum
    }

    public final ActionResult execute(SplendorGame game, Player p) {
        // Check if player can execute this action.
        if (!game.getTurnCurrentPlayer().getName().equals(p.getName())) {
            return ActionResult.INVALID_PLAYER;
        }

        return this.run(game, p);
    }

    protected abstract ActionResult run(SplendorGame game, Player p);

    public abstract Action decodeAction(JsonObject jobj);

}
