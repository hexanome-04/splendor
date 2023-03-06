package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.orient.*;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsGame;
import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
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
    public static OrientGame createNewOrientGameFromFile(int prestigePointsToWin, int numberOfPlayers) throws FileNotFoundException {
        // get the test board file
        String filename = "";
        File file = ResourceUtils.getFile("classpath:testBoard.csv");
        filename = file.getAbsolutePath();

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new OrientPlayer("Player" + (i + 1), "Colour" + (i + 1)));
        }

        OrientGame game = new OrientGame(prestigePointsToWin, 0);

        game.setPlayers(players);
        game.createSplendorBoard(new FileInputStream(filename));
        game.initBoard();

        return game;
    }

    public static TradingPostsGame createNewTradingPostGameFromFile(int prestigePointsToWin, int numberOfPlayers) throws FileNotFoundException {
        // get the test board file
        String filename = "";
        File file = ResourceUtils.getFile("classpath:testBoard.csv");
        filename = file.getAbsolutePath();

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new TradingPostsPlayer("Player" + (i + 1), "Colour" + (i + 1)));
        }

        TradingPostsGame game = new TradingPostsGame(prestigePointsToWin, 0);

        game.setPlayers(players);
        game.createSplendorBoard(new FileInputStream(filename));
        game.initBoard();

        return game;
    }

}
