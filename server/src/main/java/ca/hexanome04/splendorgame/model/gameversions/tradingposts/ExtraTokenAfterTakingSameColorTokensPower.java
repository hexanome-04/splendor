package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.TokenType;
import java.util.HashMap;

/**
 * Class that represents the extra token after taking token of same color power.
 */
public class ExtraTokenAfterTakingSameColorTokensPower extends Power {

    private HashMap<TokenType, Integer> requirements = new HashMap<>() {
        {
            put(TokenType.White, 2);
        }
    };

    /**
     * Creates an extra token power object.
     */
    public ExtraTokenAfterTakingSameColorTokensPower() {
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
     * @param player player who is using this power.
     */
    @Override
    public void execute(TradingPostsPlayer player) {
    }
}