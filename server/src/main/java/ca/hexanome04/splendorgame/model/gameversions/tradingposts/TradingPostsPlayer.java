package ca.hexanome04.splendorgame.model.gameversions.tradingposts;

import ca.hexanome04.splendorgame.model.gameversions.orient.*;


/**
 * A concrete implementation of Player for Trading Posts game.
 */
public class TradingPostsPlayer extends OrientPlayer {

    private int coatsOfArmsUnplaced;
    /**
     * The power which take extra token after purchase.
     */
    public final ExtraTokenAfterPurchasePower extraTokenAfterPurchase;
    /**
     * The power which take extra token after taking same color token.
     */
    public final ExtraTokenAfterTakingSameColorTokensPower extraTokenAfterTakingSameColor;
    /**
     * The power of using one gold token as two for same color purchase.
     */
    public final GoldTokenWorthTwoTokensPower goldTokenWorthTwoTokens;
    /**
     * The power which add five prestige points.
     */
    public final AddFivePrestigePointsPower addFivePrestigePoints;
    /**
     * The power which add prestige points based on coats of arms.
     */
    public final AddPrestigePointsWithCoatsOfArmsPower addPrestigePointsWithCoatsOfArms;

    /**
     * Creates a Player object with a given name and colour.
     *
     * @param name   The name of the player.
     * @param colour The color associated to the player.
     */
    public TradingPostsPlayer(String name, String colour) {
        super(name, colour);

        this.coatsOfArmsUnplaced = 5;
        this.extraTokenAfterPurchase = new ExtraTokenAfterPurchasePower();
        this.extraTokenAfterTakingSameColor = new ExtraTokenAfterTakingSameColorTokensPower();
        this.goldTokenWorthTwoTokens = new GoldTokenWorthTwoTokensPower();
        this.addFivePrestigePoints = new AddFivePrestigePointsPower();
        this.addPrestigePointsWithCoatsOfArms = new AddPrestigePointsWithCoatsOfArmsPower();
    }

    /**
     * Get the number of coats of arms.
     *
     * @return number of coats of arms.
     */
    public int getCoatsOfArmsUnplaced() {
        return this.coatsOfArmsUnplaced;
    }

    /**
     * Place a coat of arms on the board.
     */
    public void placeCoatOfArms() {
        this.coatsOfArmsUnplaced--;
        if (this.addPrestigePointsWithCoatsOfArms.isUnlocked()) {
            this.addPrestigePointsWithCoatsOfArms.execute(this);
        }
    }

}
