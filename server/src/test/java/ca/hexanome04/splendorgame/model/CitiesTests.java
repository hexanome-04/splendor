package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.actions.*;
import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesGame;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesPlayer;
import ca.hexanome04.splendorgame.model.gameversions.cities.CityCard;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
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
 * Tests for the Cities expansion of the game.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CitiesTests {

    @DisplayName("Ensure end of cities game is marked when player qualifies for 'same bonus' type city card.")
    @Test
    void testEndOfCitiesRound_SameBonusCity() throws FileNotFoundException {
        CitiesGame game = GameUtils.createNewCitiesGame( 1);

        // get first player (name = "Player1")
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(14);
        p1.addBonus(TokenType.White, 4);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);
        p1.addTokens(tokensToUse);

        CityCard city = (CityCard) game.getCardFromId("40012");

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(game.getWinner()).isEqualTo(p1);
        assertThat(game.isGameOver()).isTrue();
        assertThat(city).isIn(p1.getCities());
    }

    @DisplayName("Ensure end of cities game is marked when player qualifies for bonus + same bonus city card.")
    @Test
    void testEndOfCitiesRound_BonusAndSameBonusCity() throws FileNotFoundException {
        CitiesGame game = GameUtils.createNewCitiesGame( 1);

        // get first player (name = "Player1")
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(11);
        p1.addBonus(TokenType.Red, 3);
        p1.addBonus(TokenType.Brown, 3);
        p1.addBonus(TokenType.Blue, 1);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Blue, 2);
        tokensToUse.put(TokenType.Green, 2);
        tokensToUse.put(TokenType.Red, 1);
        p1.addTokens(tokensToUse);

        CityCard city = (CityCard) game.getCardFromId("40001");

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("03", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(game.getWinner()).isEqualTo(p1);
        assertThat(game.isGameOver()).isTrue();
        assertThat(city).isIn(p1.getCities());
    }

    @DisplayName("Ensure cities game doesn't end when player doesn't have separate same-bonus type from bonus price.")
    @Test
    void testEndOfCitiesRound_BonusAndSameBonusCity_NoWin() throws FileNotFoundException {
        CitiesGame game = GameUtils.createNewCitiesGame( 1);

        // get first player (name = "Player1")
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(11);
        p1.addBonus(TokenType.Red, 3);
        p1.addBonus(TokenType.Brown, 3);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Blue, 2);
        tokensToUse.put(TokenType.Green, 2);
        tokensToUse.put(TokenType.Red, 1);
        p1.addTokens(tokensToUse);

        CityCard city = (CityCard) game.getCardFromId("40001");

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("03", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(game.getWinner()).isEqualTo(null);
        assertThat(game.isGameOver()).isFalse();
        assertThat(city).isNotIn(p1.getCities());
    }

    @DisplayName("Ensure end of cities game is marked when player qualifies for a city card that costs exclusively prestige points.")
    @Test
    void testEndOfCitiesRound_PrestigePointsCity() throws FileNotFoundException {
        CitiesGame game = GameUtils.createNewCitiesGame( 1);

        // get first player (name = "Player1")
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(15);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Blue, 2);
        tokensToUse.put(TokenType.Green, 2);
        tokensToUse.put(TokenType.Red, 1);
        p1.addTokens(tokensToUse);

        CityCard city = (CityCard) game.getCardFromId("40003");

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("03", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(game.getWinner()).isEqualTo(p1);
        assertThat(game.isGameOver()).isTrue();
        assertThat(city).isIn(p1.getCities());
    }

    @DisplayName("Ensure end of game is marked at end of round when >1 player reaches gets a city.")
    @Test
    void testEndOfRound_TwoWinners() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewCitiesGame(2);

        // player 1 will qualify for 5 same bonuses city card
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(14);
        p1.addBonus(TokenType.White, 4);
        HashMap<TokenType, Integer> tokensToUse1 = new HashMap<>();
        tokensToUse1.put(TokenType.Red, 4);
        p1.addTokens(tokensToUse1);

        // player 2 will qualify for 17 prestige points city card
        CitiesPlayer p2 = (CitiesPlayer) game.getPlayerFromName("Player2");
        p2.addPrestigePoints(17);

        CityCard city1 = (CityCard) game.getCardFromId("40012");
        CityCard city2 = (CityCard) game.getCardFromId("40003");

        ArrayList<ActionResult> result1 = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse1));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result1);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner()).isEqualTo(p2);
        assertThat(game.isGameOver()).isTrue();
        assertThat(city1).isIn(p1.getCities());
        assertThat(city2).isIn(p2.getCities());
    }

    @DisplayName("Ensure cities expansion does not falsely give win to players.")
    @Test
    void testEndOfCitiesRound_NoWinner() throws FileNotFoundException {
        CitiesGame game = GameUtils.createNewCitiesGame( 1);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        p1.addBonus(TokenType.Red, 3);
        p1.addBonus(TokenType.Brown, 3);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Blue, 2);
        tokensToUse.put(TokenType.Green, 2);
        tokensToUse.put(TokenType.Red, 1);
        p1.addTokens(tokensToUse);

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("03", tokensToUse));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(game.getWinner()).isEqualTo(null);
        assertThat(game.isGameOver()).isFalse();
    }

}
