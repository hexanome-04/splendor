package ca.hexanome04.splendorgame.model;

import java.util.List;

/**
 * Represent a game session for a Splendor game.
 */
public class GameSession {
    private final String sessionId;
    private boolean launched;
    private SplendorGame game;
    private String creatorUsername;
    private String sessionName;

    /**
     * Initialize a game session.
     *
     * @param sessionId         Session id associated with this game session
     * @param creatorUsername   Username of creator associated with this game session
     * @param sessionName       Name of this session
     */
    public GameSession(String sessionId, String creatorUsername, String sessionName) {
        this.launched = false;
        this.sessionId = sessionId;
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
     * Set the game associated with the session.
     *
     * @param game The game associated with the session.
     */
    public void setGame(SplendorGame game) {
        this.game = game;
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
    public String getSessionId() {
        return this.sessionId;
    }

}
