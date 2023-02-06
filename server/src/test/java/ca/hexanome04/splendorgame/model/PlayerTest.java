package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
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
 * Test class for player.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PlayerTest {

    @DisplayName("Ensure players correctly store and retrieve tokens.")
    @Test
    void testPlayerGetTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 1);
        tokensToUse.put(TokenType.White, 1);
        tokensToUse.put(TokenType.Blue, 1);
        tokensToUse.put(TokenType.Brown, 1);
        tokensToUse.put(TokenType.Green, 1);
        tokensToUse.put(TokenType.Gold, 1);
        tokensToUse.put(TokenType.Satchel, 0);
        p1.addTokens(tokensToUse);

        // Make sure tokens added to player are successfully retrieved by getter
        assertThat(p1.getTokens()).isEqualTo(tokensToUse);
    }

    @DisplayName("Ensure player names can be set and retrieved.")
    @Test
    void testPlayerGetName() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.setName("Lisa");

        assertThat(p1.getName()).isEqualTo("Lisa");
    }

    @DisplayName("Ensure player colour can be set and retrieved.")
    @Test
    void testPlayerGetColour() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.setColour("Yellow");

        assertThat(p1.getColour()).isEqualTo("Yellow");
    }

    @DisplayName("Ensure reserved cards can be retrieved.")
    @Test
    void testPlayerReserveCard() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        RegDevelopmentCard devCard = new RegDevelopmentCard(CardTier.TIER_2, TokenType.Red, 2, 2,
                CostType.Token,
                new HashMap<>(){{
                    put(TokenType.Green, 3);
                    put(TokenType.White, 0);
                    put(TokenType.Blue, 2);
                    put(TokenType.Brown, 1);
                    put(TokenType.Red, 1);
                }},
                "234"
        );

        List<DevelopmentCard> expected = new ArrayList<>();
        expected.add(devCard);

        p1.reserveCard(devCard);

        assertThat(p1.getReservedCards()).isEqualTo(expected);
    }

    @DisplayName("Ensure players correctly store and retrieve tokens.")
    @Test
    void testPlayerTakeTokens() throws FileNotFoundException {
        OrientGame game = GameUtils.createNewOrientGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        HashMap<TokenType, Integer> currentTokens = new HashMap<>();
        currentTokens.put(TokenType.Blue, 1);
        currentTokens.put(TokenType.Brown, 1);
        p1.addTokens(currentTokens);

        HashMap<TokenType, Integer> tokensToUse = new HashMap<>();
        tokensToUse.put(TokenType.Red, 1);
        tokensToUse.put(TokenType.White, 1);
        tokensToUse.put(TokenType.Green, 1);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();
        tokensToPutBack.put(TokenType.Blue, 1);

        p1.takeTokens(tokensToUse, tokensToPutBack);

        HashMap<TokenType, Integer> tokensExpected = new HashMap<>();
        tokensExpected.put(TokenType.Brown, 1);
        tokensExpected.put(TokenType.Red, 1);
        tokensExpected.put(TokenType.White, 1);
        tokensExpected.put(TokenType.Green, 1);
        tokensExpected.put(TokenType.Blue, 0);
        tokensExpected.put(TokenType.Satchel, 0);
        tokensExpected.put(TokenType.Gold, 0);

        // Make sure tokens added to player are successfully retrieved by getter
        assertThat(p1.getTokens()).isEqualTo(tokensExpected);
    }

}
