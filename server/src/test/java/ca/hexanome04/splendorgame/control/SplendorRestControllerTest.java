package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.LaunchSessionInfo;
import ca.hexanome04.splendorgame.control.templates.PlayerInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Test for class SplendorRestController.
 */
public class SplendorRestControllerTest {

    private String dummyID = "id";
    private String dummyGameServer = "Splendor";
    private final PlayerInfo p1 = new PlayerInfo("p1", "red");
    private final PlayerInfo p2 = new PlayerInfo("p2", "blue");
    Authentication auth;

    @Test
    @DisplayName("Verify that the session launches")
    public void testLaunchSession() {
        SessionManager dummySessionManager = new SessionManager();
        List<PlayerInfo> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        LaunchSessionInfo launchSessionInfoGood = new LaunchSessionInfo(dummyGameServer, players, "creator", "sessionName");
        LaunchSessionInfo launchSessionInfoNoGS = new LaunchSessionInfo(null, players, "creator", "sessionName");
        LaunchSessionInfo launchSessionInfoBadGS = new LaunchSessionInfo("wrong", players, "creator", "sessionName");
        SplendorRestController splendorRestController = new SplendorRestController(dummySessionManager, auth);
        assertThat(
                splendorRestController.launchSession(dummyID, null)
        ).isInstanceOf(Exception.class);
        assertThat(
                splendorRestController.launchSession(dummyID, launchSessionInfoNoGS)
        ).isInstanceOf(Exception.class);
        assertThat(
                splendorRestController.launchSession(dummyID, launchSessionInfoBadGS)
        ).isInstanceOf(Exception.class);
        assertThat(
                splendorRestController.launchSession(dummyID, launchSessionInfoGood)
        ).isInstanceOf(ResponseEntity.class);
    }

    public void testGetGameState() {

    }
}
