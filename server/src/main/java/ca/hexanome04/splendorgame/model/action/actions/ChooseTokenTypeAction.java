package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.OrientDevelopmentCard;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.TokenType;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.JsonObject;

/**
 * Perform the choose token type for satchel action.
 */

public class ChooseTokenTypeAction extends Action {

    private String cardId;
    private TokenType assignedTokenType;


    /**
     * Construct a choose token type action.
     *
     * @param cardId card id to have its token assigned
     * @param assignedTokenType token type to use for assignment
     */
    public ChooseTokenTypeAction(String cardId, TokenType assignedTokenType) {
        super(Actions.CHOOSE_TOKEN);
        this.cardId = cardId;
        this.assignedTokenType = assignedTokenType;
    }

    /**
     * Construct a buy card action (to be filled with info from decoder).
     */
    public ChooseTokenTypeAction() {
        this("", null);
    }

    @Override
    protected ActionResult run(Game game, Player p) {
        OrientDevelopmentCard odc = (OrientDevelopmentCard) game.getCardFromId(this.cardId);
        if (odc == null || odc.getTokenType() != TokenType.Satchel) {
            throw new RuntimeException("Card with id '" + this.cardId + "' does not exist or is not of type Satchel.");
        }

        if (p.getBonuses().get(assignedTokenType) > 0
                && assignedTokenType != TokenType.Gold && assignedTokenType != TokenType.Satchel) {
            odc.setTokenType(assignedTokenType);
            return ActionResult.VALID_ACTION;

        } else {
            return ActionResult.INVALID_TOKEN_CHOSEN;
        }

    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.cardId = jobj.get("cardId").getAsString();

        this.assignedTokenType = TokenType.valueOf(jobj.get("selected").getAsString());

        return null;
    }
}