package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.LaunchSessionInfo;
import ca.hexanome04.splendorgame.control.templates.PlayerInfo;
import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static ca.hexanome04.splendorgame.model.gameversions.GameVersions.BASE_ORIENT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Test for class SplendorRestController.
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class SplendorRestControllerTest {

    @Mock
    Authentication auth;
    SessionManager sessionManager;
    @InjectMocks
    @Autowired
    SplendorRestController restController;

    List<Player> testPlayers = new ArrayList<>();
    List<PlayerInfo> testPlayerInfos = new ArrayList<>();
    final String gameServiceName = "gamename";
    final String testGameSessionId = "testgameId";

    @BeforeEach
    void setup() throws Exception {
        testPlayers.add(new Player("p1", "c1"));
        testPlayers.add(new Player("p2", "c2"));

        testPlayerInfos.add(new PlayerInfo("p1", "c1"));
        testPlayerInfos.add(new PlayerInfo("p2", "c2"));

        sessionManager = new SessionManager();
        restController = new SplendorRestController(sessionManager, auth, gameServiceName);

        sessionManager.addSession(testGameSessionId, (ArrayList<Player>) testPlayers, "p1", "", BASE_ORIENT);
    }

    /**
     * Verify that the session launches.
     */
    @Test
    @DisplayName("Verify that the session launches")
    public void testLaunchSession() {
        String sessionId = "random";
        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo(gameServiceName, testPlayerInfos, testPlayers.get(0).getName(), "");

        assertThat(restController.launchSession(sessionId, launchSessionInfo).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Verify that the session launch fails when missing launch session info.
     */
    @Test
    @DisplayName("Verify that the session launch fails when missing launch session info")
    public void testLaunchSessionNoLaunchInfoFail() {
        assertThat(restController.launchSession("sid", null).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verify that the session launch fails when missing game service name in launch session info.
     */
    @Test
    @DisplayName("Verify that the session launch fails when missing game service name in launch session info")
    public void testLaunchSessionNoGameServiceNameLaunchInfoFail() {
        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo(null, testPlayerInfos, testPlayers.get(0).getName(), "");

        assertThat(restController.launchSession("sid", launchSessionInfo).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verify that the session launch fails when game service is not valid in launch session info.
     */
    @Test
    @DisplayName("Verify that the session launch fails when game service is not valid in launch session info")
    public void testLaunchSessionInvalidGameServiceLaunchInfoFail() {
        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo("asdasdas", testPlayerInfos, testPlayers.get(0).getName(), "");

        assertThat(restController.launchSession("sid", launchSessionInfo).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verify that the session launch fails when a session id already exists.
     */
    @Test
    @DisplayName("Verify that the session launch fails when a session id already exists")
    public void testLaunchSessionDuplicateSessionIdFail() throws Exception {
        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo(gameServiceName, testPlayerInfos, testPlayers.get(0).getName(), "");

        sessionManager.addSession("sid", (ArrayList<Player>) testPlayers, "p1", "ssss", BASE_ORIENT);
        assertThat(restController.launchSession("sid", launchSessionInfo).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verify that the game state is obtained through the end point.
     */
    @Test
    @DisplayName("Verify that the game state is obtained through the end point")
    public void testApiGameState() {
        assertThat(restController.getGameState(testGameSessionId).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Verify that no game state is obtained from a non-existent game with the api endpoint.
     */
    @Test
    @DisplayName("Verify that no game state is obtained from a non-existent game with the api endpoint")
    public void testApiGameStateFail() {
        assertThat(restController.getGameState("nonexist").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verify that the players state is obtained through the end point.
     */
    @Test
    @DisplayName("Verify that the players state is obtained through the end point")
    public void testApiPlayersState() {
        assertThat(restController.getPlayers(testGameSessionId).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Verify that no players state is obtained from a non-existent game with the api endpoint.
     */
    @Test
    @DisplayName("Verify that no players state is obtained from a non-existent game with the api endpoint")
    public void testApiPlayersStateFail() {
        assertThat(restController.getPlayers("nonexist").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verify that the game board state is obtained through the end point.
     */
    @Test
    @DisplayName("Verify that the game board state is obtained through the end point")
    public void testApiGameBoardState() {
        assertThat(restController.getGameBoard(testGameSessionId).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Verify that no game board state is obtained from a non-existent game with the api endpoint.
     */
    @Test
    @DisplayName("Verify that no game board state is obtained from a non-existent game with the api endpoint")
    public void testApiGameBoardStateFail() {
        assertThat(restController.getGameBoard("nonexist").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verify that the list of possible actions is obtained through the end point.
     */
    @Test
    @DisplayName("Verify that the list of possible actions is obtained through the end point")
    public void testApiGetActions() {
        assertThat(restController.getActions(testGameSessionId, "p1").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Verify that no list of possible actions is obtained with an invalid game through the end point.
     */
    @Test
    @DisplayName("Verify that no game board state is obtained from a non-existent game with the api endpoint")
    public void testApiGetActionsFail() {
        assertThat(restController.getActions("nonexist", "dssdf").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

//    /**
//     * Verify that a put action is successful through the end point.
//     */
//    @Test
//    @DisplayName("Verify that the game board state is obtained through the end point")
//    public void testApiPutAction() {
//        assertThat(restController.putAction("token", testGameSessionId, "p1", Actions.BUY_CARD, ).getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    /**
     * Verify that a put action is unsuccessful due to invalid body data through the end point.
     */
    @Test
    @DisplayName("Verify that a put action is unsuccessful due to invalid body data through the end point")
    public void testApiPutActionFail() {
        Mockito.when(auth.getNameFromToken(Mockito.any(String.class)))
                        .thenReturn("p1");

        assertThat(restController.putAction("token", testGameSessionId, "p1", Actions.BUY_CARD, "{}")
                .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


}
