package ca.hexanome04.splendorgame.model.action.actions;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.TokenType;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsPlayer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perform the take token action.
 */
public class TakeTokenAction extends Action {

    private HashMap<TokenType, Integer> takeTokens;
    private HashMap<TokenType, Integer> putBackTokens;

    Logger logger = LoggerFactory.getLogger(TakeTokenAction.class);

    /**
     * Construct a take token action.
     *
     * @param takeTokens    tokens to be taken by player
     * @param putBackTokens tokens to be put back by player
     */
    public TakeTokenAction(HashMap<TokenType, Integer> takeTokens, HashMap<TokenType, Integer> putBackTokens) {
        super(Actions.TAKE_TOKEN);
        this.takeTokens = takeTokens;
        this.putBackTokens = putBackTokens;
    }

    /**
     * Construct a take token action (to be filled with info from decoder).
     */
    public TakeTokenAction() {
        this(new HashMap<>(), new HashMap<>());
    }

    @Override
    protected List<ActionResult> run(Game game, Player player) {

        ArrayList<ActionResult> result = new ArrayList<>();

        int playerTotalTokens = 0;

        for (TokenType token : game.getTokens().keySet()) {
            int takeTokensNum = takeTokens.get(token) == null ? 0 : takeTokens.get(token);
            int putBackTokensNum = putBackTokens.get(token) == null ? 0 : putBackTokens.get(token);

            // add to player token sum how many of this token the player will have after this turn
            int playerCurrentTokensNum = player.getTokens().get(token) == null ? 0 : player.getTokens().get(token);
            playerTotalTokens += playerCurrentTokensNum + takeTokensNum;
            playerTotalTokens -= putBackTokensNum;

            // check if any of this token type is being taken or put back
            if (takeTokensNum == 0 && putBackTokensNum == 0) {
                continue;
            }

            int tokensLeftOnBoard = game.getTokens().get(token) - takeTokensNum + putBackTokensNum;
            if (tokensLeftOnBoard < 0) {
                result.add(ActionResult.NOT_ENOUGH_TOKENS_ON_BOARD);
                return result;
            }

            if (putBackTokensNum > playerCurrentTokensNum) {
                result.add(ActionResult.NOT_ENOUGH_TOKENS_IN_INVENTORY);
                return result;
            }

        }

        // check if player total token inventory goes over 10
        if (playerTotalTokens > 10) {
            result.add(ActionResult.MAXIMUM_TOKENS_IN_INVENTORY);
            return result;
        }

        // cannot have doubleTokens > 0 and uniqueTokens > 0 at same time (mutually exclusive options)
        int uniqueTokens = 0; // max value of 3
        int doubleTokens = 0; // max value of 1
        for (TokenType t : takeTokens.keySet()) {
            // invalid if player tries to take >2 of one token type (unless trade routes power unlocked)
            if (takeTokens.get(t) > 2) {
                result.add(ActionResult.INVALID_TOKENS_GIVEN);
                return result;
            } else if (takeTokens.get(t) == 2) {
                doubleTokens += 1;

                // board pile must have at least 4 of this token for player to take two
                if (game.getTokens().get(t) < 4) {
                    result.add(ActionResult.INVALID_TOKENS_GIVEN);
                    return result;
                }
            } else {
                if (takeTokens.get(t) > 0) {
                    uniqueTokens++;
                }
            }
        }

        // verify either 2 of same token or 3 unique tokens
        // Unless for Trading routes, if player has power 2, can take 1 extra token of different color than doubleToken
        if (player instanceof TradingPostsPlayer tpp && tpp.extraTokenAfterTakingSameColor.isUnlocked()) {
            if (doubleTokens > 1 || uniqueTokens > 3 || (uniqueTokens > 1 && doubleTokens == 1)) {
                result.add(ActionResult.INVALID_TOKENS_GIVEN);
                return result;
            }
        } else if (doubleTokens > 1 || uniqueTokens > 3 || (uniqueTokens > 0 && doubleTokens > 0)) {
            result.add(ActionResult.INVALID_TOKENS_GIVEN);
            return result;
        }

        game.removeTokens(takeTokens);
        game.addTokens(putBackTokens);
        player.takeTokens(takeTokens, putBackTokens);

        if (game.getCurValidActions().contains(Actions.TAKE_TOKEN)) {
            result.add(ActionResult.VALID_ACTION);
        }
        result.add(ActionResult.TURN_COMPLETED);

        // clear list of current player valid actions
        game.clearValidActions();

        return result;

    }

    @Override
    public Action decodeAction(JsonObject jobj) {

        JsonObject takeTokensObj = jobj.get("takeTokens").getAsJsonObject();
        JsonObject putBackTokensObj = jobj.get("putBackTokens").getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : takeTokensObj.entrySet()) {
            TokenType type = TokenType.valueOf(entry.getKey());
            int amount = entry.getValue().getAsInt();

            takeTokens.put(type, amount);
        }

        for (Map.Entry<String, JsonElement> entry : putBackTokensObj.entrySet()) {
            TokenType type = TokenType.valueOf(entry.getKey());
            int amount = entry.getValue().getAsInt();

            putBackTokens.put(type, amount);
        }

        return this;

    }

}
