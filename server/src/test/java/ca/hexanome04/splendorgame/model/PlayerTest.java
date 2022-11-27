package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.actions.BuyCardAction;
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
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        List<Token> tokensToUse = new ArrayList<>();
        tokensToUse.add(new Token(TokenType.Red));
        tokensToUse.add(new Token(TokenType.White));
        tokensToUse.add(new Token(TokenType.Blue));
        tokensToUse.add(new Token(TokenType.Brown));
        tokensToUse.add(new Token(TokenType.Green));
        tokensToUse.add(new Token(TokenType.Gold));
        p1.addTokens(tokensToUse);

        // Make sure tokens added to player are successfully retrieved by getter
        assertThat(p1.getTokens()).isEqualTo(tokensToUse);
    }

    @DisplayName("Ensure player names can be set and retrieved.")
    @Test
    void testPlayerGetName() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.setName("Lisa");

        assertThat(p1.getName()).isEqualTo("Lisa");
    }

    @DisplayName("Ensure player colour can be set and retrieved.")
    @Test
    void testPlayerGetColour() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        p1.setColour("Yellow");

        assertThat(p1.getColour()).isEqualTo("Yellow");
    }

    @DisplayName("Ensure reserved cards can be retrieved.")
    @Test
    void testPlayerReserveCard() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

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
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        // get first player (name = "Player1")
        Player p1 = game.getPlayerFromName("Player1");

        Token token1 = new Token(TokenType.Blue);
        Token token2 = new Token(TokenType.Brown);
        Token token3 = new Token(TokenType.Red);
        Token token4 = new Token(TokenType.White);
        Token token5 = new Token(TokenType.Green);

        List<Token> currentTokens = new ArrayList<>();
        currentTokens.add(token1);
        currentTokens.add(token2);
        p1.addTokens(currentTokens);

        List<Token> tokensToUse = new ArrayList<>();
        tokensToUse.add(token3);
        tokensToUse.add(token4);
        tokensToUse.add(token5);

        HashMap<TokenType, Integer> tokensToPutBack = new HashMap<>();
        tokensToPutBack.put(TokenType.Blue, 1);

        p1.takeTokens(tokensToUse, tokensToPutBack);

        List<Token> tokensExpected = new ArrayList<>();
        tokensExpected.add(token2);
        tokensExpected.add(token3);
        tokensExpected.add(token4);
        tokensExpected.add(token5);

        // Make sure tokens added to player are successfully retrieved by getter
        assertThat(p1.getTokens()).isEqualTo(tokensExpected);
    }

}
