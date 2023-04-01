package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.DevelopmentCard;
import ca.hexanome04.splendorgame.model.OrientDevelopmentCard;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorException;
import ca.hexanome04.splendorgame.model.TokenType;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

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
        super(Actions.CHOOSE_SATCHEL_TOKEN);
        this.cardId = cardId;
        this.assignedTokenType = assignedTokenType;
    }

    /**
     * Construct a choose token action (to be filled with info from decoder).
     */
    public ChooseTokenTypeAction() {
        this("", null);
    }

    @Override
    protected List<ActionResult> run(Game game, Player p) {


        ArrayList<ActionResult> result = new ArrayList<>();

        DevelopmentCard devCard = p.getPurchasedDevelopmentCard(this.cardId);
        if (!(devCard instanceof OrientDevelopmentCard orientDevCard)) {
            throw new SplendorException("Card with id '" + this.cardId + "' does not exist is not of type Satchel.");
        }

        if (orientDevCard.getTokenType() != TokenType.Satchel) {
            throw new SplendorException("Card with id '" + this.cardId + "' is not of type Satchel.");
        }

        // player can only set satchel token type to another bonus they already have
        boolean valid = false;
        for (DevelopmentCard c : p.getDevCards()) {
            if (c.getTokenType() != null && c.getTokenType() != TokenType.Satchel
                    && c.getTokenType() != TokenType.Gold
                    && c.getTokenType() == this.assignedTokenType) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            throw new SplendorException("You must pair the satchel's token type to a card you already own.");
        }

        // The act of choosing a bonus type could make a player eligible for a noble/win condition... is this checked?
        if (p.getBonuses().get(assignedTokenType) > 0
                && assignedTokenType != TokenType.Gold && assignedTokenType != TokenType.Satchel) {
            orientDevCard.setTokenType(assignedTokenType);

            // i think the checks in this if are what i do above, whoops
            // add bonus to player
            p.addBonus(assignedTokenType, 1);
            result.add(ActionResult.TURN_COMPLETED);

        } else {
            result.add(ActionResult.INVALID_TOKEN_CHOSEN);
            return result;
        }

        if (game.getCurValidActions().contains(Actions.CHOOSE_SATCHEL_TOKEN)) {
            result.add(ActionResult.VALID_ACTION);
        }

        game.removeValidAction(Actions.CHOOSE_SATCHEL_TOKEN);

        return result;

    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.cardId = jobj.get("cardId").getAsString();

        this.assignedTokenType = TokenType.valueOf(jobj.get("selected").getAsString());

        return this;
    }
}