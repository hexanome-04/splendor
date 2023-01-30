package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.actions.BuyCardAction;
import ca.hexanome04.splendorgame.model.action.actions.TakeTokenAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Tests for doing actions in a game.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ActionsTests {

    @DisplayName("Ensure players cannot buy a card with insufficient tokens in their inventory.")
    @Test
    void testPlayerBuyCard_InsufficientTokens() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 3);

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // should be no more tier 1 cards available to be purchased
        assertThat(result).isEqualTo(ActionResult.INVALID_TOKENS_GIVEN);
    }

    @DisplayName("Ensure players can buy a card when sufficient tokens provided.")
    @Test
    void testPlayerBuyCard_SufficientTokens() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);

        p1.addTokens(tokensToUse);

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players can take two of same token in one turn.")
    @Test
    void testPlayerTakeTokens_ValidDoubleToken() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 4);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 2);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();

        ActionResult result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players can take three unique tokens in one turn.")
    @Test
    void testPlayerTakeTokens_ValidThreeUniqueTokens() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 4);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 1);
        tokensToTake.put(TokenType.Green, 1);
        tokensToTake.put(TokenType.Brown, 1);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();

        ActionResult result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players can take and put back tokens, remaining under 10 tokens in inventory")
    @Test
    void testPlayerTakeTokens_ValidPlayerTakeAndPutBack() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 4);
        playerTokens.put(TokenType.Green, 2);
        playerTokens.put(TokenType.White, 3);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 1);
        tokensToTake.put(TokenType.Green, 1);
        tokensToTake.put(TokenType.Brown, 1);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();
        tokensToPutBack.put(TokenType.White, 1);
        tokensToPutBack.put(TokenType.Red, 1);


        ActionResult result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players cannot take more than 10 tokens.")
    @Test
    void testPlayerTakeTokens_InvalidPlayerMaxTokens() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 4);
        playerTokens.put(TokenType.Green, 2);
        playerTokens.put(TokenType.White, 3);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 1);
        tokensToTake.put(TokenType.Green, 1);
        tokensToTake.put(TokenType.Brown, 1);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();

        ActionResult result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(result).isEqualTo(ActionResult.MAXIMUM_TOKENS_IN_INVENTORY);
    }

    @DisplayName("Ensure players cannot take more than 2 of the same token per turn.")
    @Test
    void testPlayerTakeTokens_InvalidThreeOfSameToken() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 1);
        playerTokens.put(TokenType.Green, 2);
        playerTokens.put(TokenType.White, 1);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 3);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();

        ActionResult result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(result).isEqualTo(ActionResult.INVALID_TOKENS_GIVEN);
    }

    @DisplayName("Ensure players cannot take more than 3 unique tokens per turn.")
    @Test
    void testPlayerTakeTokens_InvalidFourUniqueTokens() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 1);
        playerTokens.put(TokenType.Green, 2);
        playerTokens.put(TokenType.White, 1);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 1);
        tokensToTake.put(TokenType.Green, 1);
        tokensToTake.put(TokenType.Red, 1);
        tokensToTake.put(TokenType.Brown, 1);


        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();

        ActionResult result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(result).isEqualTo(ActionResult.INVALID_TOKENS_GIVEN);
    }

}
