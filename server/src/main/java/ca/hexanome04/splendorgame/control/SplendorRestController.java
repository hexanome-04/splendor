package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.LaunchSessionInfo;
import ca.hexanome04.splendorgame.control.templates.PlayerInfo;
import ca.hexanome04.splendorgame.model.Player;
import com.google.gson.Gson;
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

    @PutMapping(value = "/api/sessions/{sessionID}", consumes = "application/json; charset=utf-8")
    public ResponseEntity launchSession(@PathVariable String sessionID, @RequestBody LaunchSessionInfo launchSessionInfo) {

        try {
            if (launchSessionInfo == null || launchSessionInfo.gamename() == null)
                throw new Exception("Lobby Service did not specify a matching Service name.");
            if (!launchSessionInfo.gamename().equals(gameServiceName))
                throw new Exception("Lobby Service did not specify a matching Service name.");
            if (sessionManager.getGameSession(sessionID) != null)
                throw new Exception("Game can not be launched. Id is already in use.");

            // Looks good, lets create the game
            ArrayList<Player> playerList = new ArrayList<>();
            for(PlayerInfo p : launchSessionInfo.players()) {
                playerList.add(new Player(p.name(), p.colour()));
            }
            sessionManager.addSession(sessionID, playerList, launchSessionInfo.creator(), launchSessionInfo.sessionName());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            // Something went wrong. Send a http-400 and pass the exception message as body payload.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

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
