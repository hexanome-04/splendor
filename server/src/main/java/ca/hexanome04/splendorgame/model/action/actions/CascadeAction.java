package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.JsonObject;

/**
 * Perform the cascade action.
 */
public class CascadeAction extends Action {

    private String cascadeCardId;

    /**
     * Construct a cascade action.
     *
     * @param buyCardId card id to be bought from game
     */
    public CascadeAction(String buyCardId) {
        super(Actions.CASCADE);
        this.cascadeCardId = buyCardId;
    }

    /**
     * Construct a cascade action (to be filled with info from decoder).
     */
    public CascadeAction() {
        this("");
    }

    @Override
    protected ActionResult run(Game game, Player player) {

        // get card from board
        // should be a development card (hopefully)
        DevelopmentCard dc = (DevelopmentCard) game.getCardFromId(this.cascadeCardId);
        if (dc == null) {
            throw new RuntimeException("Card with id '" + this.cascadeCardId + "' does not exist.");
        }

        player.addCard(dc);
        game.takeCard(dc);

        player.addBonus(dc.getTokenType(), dc.getBonus());
        player.addPrestigePoints(dc.getPrestigePoints());

        return ActionResult.VALID_ACTION;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.cascadeCardId = jobj.get("cardId").getAsString();

        return this;
    }

}
