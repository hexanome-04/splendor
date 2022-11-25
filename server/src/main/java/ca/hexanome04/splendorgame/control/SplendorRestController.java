package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.model.action.Action;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/***
 * Rest controller for all API endpoints of Splendor game.
 */
@RestController
public class SplendorRestController {

    private final SessionManager sessionManager;
    private final Authentication auth;
    private final Initializer init;

    public SplendorRestController(SessionManager sessionManager, Authentication auth, Initializer init) {
        this.sessionManager = sessionManager;
        this.auth = auth;
        this.init = init;
    }



//
//    @PutMapping(value = "/api/games/{gameId}", consumes = "application/json; charset=utf-8")
//    public ResponseEntity launchGame(@PathVariable String gameId, @RequestBody LauncherInfo launcherInfo) {
//
//        try {
//            if (launcherInfo == null || launcherInfo.getGameServer() == null)
//                throw new Exception("LauncherInfo provided by Lobby Service did not specify a matching Service name.");
//            if (!launcherInfo.getGameServer().equals(gameServiceName))
//                throw new Exception("LauncherInfo provided by Lobby Service did not specify a matching Service name.");
//            if (sessionManager.getGameSession(gameId) != null)
//                throw new Exception("Game can not be launched. Id is already in use.");
//
//            // Looks good, lets create the game on model side, create a BCM for the board.
//            sessionManager.addSession(gameId, launcherInfo.getPlayers().toArray(new Player[launcherInfo.getPlayers().size()]));
//            broadcastContentManagers.put(gameId, new BroadcastContentManager<>(gameManager.getGameById(gameId).getBoard()));
//            return ResponseEntity.status(HttpStatus.OK).build();
//        } catch (Exception e) {
//
//            // Something went wrong. Send a http-400 and pass the exception message as body payload.
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

    @GetMapping(value = "/api/sessions/{sessionId}/game", produces = "application/json; charset=utf-8")
    public ResponseEntity getGameState(@PathVariable String sessionID) {
        try {
            // Check if session exists
            if (sessionManager.getGameSession(sessionID) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionID + ".");
            }

            // If so, serialize game and place it as body in a ResponseEntity
            String serializedGameState = new Gson().toJson(sessionManager.getGameSession(sessionID).getGame());
            return ResponseEntity.status(HttpStatus.OK).body(serializedGameState);
        } catch (Exception e) {
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(value = "/api/sessions/{sessionId}/game/board", produces = "application/json; charset=utf-8")
    public ResponseEntity getGameBoard(@PathVariable String sessionID) {
        try {
            if (sessionManager.getGameSession(sessionID) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionID + ".");
            }

            String serializedPlayers = new Gson().toJson(sessionManager.getGameSession(sessionID).getGame().getPlayers());
            return ResponseEntity.status(HttpStatus.OK).body(serializedPlayers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(value = "/api/sessions/{sessionId}/game/players", produces = "application/json; charset=utf-8")
    public ResponseEntity getPlayers(@PathVariable String sessionID) {
        try {
            if (sessionManager.getGameSession(sessionID) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionID + ".");
            }

            String serializedGameBoard = new Gson().toJson(sessionManager.getGameSession(sessionID).getGame().getBoardState());
            return ResponseEntity.status(HttpStatus.OK).body(serializedGameBoard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping(value = "/api/sessions/{sessionID}/game/players/{playername}/{actionIdentifier}")
    public ResponseEntity putAction(@PathVariable String sessionID, @PathVariable String playerName, @PathVariable Action actionIdentifier) {
        try {
            if (sessionManager.getGameSession(sessionID) == null) {
                throw new Exception("There is no session associated this session ID: " + sessionID + ".");
            }
            if (sessionManager.getGameSession(sessionID).getGame().getPlayerFromName(playerName) == null) {
                throw new Exception("There is no player in this session associated with this playerID: " + sessionID + ".");
            }

            // TODO: make player complete action, figure out how to get info from message body
//            sessionManager.getGameSession(sessionID).getGame().getPlayerFromName(playerName).buyCard();


            return ResponseEntity.status(HttpStatus.OK).body();
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
