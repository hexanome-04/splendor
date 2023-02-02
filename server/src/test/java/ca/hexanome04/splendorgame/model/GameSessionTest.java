package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.GameBaseOrient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for GameSession.
 */
public class GameSessionTest {


    /**
     * Test the getGame method.
     *
     * @throws FileNotFoundException Throws exception if loading from file fails
     */
    @DisplayName("Test the getGame method.")
    @Test
    public void testGetGame() throws FileNotFoundException {
        GameBaseOrient game = GameUtils.createNewGameFromFile(15, 4);

        GameSession session = new GameSession("12345", "Player1", "MyGame");
        session.setGame(game);

        assertEquals(session.getGame(), game);
    }

    /**
     * Test the getSessionId method.
     */
    @DisplayName("Test the getSessionId method.")
    @Test
    public void testGetSessionId() {
        GameSession session = new GameSession("12345", "Player1", "MyGame");

        assertEquals(session.getSessionId(), "12345");
    }

    /**
     * Test the getCostType method.
     */
    @DisplayName("Test the getCostType method.")
    @Test
    public void testHasSessionLaunched() {
        GameSession session = new GameSession("12345", "Player1", "MyGame");

        assertEquals(session.hasGameLaunched(), false);
    }

}
