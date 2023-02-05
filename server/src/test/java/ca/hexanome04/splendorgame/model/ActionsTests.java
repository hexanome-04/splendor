package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.GameBaseOrient;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.actions.BuyCardAction;
import ca.hexanome04.splendorgame.model.action.actions.ChooseNobleAction;
import ca.hexanome04.splendorgame.model.action.actions.TakeTokenAction;
import ca.hexanome04.splendorgame.model.action.actions.ReserveCardAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

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
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

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
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);

        p1.addTokens(tokensToUse);

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players can buy a card using normal and gold tokens.")
    @Test
    void testPlayerBuyCard_UseGold() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Blue, 1);
        tokensToAdd.put(TokenType.Gold, 1);
        tokensToAdd.put(TokenType.Red, 2);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Blue, 1);
        tokensToUse.put(TokenType.Gold, 1);
        tokensToUse.put(TokenType.Red, 2);

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("02", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players can buy a card using bonuses.")
    @Test
    void testPlayerBuyCard_UseBonus() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 3);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Red, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 1);

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players can buy a card using bonuses and gold tokens.")
    @Test
    void testPlayerBuyCard_UseBonusAndGold() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 3);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Gold, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Gold, 1);

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players can buy a card using bonuses and double gold tokens.")
    @Test
    void testPlayerBuyCard_UseBonusAndDoubleGoldTokensOnly() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 2);
        p1.addBonus(TokenType.Gold, 2);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players can buy a card using bonuses and double gold tokens.")
    @Test
    void testPlayerBuyCard_DoubleGoldTokensOnly() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Gold, 4);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players cannot buy a card when providing zero tokens.")
    @Test
    void testPlayerBuyCard_ZeroTokensGiven() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(result).isEqualTo(ActionResult.INVALID_TOKENS_GIVEN);
    }

    @DisplayName("Ensure players can take two of same token in one turn.")
    @Test
    void testPlayerTakeTokens_ValidDoubleToken() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

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
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

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
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

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

    @DisplayName("Ensure players cannot have more than 10 tokens.")
    @Test
    void testPlayerTakeTokens_InvalidPlayerMaxTokens() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

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

    @DisplayName("Ensure players cannot take more than 2 of the same token per turn (without Trade Routes power).")
    @Test
    void testPlayerTakeTokens_InvalidThreeOfSameToken() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

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
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

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

    @DisplayName("Ensure players can reserve a card when they have not reached the limit.")
    @Test
    void testPlayerReserveCard_Valid() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        ActionResult result = game.takeAction(p1.getName(), new ReserveCardAction("01"));

        // make sure action is valid since player can afford it
        assertThat(p1.getTokens().get(TokenType.Gold)).isEqualTo(1);
        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players can reserve a card even when the board has no gold left.")
    @Test
    void testPlayerReserveCard_ValidNoGold() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> goldsToRemove = new HashMap<>();
        goldsToRemove.put(TokenType.Gold, 5);
        game.removeTokens(goldsToRemove);

        ActionResult result = game.takeAction(p1.getName(), new ReserveCardAction("01"));

        // make sure action is valid since player can afford it
        assertThat(p1.getTokens().get(TokenType.Gold)).isEqualTo(0);
        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

    @DisplayName("Ensure players cannot reserve a card when they have reached the limit.")
    @Test
    void testPlayerReserveCard_Invalid() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        RegDevelopmentCard c1 = (RegDevelopmentCard) game.getCardFromId("01");
        RegDevelopmentCard c2 = (RegDevelopmentCard) game.getCardFromId("02");
        RegDevelopmentCard c3 = (RegDevelopmentCard) game.getCardFromId("03");

        p1.reserveCard(c1);
        p1.reserveCard(c2);
        p1.reserveCard(c3);

        ActionResult result = game.takeAction(p1.getName(), new ReserveCardAction("04"));

        // make sure action is valid since player can afford it
        assertThat(p1.getTokens().get(TokenType.Gold)).isEqualTo(0);
        assertThat(result).isEqualTo(ActionResult.MAXIMUM_CARDS_RESERVED);
    }

    @DisplayName("Ensure ChooseNoble action is triggered when player qualifies for 2+ nobles.")
    @Test
    void testPlayerChooseNoble_QualifiesForTwo() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Blue, 1);
        p1.addBonus(TokenType.Green, 2);
        p1.addBonus(TokenType.Red, 2);
        p1.addBonus(TokenType.Brown, 1);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Blue, 1);
        tokensToAdd.put(TokenType.Red, 2);
        tokensToAdd.put(TokenType.Brown, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 1);
        tokensToTake.put(TokenType.Brown, 1);
        tokensToTake.put(TokenType.Red, 2);

        ActionResult result = game.takeAction(p1.getName(), new BuyCardAction("02", tokensToTake));

        // make sure action is valid since player can afford it
        assertThat(result).isEqualTo(ActionResult.MUST_CHOOSE_NOBLE);
    }

    @DisplayName("Ensure players can choose noble when prompted.")
    @Test
    void testPlayerChooseNoble_ValidChooseCard() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Blue, 1);
        p1.addBonus(TokenType.Green, 2);
        p1.addBonus(TokenType.Red, 2);
        p1.addBonus(TokenType.Brown, 1);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Blue, 1);
        tokensToAdd.put(TokenType.Red, 2);
        tokensToAdd.put(TokenType.Brown, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Brown, 1);

        NobleCard c1 = (NobleCard) game.getCardFromId("98");
        ArrayList<NobleCard> nobleCards = new ArrayList<>();
        nobleCards.add(c1);

        ActionResult result = game.takeAction(p1.getName(), new ChooseNobleAction("98"));

        // make sure action is valid since player can afford it
        assertThat(p1.getNobles()).isEqualTo(nobleCards);
        assertThat(result).isEqualTo(ActionResult.VALID_ACTION);
    }

}
