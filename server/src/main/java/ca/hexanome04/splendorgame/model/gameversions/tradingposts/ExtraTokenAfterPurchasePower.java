package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.TokenType;
import java.util.HashMap;

/**
 * Class that represents the extra token after purchase power.
 */
public class ExtraTokenAfterPurchasePower extends Power {

    private HashMap<TokenType, Integer> requirements = new HashMap<>() {
        {
            put(TokenType.Red, 3);
            put(TokenType.White, 1);
        }
    };

    /**
     * Creates an extra token after purchase power object.
     */
    public ExtraTokenAfterPurchasePower() {
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