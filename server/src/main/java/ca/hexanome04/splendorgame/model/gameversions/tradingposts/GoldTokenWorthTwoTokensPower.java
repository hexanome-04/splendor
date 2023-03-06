package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.TokenType;
import java.util.HashMap;

/**
 * Class that represents the gold token worth two tokens power.
 */
public class GoldTokenWorthTwoTokensPower extends Power {

    private HashMap<TokenType, Integer> requirements = new HashMap<>() {
        {
            put(TokenType.Blue, 3);
            put(TokenType.Brown, 1);
        }
    };

    /**
     * Creates a gold token worth two tokens power object.
     */
    public GoldTokenWorthTwoTokensPower() {
        super();
    }

    /**
     * Check if player can unlock this power.
     *
     * @param player player who is unlocking this power.
     * @return true if player can unlock this power.
     */
    @Override
    public boolean conditionMet(TradingPostsPlayer player) {
        return player.hasBonuses(requirements);
    }

    /**
     * Executing this power.
     *
     * @param player player who is using this power.
     */
    @Override
    public void execute(TradingPostsPlayer player) {
    }
}