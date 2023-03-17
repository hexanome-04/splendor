package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.actions.*;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsGame;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsPlayer;
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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

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

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new ChooseNobleAction("98"));

        // make sure action is valid since player can afford it
        assertThat(p1.getNobles()).isEqualTo(nobleCards);
        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure end of game is marked at end of round when player reaches 15 prestige points.")
    @Test
    void testEndOfRound_OneWinner() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 1);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        p1.addPrestigePoints(14);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);
        p1.addTokens(tokensToUse);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(game.getWinner()).isEqualTo(p1);
        assertThat(game.isGameOver()).isTrue();
    }

    @DisplayName("Ensure end of game is marked at end of round when >1 player reaches 15 prestige points.")
    @Test
    void testEndOfRound_TwoWinners() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 2);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        p1.addPrestigePoints(14);
        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);
        p1.addTokens(tokensToUse);

        Player p2 = game.getPlayerFromName("Player2");
        p2.addPrestigePoints(15);

        ArrayList<ActionResult> result1 = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result1);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner()).isEqualTo(p2);
        assertThat(game.isGameOver()).isTrue();
    }

    @DisplayName("Ensure end of game is NOT marked at end of round where no player has 15 prestige points.")
    @Test
    void testEndOfRound_NoWinner() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 1);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);
        p1.addTokens(tokensToUse);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(game.getWinner()).isNull();
        assertThat(game.isGameOver()).isFalse();
    }

    @DisplayName("Ensure power 1 makes you choose an extra token after card purchase")
    @Test
    void testPlayerGainExtraTokenAfterCardPurchasePower1() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 4);

        // get first player (name = "Player1")
        TradingPostsPlayer p1 = (TradingPostsPlayer) game.getPlayerFromName("Player1");

        p1.extraTokenAfterPurchase.unlockPower(p1);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 1);
        p1.addTokens(tokensToAdd);

        game.takeAction(p1.getName(), new BuyCardAction("05", tokensToAdd));
        game.takeAction(p1.getName(), new TakeExtraTokenAfterPurchasePowerAction(TokenType.Green, null));

        tokensToAdd.put(TokenType.Red, 0);
        tokensToAdd.put(TokenType.Blue, 0);
        tokensToAdd.put(TokenType.Brown, 0);
        tokensToAdd.put(TokenType.Gold, 0);
        tokensToAdd.put(TokenType.Satchel, 0);
        tokensToAdd.put(TokenType.White, 0);

        assertThat(p1.getTokens()).isEqualTo(tokensToAdd);

    }

    @DisplayName("Ensure power 2 makes you choose an extra token after picking 2 of the same colour")
    @Test
    void testPlayerGainExtraTokenPower2() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 4);

        // get first player (name = "Player1")
        TradingPostsPlayer p1 = (TradingPostsPlayer) game.getPlayerFromName("Player1");

        p1.extraTokenAfterTakingSameColor.unlockPower(p1);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 2);
        tokensToAdd.put(TokenType.Red, 1);


        game.takeAction(p1.getName(), new TakeTokenAction(tokensToAdd, new HashMap<>()));

        tokensToAdd.put(TokenType.Blue, 0);
        tokensToAdd.put(TokenType.Brown, 0);
        tokensToAdd.put(TokenType.Gold, 0);
        tokensToAdd.put(TokenType.Satchel, 0);
        tokensToAdd.put(TokenType.White, 0);

        assertThat(p1.getTokens()).isEqualTo(tokensToAdd);

    }

    @DisplayName("Ensure you can have unlocked Power 2 but not necessarily use it")
    @Test
    void testPlayerUnlockedPower2ButDoesNotUse() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 4);

        // get first player (name = "Player1")
        TradingPostsPlayer p1 = (TradingPostsPlayer) game.getPlayerFromName("Player1");

        p1.extraTokenAfterTakingSameColor.unlockPower(p1);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 2);


        game.takeAction(p1.getName(), new TakeTokenAction(tokensToAdd, new HashMap<>()));

        tokensToAdd.put(TokenType.Red, 0);
        tokensToAdd.put(TokenType.Blue, 0);
        tokensToAdd.put(TokenType.Brown, 0);
        tokensToAdd.put(TokenType.Gold, 0);
        tokensToAdd.put(TokenType.Satchel, 0);
        tokensToAdd.put(TokenType.White, 0);

        assertThat(p1.getTokens()).isEqualTo(tokensToAdd);
    }

    @DisplayName("Ensure you cannot take an extra token with power 2 if there are not enough tokens left")
    @Test
    void testPlayerUnlockedPower2_TooFewTokensInBank() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 4);

        // get first player (name = "Player1")
        TradingPostsPlayer p1 = (TradingPostsPlayer) game.getPlayerFromName("Player1");

        p1.extraTokenAfterTakingSameColor.unlockPower(p1);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 2);
        tokensToAdd.put(TokenType.Red, 1);

        HashMap<TokenType, Integer> tokensToRemove = new HashMap<>();
        tokensToAdd.put(TokenType.Red, 7);

        game.removeTokens(tokensToRemove);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new TakeTokenAction(tokensToAdd, new HashMap<>()));

        tokensToAdd.put(TokenType.Green, 0);
        tokensToAdd.put(TokenType.Red, 0);
        tokensToAdd.put(TokenType.Blue, 0);
        tokensToAdd.put(TokenType.Brown, 0);
        tokensToAdd.put(TokenType.Gold, 0);
        tokensToAdd.put(TokenType.Satchel, 0);
        tokensToAdd.put(TokenType.White, 0);

        assertThat(ActionResult.INVALID_TOKENS_GIVEN).isIn(result);
    }

    @DisplayName("Ensure you can buy a card using gold tokens with Power 3")
    @Test
    void testPlayerUsingPower3ToPayForCard() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 4);

        // get first player (name = "Player1")
        TradingPostsPlayer p1 = (TradingPostsPlayer) game.getPlayerFromName("Player1");

        p1.goldTokenWorthTwoTokens.unlockPower(p1);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Gold, 1);
        tokensToAdd.put(TokenType.Blue, 1);
        tokensToAdd.put(TokenType.Brown, 1);

        p1.addTokens(tokensToAdd);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("02", tokensToAdd));

        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
    }

    @DisplayName("Ensure you can buy a card regularly while having power 3 and using gold tokens")
    @Test
    void testPlayerHavingPower3UnlockedButNotUsingIt() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 4);

        // get first player (name = "Player1")
        TradingPostsPlayer p1 = (TradingPostsPlayer) game.getPlayerFromName("Player1");

        p1.goldTokenWorthTwoTokens.unlockPower(p1);

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Gold, 2);
        tokensToAdd.put(TokenType.Blue, 1);
        tokensToAdd.put(TokenType.Brown, 1);

        p1.addTokens(tokensToAdd);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("02", tokensToAdd));

        HashMap<TokenType, Integer> empty = new HashMap<>();
        empty.put(TokenType.Gold, 0);
        empty.put(TokenType.Blue, 0);
        empty.put(TokenType.Brown, 0);
        empty.put(TokenType.Red, 0);
        empty.put(TokenType.Green, 0);
        empty.put(TokenType.White, 0);
        empty.put(TokenType.Satchel, 0);

        assertThat(ActionResult.TURN_COMPLETED).isIn(result);
        assertThat(p1.getTokens()).isEqualTo(empty);
    }

    @DisplayName("Ensure power 4 (add 5 PP) works properly on acquisition of a card triggering gaining a noble")
    @Test
    void testPlayerGain5PPOnNobleAcquisition() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 1);
        p1.addTokens(tokensToAdd);
        p1.addBonus(TokenType.Blue, 2);
        p1.addBonus(TokenType.Red, 2);
        p1.addBonus(TokenType.Green, 3);


        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Green, 1);

        game.takeAction(p1.getName(), new BuyCardAction("05", tokensToUse));

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new ChooseNobleAction("99"));

        // 8 because the power adds 5, the noble adds 3
        assertThat(p1.getPrestigePoints()).isEqualTo(8);
    }

    @DisplayName("Ensure power 4 (add 5 PP) works properly on acquisition of a noble then a card")
    @Test
    void testPlayerGain5PPOnCardAcquisition() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 1);
        p1.addTokens(tokensToAdd);
        p1.addBonus(TokenType.Green, 3);
        NobleCard c1 = (NobleCard) game.getCardFromId("99");
        p1.addNoble(c1);

        game.takeAction(p1.getName(), new BuyCardAction("05", tokensToAdd));


        // 8 because the power adds 5, the noble adds 3
        assertThat(p1.getPrestigePoints()).isEqualTo(8);
    }

    @DisplayName("Ensure power 5 (add 1 PP per power unlocked) works properly when a power was unlocked in the same turn")
    @Test
    void testPlayerGainPPOnPower5UnlockWithOneOtherPower() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 1);
        p1.addTokens(tokensToAdd);
        p1.addBonus(TokenType.Green, 3);
        p1.addBonus(TokenType.Brown, 3);
        NobleCard c1 = (NobleCard) game.getCardFromId("99");
        p1.addNoble(c1);

        game.takeAction(p1.getName(), new BuyCardAction("05", tokensToAdd));


        // 8 because power 4 adds 5, the noble adds 3, then power 5 adds 2 because there are 2 powers unlocked in total
        assertThat(p1.getPrestigePoints()).isEqualTo(10);
    }

    @DisplayName("Ensure power 5 (add 1 PP per power unlocked) works properly when unlocked alone")
    @Test
    void testPlayerGainPPOnPower5UnlockWithNoOtherPower() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 1);
        p1.addTokens(tokensToAdd);

        game.takeAction(p1.getName(), new BuyCardAction("08", tokensToAdd));


        // 1 because power 5 is unlocked on its own, so only 1 power
        assertThat(p1.getPrestigePoints()).isEqualTo(1);
    }

    @DisplayName("Ensure power 5 works when unlocked on some later turn after powers were unlocked")
    @Test
    void testPlayerGainPPOnPower5UnlockWithPowerUnlockedEarlierTurn() throws FileNotFoundException {
        TradingPostsGame game = GameUtils.createNewTradingPostGameFromFile(15, 2);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        Player p2 = game.getPlayerFromName("Player2");

        HashMap<TokenType, Integer> tokensToAdd = new HashMap<>();
        tokensToAdd.put(TokenType.Green, 3);
        p1.addTokens(tokensToAdd);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Green, 1);

        p1.addBonus(TokenType.Green, 3);
        NobleCard c1 = (NobleCard) game.getCardFromId("99");
        p1.addNoble(c1);

        // Unlocks power 4, adding 5PP to player
        game.takeAction(p1.getName(), new BuyCardAction("05", tokensToUse));

        // Increment turn back to player 1
        game.takeAction(p2.getName(), new TakeTokenAction(tokensToUse, new HashMap<>()));

        // Unlocks power 2
        game.takeAction(p1.getName(), new BuyCardAction("09", tokensToUse));

        game.takeAction(p2.getName(), new TakeTokenAction(tokensToUse, new HashMap<>()));

        // Unlocks power 5
        game.takeAction(p1.getName(), new BuyCardAction("08", tokensToUse));

        // 10 because 3 from noble, 5 from power 4 unlocked, 3 from power 5 unlocked and counting 3 powers
        assertThat(p1.getPrestigePoints()).isEqualTo(11);
    }

}
