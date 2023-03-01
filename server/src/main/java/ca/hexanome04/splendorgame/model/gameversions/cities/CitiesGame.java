package ca.hexanome04.splendorgame.model.gameversions.cities;

import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.orient.*;

/**
 * Class that represents the current state of an orient + cities game.
 */
public class CitiesGame extends OrientGame {

    /**
     * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
     *
     * @param turnCounter         The turn id associated with the player.
     */
    public CitiesGame(int turnCounter) {
        super(GameVersions.BASE_ORIENT_CITIES, -1, turnCounter);
    }

}
