package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.gameversions.tradingposts.TradingPostsPlayer;

/**
 * Class that represents a power.
 */
public abstract class Power {

    private boolean unlocked;

    /**
     * Construct a power.
     */
    public Power() {
        this.unlocked = false;
    }

    /**
     * Let a player unlock the power.
     *
     * @param player The player unlocking the power
     */
    public void unlockPower(TradingPostsPlayer player) {
        this.unlocked = true;
        player.placeCoatOfArms();
    }

    /**
     * Check if a player has unlocked the power.
     *
     * @return true if the player has unlocked the power
     */
    public boolean isUnlocked() {
        return this.unlocked;
    }

    /**
     * Check if the player can unlock this power.
     *
     * @param player The player that uses the power
     * @return true if player can unlock the power
     */
    public abstract boolean conditionMet(TradingPostsPlayer player);

    /**
     * Use this power.
     *
     * @param player player who uses the power
     */
    public abstract void execute(TradingPostsPlayer player);

}