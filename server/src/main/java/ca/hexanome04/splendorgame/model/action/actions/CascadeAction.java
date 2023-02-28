package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Perform the cascade action.
 */
public abstract class CascadeAction extends Action {

    private String cascadeCardId;

    /**
     * Construct a cascade action.
     *
     * @param buyCardId card id to be bought from game
     * @param tier the tier level of the cascade (1 or 2)
     */
    public CascadeAction(String buyCardId, Actions tier) {
        super(tier);
        this.cascadeCardId = buyCardId;
    }


    @Override
    protected abstract List<ActionResult> run(Game game, Player player);

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.cascadeCardId = jobj.get("cardId").getAsString();

        return this;
    }

}
