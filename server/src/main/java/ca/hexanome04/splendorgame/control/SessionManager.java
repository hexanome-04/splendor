package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.model.GameSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Hold all the game sessions available.
 */
@Component
public class SessionManager {

    Map<String, GameSession> gameSessions;

    /**
     * Initialize the Session Manager.
     */
    @Autowired
    public SessionManager() {
        this.gameSessions = new HashMap<>();
    }

    /**
     * Get a game session from the session id.
     *
     * @param sessionID session id for the game
     * @return game session or null (if game session is not found)
     */
    public @Nullable GameSession getGameSession(String sessionID) {
        if (!this.gameSessions.containsKey(sessionID)) {
            return null;
        }
        return this.gameSessions.get(sessionID);
    }

}
