package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ArrayList<NobleCard> nobleCards;
    private ArrayList<DevelopmentCard> reservedCards;
    private ArrayList<NobleCard> reservedNobles;
    private int coatsOfArms;

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
        nobleCards = new ArrayList<>();
        coatsOfArms = 5;

        for (TokenType tokenType : TokenType.values()) {
            tokens.put(tokenType, 0);
            bonuses.put(tokenType, 0);
        }
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
    public void addTokens(HashMap<TokenType, Integer> tokens) {
        tokens.forEach((key, value) -> this.tokens.put(key, this.tokens.get(key) + value));
    }

    /**
     * Adds bonuses to player inventory.
     *
     * @param bonus to add to player inventory.
     * @param numBonuses how many of this bonus to be added to player inventory.
     */
    public void addBonus(TokenType bonus, int numBonuses) {
        this.bonuses.put(bonus, bonuses.get(bonus) + numBonuses);
    }

    /**
     * Adds noble card to player inventory.
     *
     * @param nobleCard Noble to add to player inventory
     */
    public void addNoble(NobleCard nobleCard) {
        this.nobleCards.add(nobleCard);
        this.prestigePoints += 3;
    }

    /**
     * Adds prestige points to player score.
     *
     * @param prestigePoints Prestige points to add to player
     */
    public void addPrestigePoints(int prestigePoints) {
        this.prestigePoints += prestigePoints;
    }

    /**
     * Adds card to player inventory.
     *
     * @param card Card to be added to player inventory
     */
    public void addCard(DevelopmentCard card) {
        cards.add(card);
    }

    /**
     * Get a player's held tokens.
     *
     * @return list of tokens that the player has
     */
    public HashMap<TokenType, Integer> getTokens() {
        return new HashMap<>(this.tokens);
    }

    /**
     * Get a player's owned nobles.
     *
     * @return list of nobles that the player has
     */
    public ArrayList<NobleCard> getNobles() {
        return new ArrayList<>(this.nobleCards);
    }

    /**
     * Get a player's reserved cards.
     *
     * @return list of reserved cards that the player has
     */
    public List<DevelopmentCard> getReservedCards() {
        return new ArrayList<>(this.reservedCards);
    }

    /**
     * Get a player's bonuses (obtained from their owned development cards).
     *
     * @return map of player's bonuses
     */
    public Map<TokenType, Integer> getBonuses() {
        return new HashMap<>(this.bonuses);
    }

    /**
     * Removes tokens from player inventory.
     *
     * @param tokens Tokens to remove from player inventory.
     */
    public void removeTokens(HashMap<TokenType, Integer> tokens) {
        tokens.forEach((key, value) -> this.tokens.put(key, this.tokens.get(key) - value));
    }

    /**
     * Removes bonuses from player inventory.
     *
     * @param bonuses Bonuses to remove from player inventory
     */
    public void removeBonuses(HashMap<TokenType, Integer> bonuses) {
        bonuses.forEach((key, value) -> {
            Integer currentValue = this.bonuses.get(key);
            this.bonuses.put(key, currentValue - value);
        });
    }

    /**
     * Takes / puts back tokens for the player's turn.
     *
     * @param take    The tokens the player wishes to take during this turn.
     * @param putBack The tokens the player wishes to put back this turn.
     */
    public void takeTokens(HashMap<TokenType, Integer> take, HashMap<TokenType, Integer> putBack) {
        addTokens(take);
        removeTokens(putBack);
    }

    /**
     * Add card to reserved cards for a player and add one gold token to inventory (for their turn).
     *
     * @param card The development card the player would like to reserve.
     */
    public void reserveCard(DevelopmentCard card) {
        reservedCards.add(card);
    }

    /**
     * Check if player has the tokens specified in the given list.
     *
     * @param checkTokens tokens to be checked
     * @return true if player has the specified tokens
     */
    public boolean hasTokens(HashMap<TokenType, Integer> checkTokens) {

        for (TokenType key : checkTokens.keySet()) {

            if ((this.tokens.get(key) - checkTokens.get(key)) < 0) {
                return false;
            }

        }
        return true;

    }

    @Override
    public String toString() {
        return "Player name: " + name + " / Player colour: " + colour;
    }

}
