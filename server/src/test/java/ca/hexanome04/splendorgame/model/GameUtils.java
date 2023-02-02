package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.gameversions.GameBaseOrient;
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
    public static GameBaseOrient createNewGameFromFile(int prestigePointsToWin, int numberOfPlayers) throws FileNotFoundException {
        // get the test board file
        String filename = "";
        File file = ResourceUtils.getFile("classpath:testBoard.csv");
        filename = file.getAbsolutePath();

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new Player("Player" + (i + 1), "Colour" + (i + 1)));
        }

        return new GameBaseOrient(prestigePointsToWin, players, 0, new FileInputStream(filename));
    }

}
