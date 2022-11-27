package ca.hexanome04.splendorgame.control;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.GameSession;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for class SessionManager.
 */
public class SessionManagerTest {

    private final SessionManager dummySessionManager = new SessionManager();
    private final String dummyID = "id";
    private final Player p1 = new Player("p1", "red");
    private final Player p2 = new Player("p2", "blue");

    @Test
    @DisplayName("Verify add session")
    public void testAddSession() throws Exception {
        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        GameSession game1 = dummySessionManager.addSession(dummyID, players, "creator", "sessionName");
        assertThat(game1).isInstanceOf(GameSession.class);
        // Wrong number of players
        assertThat(
                dummySessionManager.addSession(dummyID, null, "creator", "sessionName")
        ).isInstanceOf(Exception.class);
        // Repeated sessionID
        assertThat(
                dummySessionManager.addSession(dummyID, players, "creator", "sessionName")
        ).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Verify get session")
    public void testGetGameSession() throws Exception {
        // Session not found
        assertNull(dummySessionManager.getGameSession("wrongID"));

        // Session found
        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        dummySessionManager.addSession(dummyID, players, "creator", "sessionName");
        assertThat(dummySessionManager.getGameSession(dummyID)).isInstanceOf(GameSession.class);
    }
}
