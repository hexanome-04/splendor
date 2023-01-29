package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.NobleCard;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorBoard;
import ca.hexanome04.splendorgame.model.SplendorGame;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import com.google.gson.JsonObject;

/**
 * Perform the choose noble action (in case when player qualifies for >1 noble in one turn).
 */
public class ChooseNobleAction extends Action {

    private String nobleId;

    /**
     * Construct a choose noble action.
     *
     * @param nobleId id of chosen noble.
     */
    public ChooseNobleAction(String nobleId) {
        super(Actions.CHOOSE_NOBLE);
        this.nobleId = nobleId;
    }

    /**
     * Construct a choose noble action (to be filled with info from decoder).
     */
    public ChooseNobleAction() {
        this("");
    }

    @Override
    protected ActionResult run(SplendorGame game, Player p) {

        SplendorBoard board = game.getBoardState();

        NobleCard nc = (NobleCard) board.getCardFromId(nobleId);
        if (nc == null) {
            throw new RuntimeException("Noble card with id '" + this.nobleId + "' does not exist.");
        }

        // no error handling
        board.takeCard(nc);
        p.addNoble(nc);

        return ActionResult.VALID_ACTION;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.nobleId = jobj.get("nobleId").getAsString();

        return this;
    }
}
