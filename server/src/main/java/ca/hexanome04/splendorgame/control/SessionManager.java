package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.model.GameSession;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorBoard;
import ca.hexanome04.splendorgame.model.SplendorGame;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

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
     * @param sessionId session id for the game
     * @return game session or null (if game session is not found)
     */
    public @Nullable GameSession getGameSession(String sessionId) {
        if (!this.gameSessions.containsKey(sessionId)) {
            return null;
        }
        return this.gameSessions.get(sessionId);
    }

    /**
     * Gets the number of sessions created in this session manager.
     *
     * @return The number of sessions
     */
    public int getNumSessions() {
        return gameSessions.size();
    }

    /**
     * Add a session to the session manager.
     *
     * @param sessionId session id
     * @param players player in session
     * @param creatorName creator name
     * @param sessionName session name
     * @return game session instance
     * @throws Exception if issue occurred
     */
    public GameSession addSession(String sessionId, ArrayList<Player> players,
                                  String creatorName, String sessionName) throws Exception {
        // Refuse creation if session with this ID already exists
        if (gameSessions.containsKey(sessionId)) {
            throw new Exception("Game can not be created, the requested ID " + sessionId + " is already in use.");
        }
        // Refuse creation if incorrect number of players in session
        if (players.size() < 2 || players.size() > 4) {
            throw new Exception("Game can not be created, 2-4 players required");
        }

        // TODO: get session ID, name, and creator username from somewhere
        GameSession session = new GameSession(sessionId, creatorName, sessionName);
        String filename = "";
        File file = ResourceUtils.getFile("classpath:cards.csv");
        filename = file.getAbsolutePath();

        session.setGame(new SplendorGame(new SplendorBoard(filename), 15, players, 0));
        gameSessions.put(sessionId, session);
        return session;
    }

}
