package ca.hexanome04.splendorgame.control;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ca.hexanome04.splendorgame.model.gameversions.GameVersions.*;
import ca.hexanome04.splendorgame.control.templates.*;
import ca.hexanome04.splendorgame.model.GameSession;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test for class SessionManager.
 */
public class SessionManagerTest {

    private final SessionManager dummySessionManager = new SessionManager();
    private final String dummyID = "id";
    private final PlayerInfo p1 = new PlayerInfo("p1", "red");
    private final PlayerInfo p2 = new PlayerInfo("p2", "blue");

    /**
     * Verify add session.
     *
     * @throws Exception Throws exception if adding session fails
     */
    @Test
    @DisplayName("Verify add session")
    public void testAddSession() throws Exception {
        ArrayList<PlayerInfo> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        GameSession game1 = dummySessionManager.createNewSession(dummyID, players, "creator", "sessionName", BASE_ORIENT);
        assertThat(game1).isInstanceOf(GameSession.class);
        // Wrong number of players
        assertThatThrownBy(() -> {
            dummySessionManager.createNewSession(dummyID, null, "creator", "sessionName", BASE_ORIENT);
        }).isInstanceOf(Exception.class);

        // Repeated sessionID
        assertThatThrownBy(() -> {
            dummySessionManager.createNewSession(dummyID, players, "creator", "sessionName", BASE_ORIENT);
        }).isInstanceOf(Exception.class);
    }

    /**
     * Verify get session.
     *
     * @throws Exception Throws exception if adding session fails
     */
    @Test
    @DisplayName("Verify get session")
    public void testGetGameSession() throws Exception {
        // Session not found
        assertNull(dummySessionManager.getGameSession("wrongID"));

        // Session found
        ArrayList<PlayerInfo> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        dummySessionManager.createNewSession(dummyID, players, "creator", "sessionName", BASE_ORIENT);
        assertThat(dummySessionManager.getGameSession(dummyID)).isInstanceOf(GameSession.class);
    }
}
