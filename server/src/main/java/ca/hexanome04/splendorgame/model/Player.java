package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that represents a player.
 */
public class Player {

    private String name;
    private String colour;

    private int prestigePoints;
    private HashMap<TokenType, Integer> tokens;
    private HashMap<TokenType, Integer> bonuses;
    private ArrayList<DevelopmentCard> cards;
    private ArrayList<DevelopmentCard> reservedCards;
    private ArrayList<NobleCard> reservedNobles;

    /**
     * Creates a Player object with a given name and colour.
     *
     * @param name   The name of the player.
     * @param colour The color associated to the player.
     */
    public Player(String name, String colour) {
        this.name = name;
        this.colour = colour;
        prestigePoints = 0;
        tokens = new HashMap<>();
        bonuses = new HashMap<>();
        cards = new ArrayList<>();
        reservedCards = new ArrayList<>();
        reservedNobles = new ArrayList<>();
    }

    /**
     * Get player name.
     *
     * @return Returns name of player.
     */
    public String getName() {
        return name;
    }

    /**
     * Get player colour.
     *
     * @return Returns colour of player.
     */
    public String getColour() {
        return colour;
    }

    /**
     * Set player name.
     *
     * @param name The player's new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set player colour.
     *
     * @param colour The player's new colour.
     */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Adds tokens to player inventory.
     *
     * @param tokens Tokens to add to player inventory.
     */
    private void addTokens(ArrayList<Token> tokens) {
        for (Token t : tokens) {
            Integer currentValue = this.tokens.get(t.getType());
            this.tokens.put(t.getType(), currentValue + 1);
        }
    }

    /**
     * Removes tokens from player inventory.
     *
     * @param tokens Tokens to remove from player inventory.
     */
    private void removeTokens(ArrayList<TokenType> tokens) {
        for (TokenType t : tokens) {
            // TODO: make this put tokens back in main deck instead of deleting them
            Integer currentValue = this.tokens.get(t);
            this.tokens.put(t, currentValue - 1);
        }
    }

    /**
     * Removes bonuses from player inventory.
     *
     * @param bonuses Bonuses to remove from player inventory
     */
    private void removeBonuses(ArrayList<TokenType> bonuses) {
        for (TokenType t : bonuses) {
            Integer currentValue = this.bonuses.get(t);
            this.bonuses.put(t, currentValue - 1);
        }
    }

    /**
     * Takes / puts back tokens for the player's turn.
     *
     * @param take    The tokens the player wishes to take during this turn.
     * @param putBack The tokens the player wishes to put back this turn.
     */
    public void takeTokens(ArrayList<Token> take, ArrayList<TokenType> putBack) {
        addTokens(take);
        removeTokens(putBack);
    }

    /**
     * Adds purchased card to inventory and removes cost from player (for their turn).
     *
     * @param card The development card the player would like to purchase.
     */
    public void buyCard(DevelopmentCard card) {
        cards.add(card);
        bonuses.put(card.getTokenType(), card.getBonus());
        prestigePoints += card.getPrestigePoints();

        // remove card cost (token or bonus burn) from player
        if (card.getCostType() == CostType.Token) {
            removeTokens(card.getTokenCost());
        }

        // TODO: handle orient double gold token cards later
        if (card.getClass().equals(OrientDevelopmentCard.class)) {
            OrientDevelopmentCard orientCard = (OrientDevelopmentCard) card;
            if (orientCard.getCostType() == CostType.Bonus) {
                removeBonuses(orientCard.getBurnBonusCost());
            }
            if (orientCard.getReserveNoble()) {
                reserveNoble();
            }
            if (orientCard.isCascade()) {
                cascadeChooseCard();
            }
            // TODO: handle satchel cards
        }

    }

    /**
     * Add card to reserved cards for a player and add one gold token to inventory (for their turn).
     *
     * @param card The development card the player would like to reserve.
     */
    public void reserveCard(DevelopmentCard card) {
        // TODO: must handle attempt to reserve more than 3 cards
        reservedCards.add(card);

        int currentValue = tokens.get(TokenType.Gold);
        tokens.put(TokenType.Gold, currentValue + 1);
    }

    /**
     * Private method to reserve noble, called only when orient reserveNoble card bought.
     * */
    private void reserveNoble() {
        // TODO: prompt user to select noble to reserve (once controllers implemented?)
        // NobleCard noble = ;
        // reservedNobles.add(noble);
    }

    /**
     * Private method to select cascade card, called only when orient cascade card bought.
     * */
    private void cascadeChooseCard() {
        // TODO: prompt user to select card at appropriate tier (once controllers implemented?)
        // OrientDevelopmentCard cascadeCard = ;
        // cards.add(cascadeCard);
    }

    @Override
    public String toString() {
        return "Player name: " + name + " / Player colour: " + colour;
    }

}
