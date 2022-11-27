package ca.hexanome04.splendorgame.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameSessionTest {


    @DisplayName("Test the getCostType method.")
    @Test
    public void testGetGame() throws FileNotFoundException {
        SplendorGame game = GameUtils.createNewGameFromFile(15, 4);

        GameSession session = new GameSession("12345", "Player1", "MyGame");
        session.setGame(game);

        assertEquals(session.getGame(), game);
    }

    @DisplayName("Test the getCostType method.")
    @Test
    public void testGetSessionId() throws FileNotFoundException {
        GameSession session = new GameSession("12345", "Player1", "MyGame");

        assertEquals(session.getSessionId(), "12345");
    }

    @DisplayName("Test the getCostType method.")
    @Test
    public void testHasSessionLaunched() throws FileNotFoundException {
        GameSession session = new GameSession("12345", "Player1", "MyGame");

        assertEquals(session.hasGameLaunched(), false);
    }

}
