package ca.hexanome04.splendorgame.model.action.actions;

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
 * Perform power 1 after purchasing a development card.
 */
public class TakeExtraTokenAfterPurchasePowerAction extends Action {

    private TokenType takeToken;
    private TokenType putBackToken;

    /**
     * Construct a take token action for power 1.
     *
     * @param takeToken    token to be taken by player
     * @param putBackToken token to be put back by player
     */
    public TakeExtraTokenAfterPurchasePowerAction(
            TokenType takeToken, TokenType putBackToken) {
        super(Actions.TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER);
        this.takeToken = takeToken;
        this.putBackToken = putBackToken;
    }

    /**
     * Construct a take token action for power 1 (to be filled with info from decoder).
     */
    public TakeExtraTokenAfterPurchasePowerAction() {
        this(null, null);
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {

        ArrayList<ActionResult> result = new ArrayList<>();

        if (takeToken == putBackToken) {
            result.add(ActionResult.VALID_ACTION);
            result.add(ActionResult.TURN_COMPLETED);
            game.removeValidAction(Actions.TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER);
            return result;
        }

        int playerTotalTokens = 0;
        int takeTokenNum = this.takeToken == null ? 0 : 1;
        int putBackTokenNum = this.putBackToken == null ? 0 : 1;

        for (TokenType token : game.getTokens().keySet()) {
            // add to player token sum how many of this token the player will have after this turn
            int playerCurrentTokensNum = player.getTokens().get(token) == null ? 0 : player.getTokens().get(token);
            playerTotalTokens += playerCurrentTokensNum;
        }

        int tokenLeftOnBoard = game.getTokens().get(takeToken) - takeTokenNum;
        if (tokenLeftOnBoard < 0) {
            result.add(ActionResult.NOT_ENOUGH_TOKENS_ON_BOARD);
            return result;
        }

        playerTotalTokens += (takeTokenNum - putBackTokenNum);

        // check if player total token inventory goes over 10
        if (playerTotalTokens > 10) {
            result.add(ActionResult.MAXIMUM_TOKENS_IN_INVENTORY);
            return result;
        }

        // Create hashmaps for the game to add/remove tokens
        HashMap<TokenType, Integer> takeTokens = new HashMap<>();
        HashMap<TokenType, Integer> putBackTokens = new HashMap<>();
        if (takeToken != null) {
            takeTokens.put(takeToken, 1);
        }
        if (putBackToken != null) {
            putBackTokens.put(putBackToken, 1);
        }

        game.removeTokens(takeTokens);
        game.addTokens(putBackTokens);
        player.takeTokens(takeTokens, putBackTokens);

        result.add(ActionResult.VALID_ACTION);
        result.add(ActionResult.TURN_COMPLETED);
        game.removeValidAction(Actions.TAKE_EXTRA_TOKEN_AFTER_PURCHASE_POWER);
        return result;
    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        if (jobj.has("takeToken")) {
            this.takeToken = TokenType.valueOf(jobj.get("takeToken").getAsString());
        }
        if (jobj.has("putBackToken")) {
            this.putBackToken = TokenType.valueOf(jobj.get("putBackToken").getAsString());
        }

        return this;

    }

}
