package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.TokenType;
import java.util.HashMap;

/**
 * Class that represents the add prestige points according to coat of arms power.
 */
public class AddPrestigePointsWithCoatsOfArmsPower extends Power {

    private int currentAddedPoints = 0;
    private HashMap<TokenType, Integer> requirements = new HashMap<>() {
        {
            put(TokenType.Brown, 3);
        }
    };


    /**
     * Creates a new add prestige according to coat of arms power object.
     */
    public AddPrestigePointsWithCoatsOfArmsPower() {
        super();
    }

    /**
     * Check if the player can unlock this power.
     *
     * @param player player who is unlocking this power.
     * @return true if player can unlock this power.
     */
    @Override
    public boolean conditionMet(TradingPostsPlayer player) {
        return player.hasBonuses(requirements);
    }

    /**
     * Executing the power.
     *
     * @param player player who is executing this power.
     */
    @Override
    public void execute(TradingPostsPlayer player) {
        // amount of coat of arms in the hand of the player
        int coatsOfArmsInHand = player.getCoatsOfArmsUnplaced();
        if (currentAddedPoints < 5) {
            player.addPrestigePoints(5 - coatsOfArmsInHand - currentAddedPoints);
            currentAddedPoints = 5 - coatsOfArmsInHand - currentAddedPoints;
        }
    }
}