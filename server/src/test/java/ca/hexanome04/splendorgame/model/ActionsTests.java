package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.actions.*;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.action.ActionResult;

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
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 3);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // should be no more tier 1 cards available to be purchased
        assertThat(ActionResult.INVALID_TOKENS_GIVEN).isIn(result);
    }

    @DisplayName("Ensure players can buy a card when sufficient tokens provided.")
    @Test
    void testPlayerBuyCard_SufficientTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);

        p1.addTokens(tokensToUse);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using normal and gold tokens.")
    @Test
    void testPlayerBuyCard_UseGold() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

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

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("02", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses.")
    @Test
    void testPlayerBuyCard_UseBonus() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 3);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Red, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 1);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses and gold tokens.")
    @Test
    void testPlayerBuyCard_UseBonusAndGold() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 3);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Gold, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Gold, 1);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses and double gold tokens.")
    @Test
    void testPlayerBuyCard_UseBonusAndDoubleGoldTokensOnly() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Red, 2);
        p1.addBonus(TokenType.Gold, 2);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can buy a card using bonuses and double gold tokens.")
    @Test
    void testPlayerBuyCard_DoubleGoldTokensOnly() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Gold, 4);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players cannot buy a card when providing zero tokens.")
    @Test
    void testPlayerBuyCard_ZeroTokensGiven() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.INVALID_TOKENS_GIVEN).isIn(result);
    }

    @DisplayName("Ensure players can take two of same token in one turn.")
    @Test
    void testPlayerTakeTokens_ValidDoubleToken() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> playerTokens = new HashMap<>();
        playerTokens.put(TokenType.Red, 4);
        p1.addTokens(playerTokens);

        HashMap<TokenType, Integer> tokensToTake = new HashMap<>();
        tokensToTake.put(TokenType.Blue, 2);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can take three unique tokens in one turn.")
    @Test
    void testPlayerTakeTokens_ValidThreeUniqueTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

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

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can take and put back tokens, remaining under 10 tokens in inventory")
    @Test
    void testPlayerTakeTokens_ValidPlayerTakeAndPutBack() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

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


        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players cannot have more than 10 tokens.")
    @Test
    void testPlayerTakeTokens_InvalidPlayerMaxTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

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

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(ActionResult.MAXIMUM_TOKENS_IN_INVENTORY).isIn(result);
    }

    @DisplayName("Ensure players cannot take more than 2 of the same token per turn (without Trade Routes power).")
    @Test
    void testPlayerTakeTokens_InvalidThreeOfSameToken() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

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

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(ActionResult.INVALID_TOKENS_GIVEN).isIn(result);
    }

    @DisplayName("Ensure players cannot take more than 3 unique tokens per turn.")
    @Test
    void testPlayerTakeTokens_InvalidFourUniqueTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

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

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToTake, tokensToPutBack));

        assertThat(ActionResult.INVALID_TOKENS_GIVEN).isIn(result);
    }

    @DisplayName("Ensure players can reserve a card when they have not reached the limit.")
    @Test
    void testPlayerReserveCard_Valid() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new ReserveCardAction("01"));

        // make sure action is valid since player can afford it
        assertThat(p1.getTokens().get(TokenType.Gold)).isEqualTo(1);
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can reserve a card even when the board has no gold left.")
    @Test
    void testPlayerReserveCard_ValidNoGold() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> goldsToRemove = new HashMap<>();
        goldsToRemove.put(TokenType.Gold, 5);
        game.removeTokens(goldsToRemove);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new ReserveCardAction("01"));

        // make sure action is valid since player can afford it
        assertThat(p1.getTokens().get(TokenType.Gold)).isEqualTo(0);
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players cannot reserve a card when they have reached the limit.")
    @Test
    void testPlayerReserveCard_Invalid() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        RegDevelopmentCard c1 = (RegDevelopmentCard) game.getCardFromId("01");
        RegDevelopmentCard c2 = (RegDevelopmentCard) game.getCardFromId("02");
        RegDevelopmentCard c3 = (RegDevelopmentCard) game.getCardFromId("03");

        p1.reserveCard(c1);
        p1.reserveCard(c2);
        p1.reserveCard(c3);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new ReserveCardAction("04"));

        // make sure action is valid since player can afford it
        assertThat(p1.getTokens().get(TokenType.Gold)).isEqualTo(0);
        assertThat(ActionResult.MAXIMUM_CARDS_RESERVED).isIn(result);
    }

    @DisplayName("Ensure ChooseNoble action is triggered when player qualifies for 2+ nobles.")
    @Test
    void testPlayerChooseNoble_QualifiesForTwo() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

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

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("02", tokensToTake));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.MUST_CHOOSE_NOBLE).isIn(result);
    }

    @DisplayName("Ensure players can choose noble when prompted.")
    @Test
    void testPlayerChooseNoble_ValidChooseCard() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.addBonus(TokenType.Blue, 2);
        p1.addBonus(TokenType.Green, 2);
        p1.addBonus(TokenType.Red, 1);
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

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new ChooseNobleAction("98"));

        // make sure action is valid since player can afford it
        assertThat(p1.getNobles()).isEqualTo(nobleCards);
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can burn a double bonus card when burning bonuses to buy a card.")
    @Test
    void testBurnDoubleBonusCardForOrientCardPurchase() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 2);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        Player p2 = game.getPlayerFromName("Player2");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 2);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> p2Tokens = new HashMap<>();
        p2Tokens.put(TokenType.Blue, 2);

        game.takeAction(p1.getName(), new BuyCardAction("07", tokensToAdd));

        game.takeAction(p2.getName(), new TakeTokenAction(p2Tokens, new HashMap<>()));

        Card expectedCard = game.getCardFromId("10");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("10", new HashMap<>()));

        HashMap<TokenType, Integer> expectedBonuses = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            expectedBonuses.put(type, 0);
        }
        expectedBonuses.put(TokenType.Brown, 1);

        // make sure action is valid since player can afford it
        assertThat(p1.getDevCards().size()).isEqualTo(1);
        assertThat(p1.getDevCards().get(0)).isEqualTo(expectedCard);
        assertThat(p1.getBonuses()).isEqualTo(expectedBonuses);
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can burn a bonus card and the satchel card takes priority.")
    @Test
    void testBurnDoubleBonusCardSatchelPriority() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 2);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        Player p2 = game.getPlayerFromName("Player2");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 2);
        tokensToAdd.put(TokenType.Blue, 2);
        tokensToAdd.put(TokenType.Red, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> add = new HashMap<>();
        add.put(TokenType.Green, 1);
        p1.addTokens(add);
        p1.addTokens(add);

        HashMap<TokenType, Integer> p2Tokens = new HashMap<>();
        p2Tokens.put(TokenType.Blue, 1);

        game.takeAction(p1.getName(), new BuyCardAction("03", tokensToAdd));

        game.takeAction(p2.getName(), new TakeTokenAction(p2Tokens, new HashMap<>()));

        game.takeAction(p1.getName(), new BuyCardAction("11", add));
        game.takeAction(p1.getName(), new ChooseTokenTypeAction("11", TokenType.Red));

        game.takeAction(p2.getName(), new TakeTokenAction(p2Tokens, new HashMap<>()));

        Card expectedCard1 = game.getCardFromId("07");
        game.takeAction(p1.getName(), new BuyCardAction("07", add));

        game.takeAction(p2.getName(), new TakeTokenAction(p2Tokens, new HashMap<>()));

        Card expectedCard2 = game.getCardFromId("10");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("10", new HashMap<>()));

        HashMap<TokenType, Integer> expectedBonuses = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            expectedBonuses.put(type, 0);
        }
        expectedBonuses.put(TokenType.Red, 2);
        expectedBonuses.put(TokenType.Brown, 1);

        assertThat(p1.getBonuses()).isEqualTo(expectedBonuses);
        assertThat(expectedCard1).isIn(p1.getDevCards());
        assertThat(expectedCard2).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure players can burn a bonus card and lowest prestige point cards take priority.")
    @Test
    void testBurnDoubleBonusCardOptimalChoice() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 2);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        Player p2 = game.getPlayerFromName("Player2");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 2);
        tokensToAdd.put(TokenType.Blue, 2);
        tokensToAdd.put(TokenType.Red, 1);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> add = new HashMap<>();
        add.put(TokenType.Green, 1);
        p1.addTokens(add);
        p1.addTokens(add);

        HashMap<TokenType, Integer> p2Tokens = new HashMap<>();
        p2Tokens.put(TokenType.Blue, 1);

        Card expectedCard1 = game.getCardFromId("03");
        game.takeAction(p1.getName(), new BuyCardAction("03", tokensToAdd));

        game.takeAction(p2.getName(), new TakeTokenAction(p2Tokens, new HashMap<>()));

        game.takeAction(p1.getName(), new BuyCardAction("12", add));

        game.takeAction(p2.getName(), new TakeTokenAction(p2Tokens, new HashMap<>()));

        game.takeAction(p1.getName(), new BuyCardAction("13", add));

        game.takeAction(p2.getName(), new TakeTokenAction(p2Tokens, new HashMap<>()));

        Card expectedCard2 = game.getCardFromId("10");
        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("10", new HashMap<>()));

        HashMap<TokenType, Integer> expectedBonuses = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            expectedBonuses.put(type, 0);
        }
        expectedBonuses.put(TokenType.Red, 1);
        expectedBonuses.put(TokenType.Brown, 1);

        assertThat(p1.getBonuses()).isEqualTo(expectedBonuses);
        assertThat(p1.getPrestigePoints()).isEqualTo(3);
        assertThat(expectedCard1).isIn(p1.getDevCards());
        assertThat(expectedCard2).isIn(p1.getDevCards());
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure end of game is marked at end of round when player reaches 15 prestige points.")
    @Test
    void testEndOfRound_OneWinner() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 2);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        Player p2 = game.getPlayerFromName("Player2");
        p1.addPrestigePoints(14);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);
        p1.addTokens(tokensToUse);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().size()).isEqualTo(1);
        assertThat(game.getWinner().get(0)).isEqualTo(p1);
        assertThat(game.isGameOver()).isTrue();
    }

    @DisplayName("Ensure end of game is marked at end of round when >1 player reaches 15 prestige points.")
    @Test
    void testEndOfRound_TwoPotentialWinners() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 2);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        p1.addPrestigePoints(14);
        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);
        p1.addTokens(tokensToUse);

        Player p2 = game.getPlayerFromName("Player2");
        p2.addPrestigePoints(17);

        ArrayList<ActionResult> result1 = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result1);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().size()).isEqualTo(1);
        assertThat(game.getWinner().get(0)).isEqualTo(p2);
        assertThat(game.isGameOver()).isTrue();
    }

    @DisplayName("Ensure end of game is marked at end of round when a tie occurs.")
    @Test
    void testEndOfRound_Tie() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 2);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        p1.addPrestigePoints(15);

        Player p2 = game.getPlayerFromName("Player2");
        p2.addPrestigePoints(15);

        ArrayList<ActionResult> result1 = game.takeAction(p1.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result1);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().contains(p1)).isTrue();
        assertThat(game.getWinner().contains(p2)).isTrue();
        assertThat(game.getWinner().size()).isEqualTo(2);
        assertThat(game.isGameOver()).isTrue();
    }

    @DisplayName("Ensure end of game is marked at end of round when a tie occurs.")
    @Test
    void testEndOfRound_ThreeWayTie() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 3);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        p1.addPrestigePoints(15);

        Player p2 = game.getPlayerFromName("Player2");
        p2.addPrestigePoints(15);

        Player p3 = game.getPlayerFromName("Player3");
        p3.addPrestigePoints(15);

        ArrayList<ActionResult> result1 = game.takeAction(p1.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));
        ArrayList<ActionResult> result3 = game.takeAction(p3.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result1);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(ActionResult.VALID_ACTION).isIn(result3);
        assertThat(game.getWinner().contains(p1)).isTrue();
        assertThat(game.getWinner().contains(p2)).isTrue();
        assertThat(game.getWinner().contains(p3)).isTrue();
        assertThat(game.getWinner().size()).isEqualTo(3);
        assertThat(game.isGameOver()).isTrue();
    }

    @DisplayName("Ensure end of game is NOT marked at end of round where no player has 15 prestige points.")
    @Test
    void testEndOfRound_NoWinner() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGame(15, 2);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        Player p2 = game.getPlayerFromName("Player2");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);
        p1.addTokens(tokensToUse);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().size()).isEqualTo(0);
        assertThat(game.isGameOver()).isFalse();
    }

}
