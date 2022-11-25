package ca.hexanome04.splendorgame.control;

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

    @GetMapping(value = "/api/session/{sessionId}/players", produces = "application/json; charset=utf-8")
    public ResponseEntity getPlayers(@PathVariable String sessionID) {

        try {
            // Check if session exists
            if (sessionManager.getGameSession(sessionID) == null) {
                throw new Exception("There is no session associated " + sessionID + ".");
            }

            // If so, serialize board and place it as body in a ResponseEntity (Http-OK).
            String serializedPlayers = new Gson().toJson(sessionManager.getGameSession(sessionID).getGame().getPlayers());
            return ResponseEntity.status(HttpStatus.OK).body(serializedPlayers);
        } catch (Exception e) {
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
