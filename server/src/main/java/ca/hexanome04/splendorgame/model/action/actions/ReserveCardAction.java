package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.DevelopmentCard;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.TokenType;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Perform the reserve card action.
 */
public class ReserveCardAction extends Action {

    private String reserveCardId;

    /**
     * Construct a reserve card action.
     *
     * @param reserveCardId card id to be reserved from game
     */
    public ReserveCardAction(String reserveCardId) {
        super(Actions.RESERVE_CARD);
        this.reserveCardId = reserveCardId;
    }

    /**
     * Construct a reserve card action (to be filled with info from decoder).
     */
    public ReserveCardAction() {
        this("");
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {

        // clear list of current player valid actions
        game.clearValidActions();

        ArrayList<ActionResult> result = new ArrayList<>();

        // get card from board
        DevelopmentCard dc = (DevelopmentCard) game.getCardFromId(this.reserveCardId);
        if (dc == null) {
            throw new RuntimeException("Card with id '" + this.reserveCardId + "' does not exist.");
        }

        if (player.getReservedCards().size() >= 3) {
            result.add(ActionResult.MAXIMUM_CARDS_RESERVED);
            return result;
        }

        // no error handling
        game.takeCard(dc);
        player.reserveCard(dc);

        HashMap<TokenType, Integer> goldToken = new HashMap<>();
        goldToken.put(TokenType.Gold, 1);

        // removes one gold token from board (if there is a gold token to take)
        boolean removeTokensResults = game.removeTokens(goldToken);

        // give player gold token if there were any left
        if (removeTokensResults == true) {
            player.addTokens(goldToken);
        }

        if (game.getCurValidActions().size() == 0) {
            result.add(ActionResult.TURN_COMPLETED);
        }

        result.add(ActionResult.VALID_ACTION);

        return result;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        // if missing data, throw exception
        this.reserveCardId = jobj.get("cardId").getAsString();
        return this;

    }

}
