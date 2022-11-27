package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.LaunchSessionInfo;
import ca.hexanome04.splendorgame.control.templates.PlayerInfo;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorGame;
import ca.hexanome04.splendorgame.model.Token;
import ca.hexanome04.splendorgame.model.TokenType;
import ca.hexanome04.splendorgame.model.action.ActionDecoder;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for all API endpoints of Splendor game.
 */
@RestController
public class SplendorRestController {

    final Logger logger = LoggerFactory.getLogger(SplendorRestController.class);

    @Value("${gs.name}")
    private String gameServiceName;
    private final SessionManager sessionManager;
    private final Authentication auth;

    /**
     * Create an instance of Splendor's REST controller.
     *
     * @param sessionManager session manager
     * @param auth methods relating to authentication with LS
     */
    public SplendorRestController(@Autowired SessionManager sessionManager,
                                  @Autowired Authentication auth) {
        this.sessionManager = sessionManager;
        this.auth = auth;
    }

    /**
     * Launch a session (only from lobby service).
     *
     * @param sessionId session id for new session
     * @param launchSessionInfo information about the new session
     * @return HTTP response entity
     */
    @PutMapping(value = "/api/games/{sessionId}", consumes = "application/json; charset=utf-8")
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
                Player newPlayer = new Player(p.name(), p.colour());
                ArrayList<Token> defaultTokens = new ArrayList<>();

                for (int i = 0; i < 3; i++) {
                    defaultTokens.add(new Token(TokenType.Green));
                    defaultTokens.add(new Token(TokenType.White));
                    defaultTokens.add(new Token(TokenType.Blue));
                    defaultTokens.add(new Token(TokenType.Brown));
                    defaultTokens.add(new Token(TokenType.Red));
                }

                newPlayer.addTokens(defaultTokens);
                playerList.add(newPlayer);
            }
            sessionManager.addSession(sessionId, playerList, launchSessionInfo.creator(), launchSessionInfo.savegame());
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

            String serializedGameBoard = new Gson().toJson(sessionManager.getGameSession(sessionId).getGame().getBoardState());
            return ResponseEntity.status(HttpStatus.OK).body(serializedGameBoard);
        } catch (Exception e) {
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
    @PutMapping(value = "/api/sessions/{sessionId}/game/players/{playerName}/{actionIdentifier}",
                consumes = "application/json; charset=utf-8")
    public ResponseEntity<String> putAction(@RequestParam("access_token") String token, @PathVariable String sessionId,
                                    @PathVariable String playerName, @PathVariable Actions actionIdentifier,
                                    @RequestBody String bodyData) {

        try {
            if (sessionManager.getGameSession(sessionId) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionId + ".");
            }
            SplendorGame game = sessionManager.getGameSession(sessionId).getGame();
            Player player = game.getPlayerFromName(playerName);

            if (player == null) {
                throw new Exception("There is no player in this session associated with this playerID: " + sessionId + ".");
            }

            if (!auth.getNameFromToken(token).equals(playerName)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token does not match requested players name.");
            }

            JsonObject jobj = JsonParser.parseString(bodyData).getAsJsonObject();

            // TODO: make player complete action, figure out how to get info from message body

            ActionResult actionResult = ActionDecoder.createAction(actionIdentifier.toString(), jobj).execute(game, player);
            if (actionResult != ActionResult.VALID_ACTION) {
                throw new RuntimeException("Invalid action performed: " + actionResult.toString());
            }

            // update game state
            game.incrementTurn();

            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Mappings needed
    // - launch game (ls to server)
    // - update game state (/api/sessions/{sessionId}/
    // - get gameBoard
    // - select action (POST)
    // -

}