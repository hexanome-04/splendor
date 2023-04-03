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
        CitiesGame game = GameUtils.createNewCitiesGame( 2);

        // get first player (name = "Player1")
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(14);
        p1.addBonus(TokenType.White, 4);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);
        p1.addTokens(tokensToUse);

        CitiesPlayer p2 = (CitiesPlayer) game.getPlayerFromName("Player2");

        CityCard city = (CityCard) game.getCardFromId("40012");

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().size()).isEqualTo(1);
        assertThat(game.getWinner().get(0)).isEqualTo(p1);
        assertThat(game.isGameOver()).isTrue();
        assertThat(city).isIn(p1.getCities());
    }

    @DisplayName("Ensure end of cities game is marked when player qualifies for bonus + same bonus city card.")
    @Test
    void testEndOfCitiesRound_BonusAndSameBonusCity() throws FileNotFoundException {
        CitiesGame game = GameUtils.createNewCitiesGame( 2);

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

        CitiesPlayer p2 = (CitiesPlayer) game.getPlayerFromName("Player2");

        CityCard city = (CityCard) game.getCardFromId("40001");

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("03", tokensToUse));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().size()).isEqualTo(1);
        assertThat(game.getWinner().get(0)).isEqualTo(p1);
        assertThat(game.isGameOver()).isTrue();
        assertThat(city).isIn(p1.getCities());
    }

    @DisplayName("Ensure cities game doesn't end when player doesn't have separate same-bonus type from bonus price.")
    @Test
    void testEndOfCitiesRound_BonusAndSameBonusCity_NoWin() throws FileNotFoundException {
        CitiesGame game = GameUtils.createNewCitiesGame( 2);

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

        CitiesPlayer p2 = (CitiesPlayer) game.getPlayerFromName("Player2");

        CityCard city = (CityCard) game.getCardFromId("40001");

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("03", tokensToUse));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().size()).isEqualTo(0);
        assertThat(game.isGameOver()).isFalse();
        assertThat(city).isNotIn(p1.getCities());
    }

    @DisplayName("Ensure end of cities game is marked when player qualifies for a city card that costs exclusively prestige points.")
    @Test
    void testEndOfCitiesRound_PrestigePointsCity() throws FileNotFoundException {
        CitiesGame game = GameUtils.createNewCitiesGame( 2);

        // get first player (name = "Player1")
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(15);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Blue, 2);
        tokensToUse.put(TokenType.Green, 2);
        tokensToUse.put(TokenType.Red, 1);
        p1.addTokens(tokensToUse);

        CitiesPlayer p2 = (CitiesPlayer) game.getPlayerFromName("Player2");

        CityCard city = (CityCard) game.getCardFromId("40003");

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("03", tokensToUse));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().size()).isEqualTo(1);
        assertThat(game.getWinner().get(0)).isEqualTo(p1);
        assertThat(game.isGameOver()).isTrue();
        assertThat(city).isIn(p1.getCities());
    }

    @DisplayName("Ensure end of game is marked at end of round when >1 player reaches gets a city.")
    @Test
    void testEndOfCitiesRound_TwoPotentialWinners() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewCitiesGame(2);

        // player 1 will qualify for 5 same bonuses city card
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(15);
        p1.addBonus(TokenType.White, 5);

        // player 2 will qualify for 17 prestige points city card
        CitiesPlayer p2 = (CitiesPlayer) game.getPlayerFromName("Player2");
        p2.addPrestigePoints(17);

        CityCard city1 = (CityCard) game.getCardFromId("40012");
        CityCard city2 = (CityCard) game.getCardFromId("40003");

        ArrayList<ActionResult> result1 = game.takeAction(p1.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result1);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().size()).isEqualTo(1);
        assertThat(game.getWinner().get(0)).isEqualTo(p2);
        assertThat(game.isGameOver()).isTrue();
        assertThat(city1).isIn(p1.getCities());
        assertThat(city2).isIn(p2.getCities());
    }

    @DisplayName("Ensure end of game is marked at end of round when a tie occurs.")
    @Test
    void testEndOfCitiesRound_Tie() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewCitiesGame(2);

        // player 1 will qualify for 5 same bonuses city card
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(17);

        // player 2 will qualify for 17 prestige points city card
        CitiesPlayer p2 = (CitiesPlayer) game.getPlayerFromName("Player2");
        p2.addPrestigePoints(17);
        p2.addBonus(TokenType.White, 5);

        CityCard city1 = (CityCard) game.getCardFromId("40003");
        CityCard city2 = (CityCard) game.getCardFromId("40012");

        ArrayList<ActionResult> result1 = game.takeAction(p1.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result1);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().contains(p1)).isTrue();
        assertThat(game.getWinner().contains(p2)).isTrue();
        assertThat(game.getWinner().size()).isEqualTo(2);
        assertThat(game.isGameOver()).isTrue();
        assertThat(city1).isIn(p1.getCities());
        assertThat(city2).isIn(p2.getCities());
    }

    @DisplayName("Ensure end of game is marked at end of round when a tie occurs.")
    @Test
    void testEndOfCitiesRound_ThreeWayTie() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewCitiesGame(3);

        // player 1 will qualify for 5 same bonuses city card
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(17);

        // player 2 will qualify for 17 prestige points city card
        CitiesPlayer p2 = (CitiesPlayer) game.getPlayerFromName("Player2");
        p2.addPrestigePoints(17);
        p2.addBonus(TokenType.White, 5);

        // player 3 will qualify for 13 prestige points + normal bonuses city card
        CitiesPlayer p3 = (CitiesPlayer) game.getPlayerFromName("Player3");
        p3.addPrestigePoints(17);
        p3.addBonus(TokenType.Red, 4);
        p3.addBonus(TokenType.Brown, 3);
        p3.addBonus(TokenType.Green, 1);


        CityCard city1 = (CityCard) game.getCardFromId("40003");
        CityCard city2 = (CityCard) game.getCardFromId("40012");
        CityCard city3 = (CityCard) game.getCardFromId("40001");

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
        assertThat(city1).isIn(p1.getCities());
        assertThat(city2).isIn(p2.getCities());
        assertThat(city3).isIn(p3.getCities());
    }

    @DisplayName("Ensure cities expansion does not falsely give win to players.")
    @Test
    void testEndOfCitiesRound_NoWinner() throws FileNotFoundException {
        CitiesGame game = GameUtils.createNewCitiesGame( 2);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");
        p1.addBonus(TokenType.Red, 3);
        p1.addBonus(TokenType.Brown, 3);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Blue, 2);
        tokensToUse.put(TokenType.Green, 2);
        tokensToUse.put(TokenType.Red, 1);
        p1.addTokens(tokensToUse);

        CitiesPlayer p2 = (CitiesPlayer) game.getPlayerFromName("Player2");

        ArrayList<ActionResult> result = game.takeAction(p1.getName(), new BuyCardAction("03", tokensToUse));
        ArrayList<ActionResult> result2 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(game.getWinner().size()).isEqualTo(0);
        assertThat(game.isGameOver()).isFalse();
    }

    @DisplayName("Ensure end of cities game is marked after player has to choose a city.")
    @Test
    void testEndOfCitiesRound_TwoCities() throws FileNotFoundException {
        CitiesGame game = GameUtils.createNewCitiesGame( 2);

        // get first player (name = "Player1")
        CitiesPlayer p1 = (CitiesPlayer) game.getPlayerFromName("Player1");
        p1.addPrestigePoints(16);
        p1.addBonus(TokenType.White, 4);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 4);
        p1.addTokens(tokensToUse);

        CitiesPlayer p2 = (CitiesPlayer) game.getPlayerFromName("Player2");

        CityCard city1 = (CityCard) game.getCardFromId("40012");
        CityCard city2 = (CityCard) game.getCardFromId("40003");

        ArrayList<ActionResult> result1 = game.takeAction(p1.getName(), new BuyCardAction("01", tokensToUse));
        ArrayList<ActionResult> result2 = game.takeAction(p1.getName(), new ChooseCityAction("40003"));
        ArrayList<ActionResult> result3 = game.takeAction(p2.getName(), new TakeTokenAction(new HashMap<>(), new HashMap<>()));

        // make sure action is valid since player can afford it
        assertThat(ActionResult.VALID_ACTION).isIn(result1);
        assertThat(ActionResult.MUST_CHOOSE_CITY).isIn(result1);
        assertThat(ActionResult.VALID_ACTION).isIn(result2);
        assertThat(ActionResult.TURN_COMPLETED).isIn(result2);
        assertThat(ActionResult.VALID_ACTION).isIn(result3);
        assertThat(ActionResult.TURN_COMPLETED).isIn(result3);

        assertThat(game.getWinner().size()).isEqualTo(1);
        assertThat(game.getWinner().get(0)).isEqualTo(p1);
        assertThat(game.isGameOver()).isTrue();
        assertThat(city2).isIn(p1.getCities());
        assertThat(city1).isNotIn(p1.getCities());
    }

}
