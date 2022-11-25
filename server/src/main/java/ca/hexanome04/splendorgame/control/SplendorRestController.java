package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.LaunchSessionInfo;
import ca.hexanome04.splendorgame.control.templates.PlayerInfo;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorGame;
import ca.hexanome04.splendorgame.model.action.ActionDecoder;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/***
 * Rest controller for all API endpoints of Splendor game.
 */
@RestController
public class SplendorRestController {

    @Value("${gs.name}")
    private String gameServiceName;
    private final SessionManager sessionManager;
    private final Authentication auth;

    public SplendorRestController(@Autowired SessionManager sessionManager,
                                  @Autowired Authentication auth) {
        this.sessionManager = sessionManager;
        this.auth = auth;
    }

    @PutMapping(value = "/api/sessions/{sessionId}", consumes = "application/json; charset=utf-8")
    public ResponseEntity launchSession(@PathVariable String sessionId, @RequestBody LaunchSessionInfo launchSessionInfo) {

        try {
            if (launchSessionInfo == null || launchSessionInfo.gamename() == null)
                throw new Exception("Lobby Service did not specify a matching Service name.");
            if (!launchSessionInfo.gamename().equals(gameServiceName))
                throw new Exception("Lobby Service did not specify a matching Service name.");
            if (sessionManager.getGameSession(sessionId) != null)
                throw new Exception("Game can not be launched. Id is already in use.");

            // Looks good, lets create the game
            ArrayList<Player> playerList = new ArrayList<>();
            for(PlayerInfo p : launchSessionInfo.players()) {
                playerList.add(new Player(p.name(), p.colour()));
            }
            sessionManager.addSession(sessionId, playerList, launchSessionInfo.creator(), launchSessionInfo.sessionName());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

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

    @GetMapping(value = "/api/sessions/{sessionId}/game/board", produces = "application/json; charset=utf-8")
    public ResponseEntity getGameBoard(@PathVariable String sessionId) {
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

    @GetMapping(value = "/api/sessions/{sessionId}/game/players", produces = "application/json; charset=utf-8")
    public ResponseEntity getPlayers(@PathVariable String sessionId) {
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

    @PutMapping(value = "/api/sessions/{sessionId}/game/players/{playerName}/{actionIdentifier}", consumes = "application/json; charset=utf-8")
    public ResponseEntity<String> putAction(@RequestParam("token") String token, @PathVariable String sessionId,
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

            if(!auth.getNameFromToken(token).equals(playerName)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token does not match requested players name.");
            }

            JsonObject jobj = JsonParser.parseString(bodyData).getAsJsonObject();

            // TODO: make player complete action, figure out how to get info from message body

            ActionResult actionResult = ActionDecoder.createAction(actionIdentifier.toString(), jobj).executeAction(game, player);
            if(actionResult != ActionResult.VALID_ACTION) {
                throw new RuntimeException("Invalid action performed.");
            }

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
