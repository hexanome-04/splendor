package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.TokenType;
import java.util.HashMap;

/**
 * Class that represents the add five prestige points power.
 */
public class AddFivePrestigePointsPower extends Power {

    private boolean isUsed = false;
    private HashMap<TokenType, Integer> requirements = new HashMap<>() {
        {
            put(TokenType.Green, 5);
        }
    };

    /**
     * Creates a new add five prestige points power object.
     */
    public AddFivePrestigePointsPower() {
        super();
    }

    /**
     * Checking if the player can unlock this power.
     *
     * @param player player who is unlocking this power.
     * @return true if player can unlock this power.
     */
    @Override
    public boolean conditionMet(TradingPostsPlayer player) {
        return (player.hasBonuses(requirements) && player.hasNobles());
    }

    /**
     * Executing the power.
     *
     * @param player player who is using this power.
     */
    @Override
    public void execute(TradingPostsPlayer player) {
        if (!isUsed) {
            player.addPrestigePoints(5);
            this.isUsed = true;
        }
    }
}