package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesGame;
import ca.hexanome04.splendorgame.model.gameversions.cities.CitiesPlayer;
import ca.hexanome04.splendorgame.model.gameversions.orient.*;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsGame;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Utilities to create/load game for testing.
 */
public class GameUtils {

    final static Logger logger = LoggerFactory.getLogger(GameUtils.class);

    /**
     * Create a game from a file (which describes the cards).
     *
     * @param prestigePointsToWin prestige points to win the game
     * @param numberOfPlayers number of players to be added
     * @return a game instance of splendor
     * @throws FileNotFoundException if file does not exist
     */
    public static OrientGame createNewOrientGame(int prestigePointsToWin, int numberOfPlayers) throws FileNotFoundException {

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new OrientPlayer("Player" + (i + 1), "Colour" + (i + 1)));
        }

        OrientGame game = new OrientGame(prestigePointsToWin, 0);

        game.setPlayers(players);
        game.createSplendorBoard();
        game.initBoard();

        return game;
    }

    /**
     * Create a cities game from a file (which describes the cards).
     *
     * @param numberOfPlayers number of players to be added
     * @return a game instance of splendor
     * @throws FileNotFoundException if file does not exist
     */
    public static CitiesGame createNewCitiesGame(int numberOfPlayers) throws FileNotFoundException {

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new CitiesPlayer("Player" + (i + 1), "Colour" + (i + 1)));
        }

        CitiesGame game = new CitiesGame(0);

        game.setPlayers(players);
        game.createSplendorBoard();
        game.initBoard();

        return game;
    }

    public static TradingPostsGame createNewTradingPostGame(int prestigePointsToWin, int numberOfPlayers) throws FileNotFoundException {

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new TradingPostsPlayer("Player" + (i + 1), "Colour" + (i + 1)));
        }

        TradingPostsGame game = new TradingPostsGame(prestigePointsToWin, 0);

        game.setPlayers(players);
        game.createSplendorBoard();
        game.initBoard();

        return game;
    }

}
