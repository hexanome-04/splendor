package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents a player.
 */
public interface Player {

    /**
     * Get player name.
     *
     * @return Returns name of player.
     */
    String getName();

    /**
     * Get player colour.
     *
     * @return Returns colour of player.
     */
    String getColour();

    /**
     * Set player name.
     *
     * @param name The player's new name.
     */
    void setName(String name);

    /**
     * Set player colour.
     *
     * @param colour The player's new colour.
     */
    void setColour(String colour);

    /**
     * Adds tokens to player inventory.
     *
     * @param tokens Tokens to add to player inventory.
     */
    void addTokens(HashMap<TokenType, Integer> tokens);

    /**
     * Adds bonuses to player inventory.
     *
     * @param bonus to add to player inventory.
     * @param numBonuses how many of this bonus to be added to player inventory.
     */
    void addBonus(TokenType bonus, int numBonuses);

    /**
     * Adds noble card to player inventory.
     *
     * @param nobleCard Noble to add to player inventory
     */
    void addNoble(NobleCard nobleCard);

    /**
     * Adds prestige points to player score.
     *
     * @param prestigePoints Prestige points to add to player
     */
    void addPrestigePoints(int prestigePoints);

    /**
     * Adds card to player inventory.
     *
     * @param card Card to be added to player inventory
     */
    void addCard(DevelopmentCard card);

    /**
     * Removes a card from player inventory.
     *
     * @param card Card to removed from player inventory
     */

    void removeCard(DevelopmentCard card);

    /**
     * Removes a reserved development card from player inventory.
     *
     * @param card Card to removed from player inventory
     */
    void removeReservedCard(DevelopmentCard card);

    /**
     * Remove a reserved noble from the players inventory.
     *
     * @param card reserved noble card
     */
    void removeReservedNoble(NobleCard card);

    /**
     * Get a player's held tokens.
     *
     * @return list of tokens that the player has
     */
    HashMap<TokenType, Integer> getTokens();

    /**
     * Get a player's owned nobles.
     *
     * @return list of nobles that the player has
     */
    ArrayList<NobleCard> getNobles();

    /**
     * Get a player's reserved cards.
     *
     * @return list of reserved cards that the player has
     */
    List<DevelopmentCard> getReservedCards();

    /**
     * Get a player's reserved nobles.
     *
     * @return list of reserved nobles that the player has
     */
    List<NobleCard> getReservedNobles();

    /**
     * Get a player's bonuses (obtained from their owned development cards).
     *
     * @return map of player's bonuses
     */
    Map<TokenType, Integer> getBonuses();

    /**
     * Removes tokens from player inventory.
     *
     * @param tokens Tokens to remove from player inventory.
     */
    void removeTokens(HashMap<TokenType, Integer> tokens);

    /**
     * Removes bonuses from player inventory.
     *
     * @param bonuses Bonuses to remove from player inventory
     */
    void removeBonuses(HashMap<TokenType, Integer> bonuses);

    /**
     * Takes / puts back tokens for the player's turn.
     *
     * @param take    The tokens the player wishes to take during this turn.
     * @param putBack The tokens the player wishes to put back this turn.
     */
    void takeTokens(HashMap<TokenType, Integer> take, HashMap<TokenType, Integer> putBack);

    /**
     * Add card to reserved cards for a player and add one gold token to inventory (for their turn).
     *
     * @param card The development card the player would like to reserve.
     */
    void reserveCard(DevelopmentCard card);

    /**
     * Add card to reserved cards for a player and add one gold token to inventory (for their turn).
     *
     * @param noble The development card the player would like to reserve.
     */
    void reserveNoble(NobleCard noble);

    /**
     * Check if player has the tokens specified in the given list.
     *
     * @param checkTokens tokens to be checked
     * @return true if player has the specified tokens
     */
    boolean hasTokens(HashMap<TokenType, Integer> checkTokens);

    /**
     * Check if player has the bonuses specified in the given list.
     *
     * @param checkBonuses bonuses to be checked
     * @return true if player has the specified bonuses
     */
    boolean hasBonuses(HashMap<TokenType, Integer> checkBonuses);

    /**
     * Check if player has at least one noble.
     *
     * @return true if player has at least one noble
     */
    boolean hasNobles();

    /**
     * Returns the arraylist of cards that a player has.
     *
     * @return the arraylist of cards that a player has
     */
    List<DevelopmentCard> getDevCards();

    /**
     * Retrieve a purchased development card from the players inventory using the card id.
     *
     * @param cid card id
     * @return development card if found
     */
    DevelopmentCard getPurchasedDevelopmentCard(String cid);

    /**
     * Retrieve a reserved development card from the players inventory using the card id.
     *
     * @param cid card id
     * @return development card if found
     */
    DevelopmentCard getReservedDevelopmentCard(String cid);

    /**
     * Returns the amount of prestige points a player has.
     *
     * @return the amount of prestige points a player has
     */
    int getPrestigePoints();

}