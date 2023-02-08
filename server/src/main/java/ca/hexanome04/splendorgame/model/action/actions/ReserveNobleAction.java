package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.JsonObject;

/**
 * Perform the reserve noble action.
 */
public class ReserveNobleAction extends Action {

    private String reserveNobleId;

    /**
     * Construct a reserve noble action.
     *
     * @param reserveNobleId card id to be reserved from game
     */
    public ReserveNobleAction(String reserveNobleId) {
        super(Actions.RESERVE_CARD);
        this.reserveNobleId = reserveNobleId;
    }

    /**
     * Construct a reserve noble action (to be filled with info from decoder).
     */
    public ReserveNobleAction() {
        this("");
    }

    @Override
    protected ActionResult run(Game game, Player player) {

        // get card from board
        NobleCard noble = (NobleCard) game.getCardFromId(this.reserveNobleId);
        if (noble == null) {
            throw new RuntimeException("Noble with id '" + this.reserveNobleId + "' does not exist.");
        }

        // no error handling
        game.takeCard(noble);
        player.reserveNoble(noble);

        return ActionResult.VALID_ACTION;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.reserveNobleId = jobj.get("cardId").getAsString();
        return this;

    }

}
