package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.model.GameSession;
import ca.hexanome04.splendorgame.model.Player;
import ca.hexanome04.splendorgame.model.SplendorBoard;
import ca.hexanome04.splendorgame.model.SplendorGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
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

    public GameSession addSession(String sessionID, ArrayList<Player> players, String creatorName, String sessionName) throws Exception {
        // Refuse creation if session with this ID already exists
        if (gameSessions.containsKey(sessionID))
            throw new Exception("Game can not be created, the requested ID " + sessionID + "is already in use.");
        // Refuse creation if incorrect number of players in session
        if (players.size() < 2 || players.size() > 4) {
            throw new Exception("Game can not be created, 2-4 players required");
        }

        // TODO: get session ID, name, and creator username from somewhere
        GameSession session = new GameSession(sessionID, creatorName, sessionName);
        String filename = "";
        File file = ResourceUtils.getFile("classpath:cards.csv");
        filename = file.getAbsolutePath();

        session.setGame(new SplendorGame(new SplendorBoard(filename),15, players, 0));
        gameSessions.put(sessionID, session);
        return session;
    }

}
