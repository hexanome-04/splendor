package ca.hexanome04.splendorgame.control;

import static ca.hexanome04.splendorgame.model.gameversions.GameVersions.BASE_ORIENT;

import ca.hexanome04.splendorgame.control.templates.LaunchSessionInfo;
import ca.hexanome04.splendorgame.control.templates.PlayerInfo;
import ca.hexanome04.splendorgame.model.GameSession;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.action.ActionDecoder;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.dacbiet.simpoll.ContentWatcher;
import dev.dacbiet.simpoll.Fetcher;
import dev.dacbiet.simpoll.ResultGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;


/**
 * Rest controller for all API endpoints of Splendor game.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class SplendorRestController {

    final Logger logger = LoggerFactory.getLogger(SplendorRestController.class);

    private String gameServiceName;
    private final SessionManager sessionManager;
    private final Authentication auth;
    private final long longPollTimeout;
    private final Map<String, ContentWatcher> gameWatcher;

    /**
     * Create an instance of Splendor's REST controller.
     *
     * @param sessionManager session manager
     * @param auth methods relating to authentication with LS
     * @param gameServiceName game service name
     * @param longPollTimeout timeout for long polling
     */

    public SplendorRestController(@Autowired SessionManager sessionManager,
                                  @Autowired Authentication auth,
                                  @Value("${gs.name}") String gameServiceName,
                                  @Value("${longpoll.timeout}") int longPollTimeout) {
        this.sessionManager = sessionManager;
        this.auth = auth;
        this.gameServiceName = gameServiceName;
        this.longPollTimeout = longPollTimeout;
        this.gameWatcher = new HashMap<>();
    }

    /**
     * Check if server is online.
     *
     * @return Quick statement confirming whether server is online
     */
    @GetMapping(value = "/api/online", produces = "application/json; charset=utf-8")
    public String online() {
        return "The server currently has " + sessionManager.getNumSessions() + " sessions created.";

    }

    /**
     * Launch a session (only from lobby service).
     *
     * @param sessionId session id for new session
     * @param launchSessionInfo information about the new session
     * @return HTTP response entity
     */
    @PutMapping(value = "/api/sessions/{sessionId}", consumes = "application/json; charset=utf-8")
    public ResponseEntity launchSession(@PathVariable String sessionId, @RequestBody LaunchSessionInfo launchSessionInfo) {

        try {
            if (launchSessionInfo == null) {
                throw new Exception("Missing launch session info.");
            }
            if (launchSessionInfo.gameServer() == null) {
                throw new Exception("Missing service name in launch session info.");
            }

            // verify if game service name is valid.
            boolean validGameServiceName = false;
            GameVersions gameVersion = null;
            for (GameVersions gv : GameVersions.values()) {
                if (launchSessionInfo.gameServer().equals(gameServiceName + "_" + gv)) {
                    validGameServiceName = true;
                    gameVersion = gv;
                    break;
                }
            }
            if (!validGameServiceName) {
                throw new Exception("Lobby Service did not specify a matching Service name.");
            }

            if (sessionManager.getGameSession(sessionId) != null) {
                throw new Exception("Game can not be launched. Id is already in use.");
            }

            // Looks good, lets create the game
            this.addSession(sessionId, launchSessionInfo, gameVersion);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.error("Unable to launch game session: " + e.getMessage());
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Adds a session to the session manger and initializes the game.
     *
     * @param sessionId session id of game
     * @param launchSessionInfo launch session info of game
     * @param gameVersion  game version to launch as
     * @throws Exception thrown due to IO or invalid information given
     */
    protected void addSession(String sessionId, LaunchSessionInfo launchSessionInfo, GameVersions gameVersion) throws Exception {
        sessionManager.addSession(sessionId, launchSessionInfo.players(),
                launchSessionInfo.creator(),
                launchSessionInfo.savegame(),
                gameVersion);
        logger.info("Launched new game session: " + sessionId);
        gameWatcher.put(sessionId, new ContentWatcher());
    }

    /**
     * Obtain the game state of specified session.
     *
     * @param sessionId session id to get game state of
     * @param hash optional hash from client
     * @return JSON object of game state
     */
    @GetMapping(value = "/api/sessions/{sessionId}", produces = "application/json; charset=utf-8")
    public DeferredResult getGameState(@PathVariable String sessionId, @RequestParam(required = false) String hash) {
        try {
            // Check if session exists
            GameSession game = sessionManager.getGameSession(sessionId);
            if (game == null) {
                throw new Exception("There is no session associated this session ID: " + sessionId + ".");
            }

            ContentWatcher watcher = gameWatcher.get(sessionId);
            // serialize game
            Fetcher fetcher = () -> new Gson().toJson(game.getGame());

            return ResultGenerator.getStringResult(watcher, fetcher, hash, this.longPollTimeout);
        } catch (Exception e) {
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            DeferredResult result = new DeferredResult();
            result.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()));
            return result;
        }
    }

    /**
     * Get players in the specified game session.
     *
     * @param sessionId game session id
     * @return JSON object of players
     */
    @GetMapping(value = "/api/sessions/{sessionId}/players", produces = "application/json; charset=utf-8")
    public ResponseEntity getPlayers(@PathVariable String sessionId) {
        try {
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionId + ".");
            }

            String serializedPlayers = new Gson().toJson(sessionManager.getGameSession(sessionId).getGame().getPlayers());
            return ResponseEntity.status(HttpStatus.OK).body(serializedPlayers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Obtain the available actions for this turn.
     *
     * @param sessionId session id to get actions for
     * @param playerName player whose action is being put
     * @return JSON object of available actions
     */
    @GetMapping(value = "/api/sessions/{sessionId}/players/{playerName}/actions",
                produces = "application/json; charset=utf-8")
    public ResponseEntity getActions(@PathVariable String sessionId, @PathVariable String playerName) {
        try {
            // Check if session exists
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionId + ".");
            }
            // Check if player exists
            Game game = sessionManager.getGameSession(sessionId).getGame();
            Player player = game.getPlayerFromName(playerName);
            if (player == null) {
                throw new Exception("There is no player in this session associated with this playerID: " + sessionId + ".");
            }

            // If so, serialize actions and place it as body in a ResponseEntity
            String[] actions = new String[game.getCurValidActions().size()];
            for (int i = 0; i < game.getCurValidActions().size(); i++) {
                actions[i] = game.getCurValidActions().get(i).toString();
            }

            String serializedActions = new Gson().toJson(actions);
            return ResponseEntity.status(HttpStatus.OK).body(serializedActions);
        } catch (Exception e) {
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Perform an action to specified session for specified player.
     * Will perform checks to ensure correct player is performing the action
     * and that the player is able to perform the action.
     *
     * @param token access_token
     * @param sessionId session id
     * @param playerName player name who is performing action
     * @param actionIdentifier action identifier
     * @param bodyData JSON string information of action
     * @return String (empty is OK)
     */
    @PutMapping(value = "/api/sessions/{sessionId}/players/{playerName}/actions/{actionIdentifier}",
            consumes = "application/json; charset=utf-8")
    public ResponseEntity<String> putAction(@RequestParam("access_token") String token, @PathVariable String sessionId,
                                            @PathVariable String playerName, @PathVariable Actions actionIdentifier,
                                            @RequestBody String bodyData) {

        try {
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionId + ".");
            }
            Game game = sessionManager.getGameSession(sessionId).getGame();
            Player player = game.getPlayerFromName(playerName);

            if (player == null) {
                throw new Exception("There is no player in this session associated with this playerID: " + sessionId + ".");
            }

            if (!auth.getNameFromToken(token).equals(playerName)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token does not match requested players name.");
            }

            JsonObject jobj = JsonParser.parseString(bodyData).getAsJsonObject();

            ArrayList<ActionResult> actionResult =
                    game.takeAction(playerName, ActionDecoder.createAction(actionIdentifier.toString(), jobj));
            if (!actionResult.contains(ActionResult.TURN_COMPLETED)) {
                throw new RuntimeException("Invalid action performed: " + actionResult.toString());
            }

            // TODO: return what further actions are needed (if any)
            // mark that the game state has changed
            gameWatcher.get(sessionId).markDirty();
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Restart an already launched game.
     *
     * @param token token of player
     * @param sessionId session id
     * @param bodyData body data in post
     * @return String (empty is OK)
     */
    @PostMapping(value = "/api/sessions/{sessionId}/restart",
            consumes = "text/plain; charset=utf-8")
    public ResponseEntity<String> restartGame(@RequestParam("access_token") String token,
                                              @PathVariable String sessionId,
                                              @RequestBody(required = false) String bodyData) {
        try {
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionId + ".");
            }
            GameSession gameSession = sessionManager.getGameSession(sessionId);
            String creatorName = gameSession.getCreatorUsername();

            if (!auth.getNameFromToken(token).equals(creatorName)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Only the creator of the game can restart the game.");
            }

            // username matches, maybe we add a vote to restart in the future?
            // recreate all info to launch a game
            GameVersions gameVersion = gameSession.getGame().getGameVersion();

            List<PlayerInfo> playersInfo = new ArrayList<>();
            for (Player p : gameSession.getGame().getPlayers()) {
                playersInfo.add(new PlayerInfo(p.getName(), p.getColour()));
            }

            Game newGame = sessionManager.launchNewGame(gameVersion, playersInfo);
            gameSession.setGame(newGame);

            logger.info("Restarted game session: " + sessionId);

            // game has updated
            gameWatcher.get(sessionId).markDirty();

            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
