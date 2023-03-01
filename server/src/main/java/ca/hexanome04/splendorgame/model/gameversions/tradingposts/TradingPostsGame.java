package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.orient.*;

/**
 * Class that represents the current state of an orient + trading posts game.
 */
public class TradingPostsGame extends OrientGame {

    /**
     * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
     *
     * @param prestigePointsToWin The amount of prestige points needed to win the game.
     * @param turnCounter         The turn id associated with the player.
     */
    public TradingPostsGame(int prestigePointsToWin, int turnCounter) {
        super(GameVersions.BASE_ORIENT_TRADE_ROUTES, prestigePointsToWin, turnCounter);
    }

}
