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
public class CascadeTier1Action extends CascadeAction {

    private String cascadeCardId;

    /**
     * Construct a cascade action.
     *
     * @param buyCardId card id to be bought from game
     */
    public CascadeTier1Action(String buyCardId) {
        super(buyCardId, Actions.CASCADE_1);
        this.cascadeCardId = buyCardId;
    }

    /**
     * Construct a cascade action (to be filled with info from decoder).
     */
    public CascadeTier1Action() {
        this("");
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {

        game.removeValidAction(Actions.CASCADE_1);

        ArrayList<ActionResult> result = new ArrayList<>();

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

        if (dc.getClass().equals(OrientDevelopmentCard.class)) {
            OrientDevelopmentCard orientCard = (OrientDevelopmentCard) dc;

            if (orientCard.getReserveNoble()) {
                result.add(ActionResult.MUST_RESERVE_NOBLE);
            }

            if (orientCard.getTokenType() == TokenType.Satchel) {
                result.add(ActionResult.MUST_CHOOSE_TOKEN_TYPE);
            }
        }

        if (game.getCurValidActions().size() == 0) {
            result.add(ActionResult.TURN_COMPLETED);
        }

        return result;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.cascadeCardId = jobj.get("cardId").getAsString();

        return this;
    }

}
