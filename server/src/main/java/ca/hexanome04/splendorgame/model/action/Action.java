package ca.hexanome04.splendorgame.model.action;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract action that can be performed on a game.
 */
public abstract class Action {

    private final Actions actionType;

    /**
     * Construct an action with the given action type.
     *
     * @param actionType action type
     */
    public Action(Actions actionType) {
        this.actionType = actionType; // just to force every child to use one enum
    }

    /**
     * Executes an action on the specified game, performed by the specified player.
     *
     * @param game game to execute action on
     * @param p player executing the action
     * @return information about execution of action
     */
    public final List<ActionResult> execute(Game game, Player p) {
        ArrayList<ActionResult> result = new ArrayList<ActionResult>();
        // Check if player can execute this action.
        if (!game.getTurnCurrentPlayer().getName().equals(p.getName())) {
            result.add(ActionResult.INVALID_PLAYER);
        }

        return this.run(game, p);
    }

    /**
     * Run the actual execution for the action (execute is for checks, which then uses this function).
     *
     * @param game game to execute action on
     * @param p player executing the action
     * @return information about execution of action
     */
    protected abstract List<ActionResult> run(Game game, Player p);

    /**
     * Decode the action with the given JSON object and return a proper instances of the action.
     *
     * @param jobj JSON object information for action
     * @return proper instance of action
     */
    public abstract Action decodeAction(JsonObject jobj);

}
