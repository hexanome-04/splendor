package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.gameversions.orient.*;

/**
 * A concrete implementation of Player for Trading Posts game.
 */
public class TradingPostsPlayer extends OrientPlayer {

    private int coatsOfArms;

    /**
     * Creates a Player object with a given name and colour.
     *
     * @param name   The name of the player.
     * @param colour The color associated to the player.
     */
    public TradingPostsPlayer(String name, String colour) {
        super(name, colour);

        this.coatsOfArms = 5;
    }

}
