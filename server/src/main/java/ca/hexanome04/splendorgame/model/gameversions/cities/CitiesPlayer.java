package ca.hexanome04.splendorgame.model.gameversions.cities;

import ca.hexanome04.splendorgame.model.gameversions.orient.*;

/**
 * A concrete implementation of Player for Cities game.
 */
public class CitiesPlayer extends OrientPlayer {

    /**
     * Creates a Player object with a given name and colour.
     *
     * @param name   The name of the player.
     * @param colour The color associated to the player.
     */
    public CitiesPlayer(String name, String colour) {
        super(name, colour);
    }

}
