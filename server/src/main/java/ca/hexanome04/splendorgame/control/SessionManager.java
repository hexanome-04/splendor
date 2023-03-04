package ca.hexanome04.splendorgame.control;

import ca.hexanome04.splendorgame.control.templates.*;
import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.gameversions.*;
import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesGame;
import ca.hexanome04.splendorgame.model.gameversions.orient.OrientGame;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsGame;
import java.io.*;
import java.util.*;
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
    public GameSession addSession(String sessionId, List<PlayerInfo> players,
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
        session.setGame(this.launchNewGame(version, players));

        gameSessions.put(sessionId, session);
        return session;
    }

    /**
     * Create a new game with the specified information and launch it.
     *
     * @param version game version
     * @param players list of player infos
     * @return newly created game
     * @throws Exception thrown if issue occurred
     */
    public Game launchNewGame(GameVersions version, List<PlayerInfo> players) throws Exception {

        InputStream is = ResourceUtils.getURL("classpath:cards.csv").openStream();

        Game game = switch (version) {
            case BASE_ORIENT -> new OrientGame(15, 0);
            case BASE_ORIENT_CITIES -> new CitiesGame(0);
            case BASE_ORIENT_TRADE_ROUTES -> new TradingPostsGame(15, 0);
            default -> throw new RuntimeException("Invalid game version selected!");
        };

        List<Player> playerList = new ArrayList<>();
        for (PlayerInfo p : players) {
            HashMap<TokenType, Integer> defaultTokens = new HashMap<>();

            defaultTokens.put(TokenType.Green, 3);
            defaultTokens.put(TokenType.White, 3);
            defaultTokens.put(TokenType.Blue, 3);
            defaultTokens.put(TokenType.Brown, 3);
            defaultTokens.put(TokenType.Red, 3);

            Player newPlayer = game.createPlayer(p.name(), p.colour());

            newPlayer.addTokens(defaultTokens);
            playerList.add(newPlayer);
        }
        game.setPlayers(playerList);
        game.createSplendorBoard(is);
        game.initBoard();

        return game;
    }


}
