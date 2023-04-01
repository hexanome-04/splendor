package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.LaunchSessionInfo;
import ca.hexanome04.splendorgame.control.templates.PlayerInfo;
import ca.hexanome04.splendorgame.model.GameUtils;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.action.*;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.*;

import java.io.FileNotFoundException;
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
    SplendorRestController restController;
    @MockBean
    GameSavesManager gameSavesManager;

    List<PlayerInfo> testPlayerInfos = new ArrayList<>();
    final String gameServiceName = "gamename";
    final String testGameSessionId = "testgameId";
    private AutoCloseable closeable;

    @BeforeEach
    void setup() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);

        testPlayerInfos.add(new PlayerInfo("p1", "c1"));
        testPlayerInfos.add(new PlayerInfo("p2", "c2"));

        sessionManager = new SessionManager();
        restController = new SplendorRestController(sessionManager, auth,
                gameSavesManager, gameServiceName, 30000);

        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo(
                gameServiceName,
                testPlayerInfos,
                "p1",
                ""
        );
        restController.addSession(testGameSessionId, launchSessionInfo, BASE_ORIENT);
    }

    @AfterEach
    void finish() throws Exception {
        closeable.close();
    }


    /**
     * Verify that the session launches.
     */
    @Test
    @DisplayName("Verify that the session launches")
    public void testLaunchSession() {
        String sessionId = "random";
        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo(gameServiceName + "_" + BASE_ORIENT, testPlayerInfos, testPlayerInfos.get(0).name(), "");

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
        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo(null, testPlayerInfos, testPlayerInfos.get(0).name(), "");

        assertThat(restController.launchSession("sid", launchSessionInfo).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verify that the session launch fails when game service is not valid in launch session info.
     */
    @Test
    @DisplayName("Verify that the session launch fails when game service is not valid in launch session info")
    public void testLaunchSessionInvalidGameServiceLaunchInfoFail() {
        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo("asdasdas", testPlayerInfos, testPlayerInfos.get(0).name(), "");

        assertThat(restController.launchSession("sid", launchSessionInfo).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verify that the session launch fails when a session id already exists.
     */
    @Test
    @DisplayName("Verify that the session launch fails when a session id already exists")
    public void testLaunchSessionDuplicateSessionIdFail() throws Exception {
        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo(gameServiceName, testPlayerInfos, testPlayerInfos.get(0).name(), "");

        sessionManager.createNewSession("sid", testPlayerInfos, "p1", "ssss", BASE_ORIENT);
        assertThat(restController.launchSession("sid", launchSessionInfo).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * Verify that the game state is obtained through the end point.
     */
    @Test
    @DisplayName("Verify that the game state is obtained through the end point")
    public void testApiGameState() {
        ResponseEntity result = (ResponseEntity) restController.getGameState(testGameSessionId, null).getResult();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Verify that no game state is obtained from a non-existent game with the api endpoint.
     */
    @Test
    @DisplayName("Verify that no game state is obtained from a non-existent game with the api endpoint")
    public void testApiGameStateFail() {
        ResponseEntity result = (ResponseEntity) restController.getGameState("s", null).getResult();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
        ResponseEntity result = (ResponseEntity) restController.getGameState(testGameSessionId, null).getResult();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Verify that no game board state is obtained from a non-existent game with the api endpoint.
     */
    @Test
    @DisplayName("Verify that no game board state is obtained from a non-existent game with the api endpoint")
    public void testApiGameBoardStateFail() {
        ResponseEntity result = (ResponseEntity) restController.getGameState("s", null).getResult();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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

    @Test
    @DisplayName("Verify previously saved game launches")
    void testLaunchSavedGame() throws FileNotFoundException {
        String sessionId = "random";
        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo(gameServiceName + "_" + BASE_ORIENT,
                testPlayerInfos,
                testPlayerInfos.get(0).name(),
                "save");

        Game game = GameUtils.createNewOrientGame(15, 2);
        Mockito.when(gameSavesManager.getGameSaveData(Mockito.any()))
                .thenReturn(game);

        assertThat(restController.launchSession(sessionId, launchSessionInfo).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Verify previously saved game assigns players correctly")
    void testLaunchSavedGameSetPlayers() throws FileNotFoundException {
        String sessionId = "random";
        List<PlayerInfo> playerInfoList = List.of(
                new PlayerInfo("p1", "c1"),
                new PlayerInfo("p2", "c2"),
                new PlayerInfo("p3", "c3"),
                new PlayerInfo("p4", "c4")
        );
        LaunchSessionInfo launchSessionInfo = new LaunchSessionInfo(gameServiceName + "_" + BASE_ORIENT,
                playerInfoList,
                playerInfoList.get(0).name(),
                "save");

        Game game = GameUtils.createNewOrientGame(15, 4);
        // modify the players in the game so it matches some of the players in the previous list
        List<Player> players = game.getPlayers();
        players.get(2).setName(playerInfoList.get(0).name()); // 3rd player in game matches 1st player in launch info
        players.get(0).setName(playerInfoList.get(3).name()); // 1st player in game matches 4th player in launch info

        Mockito.when(gameSavesManager.getGameSaveData(Mockito.any()))
                .thenReturn(game);

        assertThat(restController.launchSession(sessionId, launchSessionInfo).getStatusCode()).isEqualTo(HttpStatus.OK);

        // verify info is set properly in launched game
        Game loadedGame = sessionManager.getGameSession(sessionId).getGame();
        List<Player> loadedPlayers = loadedGame.getPlayers();
        assertThat(loadedPlayers.size()).isEqualTo(playerInfoList.size());

        for (PlayerInfo pi : playerInfoList) {
            Player loadPlayer = null;
            for (Player p : loadedPlayers) {
                if (p.getName().equals(pi.name())) {
                    loadPlayer = p;
                    break;
                }
            }

            // check player name and color, ensure they are in the game and have been set to an inactive player
            assertThat(loadPlayer.getColour()).isEqualTo(pi.colour()); // 429 :(
        }
    }

    @Test
    @DisplayName("Verify that the deck does not show non-visible cards to the client")
    public void testDeckHidesFromClient() {
        ResponseEntity result = (ResponseEntity) restController.getGameState(testGameSessionId, null).getResult();

        JsonObject jsonObject = JsonParser.parseString((String) result.getBody()).getAsJsonObject();
        JsonObject deckObject = jsonObject.getAsJsonObject("tier1Deck");
        assertThat(deckObject.has("cards")).isFalse();
        assertThat(deckObject.has("visibleCards")).isTrue();
    }
}
