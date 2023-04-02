package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.GameSaveData;
import ca.hexanome04.splendorgame.control.templates.GameSaveInfo;
import ca.hexanome04.splendorgame.model.GameSession;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Rest controller for all API endpoints for game saves.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class GameSaveRestController {

    static final Logger LOGGER = LoggerFactory.getLogger(SplendorRestController.class);

    private final GameSavesManager gameSavesManager;
    private final GameSaveInitializer gameSaveInitializer;
    private final SessionManager sessionManager;
    private final Authentication authentication;
    @Value("${LS.server.username}")
    private String registerUsername;

    /**
     * Constructs a REST controller for game saving.
     *
     * @param authentication authentication
     * @param gameSavesManager game saves manager
     * @param gameSaveInitializer game save initializer
     * @param sessionManager session manager
     */
    public GameSaveRestController(@Autowired Authentication authentication,
                                  @Autowired GameSavesManager gameSavesManager,
                                  @Autowired GameSaveInitializer gameSaveInitializer,
                                  @Autowired SessionManager sessionManager) {
        this.authentication = authentication;
        this.gameSavesManager = gameSavesManager;
        this.gameSaveInitializer = gameSaveInitializer;
        this.sessionManager = sessionManager;
    }

    /**
     * Creates a new game save based on the session given and notifies the lobby service.
     *
     * @param token access token
     * @param sessionId session id of game
     * @param bodyData name of game save
     * @return id of new game save or error response
     */
    @PostMapping(value = "/api/saves", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createNewGameSave(@RequestParam("access_token") String token,
                                                    @RequestParam("session_id") String sessionId,
                                                    @RequestBody String bodyData) {
        try {
            GameSession gameSession = this.sessionManager.getGameSession(sessionId);
            if (gameSession == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No game session with specified id found.");
            }

            // we get the token username for setting the game save creators name
            String username = this.authentication.getNameFromToken(token);
            if (this.authentication.getNameFromToken(token).isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in.");
            }

            if (gameSession.getGame().isGameOver()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot save a game that has ended.");
            }

            // should be ok
            String gameSaveId = this.gameSavesManager.createGameSave(bodyData.trim(), username, gameSession.getGame());
            if (gameSaveId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
            }

            GameSaveInfo gameSaveInfo = this.gameSavesManager.getGameSave(gameSaveId);
            this.gameSaveInitializer.registerNewGameSave(gameSaveInfo.gameSaveData());

            return ResponseEntity.status(HttpStatus.OK).body(gameSaveId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Retrieve the list of all game saves.
     *
     * @param token access token
     * @return list of game saves
     */
    @GetMapping(value = "/api/saves")
    public ResponseEntity<String> getGameSaveInfo(@RequestParam("access_token") String token) {
        try {
            String username = this.authentication.getNameFromToken(token);
            if (username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid access token.");
            }

            Gson gson = new Gson();
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(this.gameSavesManager.getGameSaves()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("idk");
        }
    }

    /**
     * Deletes a game save and notifies the lobby service.
     *
     * @param token access token
     * @param gameSaveId game save id
     * @return success response
     */
    @DeleteMapping(value = "/api/saves/{gameSaveId}")
    public ResponseEntity<String> deleteGameSave(@RequestParam("access_token") String token,
                                                 @PathVariable String gameSaveId) {
        try {
            GameSaveInfo gameSaveInfo = this.gameSavesManager.getGameSave(gameSaveId);
            if (gameSaveInfo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No game save exists with specified id.");
            }
            GameSaveData gameSaveData = gameSaveInfo.gameSaveData();

            String tokenUsername = this.authentication.getNameFromToken(token);
            if (!tokenUsername.equals(gameSaveInfo.creator())
                    && !this.authentication.isAdminToken(token)
                    && !tokenUsername.equals(registerUsername)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Save games can only be deleted by creator, "
                        + "admin or service who registered the game service.");
            }

            // should be ok
            this.gameSaveInitializer.deleteGameSave(gameSaveData);
            this.gameSavesManager.deleteGameSave(gameSaveData.savegameid());

            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
