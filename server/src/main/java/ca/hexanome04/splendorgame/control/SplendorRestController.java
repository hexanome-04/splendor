package ca.hexanome04.splendorgame.control;

import static ca.hexanome04.splendorgame.model.gameversions.GameVersions.BASE_ORIENT;

import ca.hexanome04.splendorgame.control.templates.LaunchSessionInfo;
import ca.hexanome04.splendorgame.control.templates.PlayerInfo;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.TokenType;
import ca.hexanome04.splendorgame.model.action.ActionDecoder;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.Game;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    /**
     * Create an instance of Splendor's REST controller.
     *
     * @param sessionManager session manager
     * @param auth methods relating to authentication with LS
     * @param gameServiceName game service name
     */

    public SplendorRestController(@Autowired SessionManager sessionManager,
                                  @Autowired Authentication auth,
                                  @Value("${gs.name}") String gameServiceName) {
        this.sessionManager = sessionManager;
        this.auth = auth;
        this.gameServiceName = gameServiceName;
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
            if (!launchSessionInfo.gameServer().equals(gameServiceName)) {
                throw new Exception("Lobby Service did not specify a matching Service name.");
            }
            if (sessionManager.getGameSession(sessionId) != null) {
                throw new Exception("Game can not be launched. Id is already in use.");
            }

            // Looks good, lets create the game
            ArrayList<Player> playerList = new ArrayList<>();
            for (PlayerInfo p : launchSessionInfo.players()) {
                HashMap<TokenType, Integer> defaultTokens = new HashMap<>();

                defaultTokens.put(TokenType.Green, 3);
                defaultTokens.put(TokenType.White, 3);
                defaultTokens.put(TokenType.Blue, 3);
                defaultTokens.put(TokenType.Brown, 3);
                defaultTokens.put(TokenType.Red, 3);

                Player newPlayer = new Player(p.name(), p.colour());

                newPlayer.addTokens(defaultTokens);
                playerList.add(newPlayer);
            }
            sessionManager.addSession(sessionId, playerList, launchSessionInfo.creator(), launchSessionInfo.savegame(),
                    BASE_ORIENT);
            logger.info("Launched new game session: " + sessionId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.error("Unable to launch game session: " + e.getMessage());
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Obtain the game state of specified session.
     *
     * @param sessionId session id to get game state of
     * @return JSON object of game state
     */
    @GetMapping(value = "/api/sessions/{sessionId}/game", produces = "application/json; charset=utf-8")
    public ResponseEntity getGameState(@PathVariable String sessionId) {
        try {
            // Check if session exists
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionId + ".");
            }

            // If so, serialize game and place it as body in a ResponseEntity
            String serializedGameState = new Gson().toJson(sessionManager.getGameSession(sessionId).getGame());
            return ResponseEntity.status(HttpStatus.OK).body(serializedGameState);
        } catch (Exception e) {
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get players in the specified game session.
     *
     * @param sessionId game session id
     * @return JSON object of players
     */
    @GetMapping(value = "/api/sessions/{sessionId}/game/players", produces = "application/json; charset=utf-8")
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
     * Get game board of specified session.
     *
     * @param sessionId session id
     * @return JSON object of game board
     */
    @GetMapping(value = "/api/sessions/{sessionId}/game/board", produces = "application/json; charset=utf-8")
    public ResponseEntity getGameBoard(@PathVariable String sessionId) {
        try {
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionId + ".");
            }

            // TODO: do better information hiding
            String serializedGameBoard = new Gson().toJson(sessionManager.getGameSession(sessionId).getGame());
            return ResponseEntity.status(HttpStatus.OK).body(serializedGameBoard);
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
    @GetMapping(value = "/api/sessions/{sessionId}/game/players/{playerName}/actions",
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
            String[] actions = { Actions.BUY_CARD.toString() };
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
    @PutMapping(value = "/api/sessions/{sessionId}/game/players/{playerName}/actions/{actionIdentifier}",
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
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token does not match requested players name.");
            }

            JsonObject jobj = JsonParser.parseString(bodyData).getAsJsonObject();

            ActionResult actionResult =
                    game.takeAction(playerName, ActionDecoder.createAction(actionIdentifier.toString(), jobj));
            if (actionResult != ActionResult.VALID_ACTION) {
                throw new RuntimeException("Invalid action performed: " + actionResult.toString());
            }

            // TODO: return what further actions are needed (if any)
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
