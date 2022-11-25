package ca.hexanome04.splendorgame.model;

import java.util.List;

public class GameSession {
    private final String sessionID;
    private boolean launched;
    private SplendorGame game;
    private String creatorUsername;
    private String sessionName;

    /**
     * Initialize a game session.
     *
     * @param sessionID session id associate with this game session
     */
    public GameSession(String sessionID, String creatorUsername, String sessionName) {
        this.launched = false;
        this.sessionID = sessionID;
        this.game = null;
        this.creatorUsername = creatorUsername;
        this.sessionName = sessionName;
    }

    /**
     * Launch a session.
     *
     * @param players players playing
     */
    public void launchSession(List<Player> players) {
        // TODO: incomplete (more args?)
    }

    /**
     * Get the game associated with the session.
     *
     * @return game associated with this session
     */
    public SplendorGame getGame() {
        return game;
    }

    /**
     * Check if the game has launched yet.
     *
     * @return has game launched
     */
    public boolean hasGameLaunched() {
        return this.launched;
    }

    /**
     * Get this game's session id.
     *
     * @return session id
     */
    public String getSessionID() {
        return this.sessionID;
    }

}
