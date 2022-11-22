package ca.hexanome04.splendorgame.model;

/**
 * Class representing the noble cards.
 */
public class NobleCard implements CardType {
    private final int prestigePoints;

    /**
     * NobleCard constructor that instantiate a noble card on the board when it is drawn.
     * 
     * @param prestigePoints Amount of prestige points associated with this noble.
     */

    public NobleCard(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }




}

