package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.model.GameSession;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.gameversions.GameBaseOrient;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import java.io.*;
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
     * @param version version of the game (which expansions)
     * @return game session instance
     * @throws Exception if issue occurred
     */
    public GameSession addSession(String sessionId, ArrayList<Player> players,
                                  String creatorName, String sessionName, GameVersions version) throws Exception {
        // Refuse creation if session with this ID already exists
        if (gameSessions.containsKey(sessionId)) {
            throw new Exception("Game can not be created, the requested ID " + sessionId + " is already in use.");
        }
        // Refuse creation if incorrect number of players in session
        if (players.size() < 2 || players.size() > 4) {
            throw new Exception("Game can not be created, 2-4 players required");
        }

        GameSession session = new GameSession(sessionId, creatorName, sessionName);
        InputStream is = ResourceUtils.getURL("classpath:cards.csv").openStream();

        // TODO: put in other cases once classes made
        switch (version) {
            case BASE_ORIENT -> session.setGame(new GameBaseOrient(15, players, 0, is));
            //case BASE_ORIENT_CITIES -> ;
            //case BASE_ORIENT_TRADE_ROUTES -> ;
            default -> session.setGame(new GameBaseOrient(15, players, 0, is));
        }

        gameSessions.put(sessionId, session);
        return session;
    }

}
