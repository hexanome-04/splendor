package ca.hexanome04.splendorgame.model.gameversions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class that represents the current state of a game.
 */
public interface Game {


    /**
     * Constructor for the splendorBoard, initializes all the decks with cards from a file.
     *
     * @throws FileNotFoundException If file is not found.
     */
    void createSplendorBoard() throws FileNotFoundException;

    /**
     * Initialize state of the board (cards, nobles, tokens).
     */
    void initBoard();

    /**
     * Get the current four visible tier 1 cards.
     *
     * @return list of visible tier 1 cards
     */
    List<RegDevelopmentCard> getTier1PurchasableDevelopmentCards();

    /**
     * Get the current four visible tier 2 cards.
     *
     * @return list of visible tier 2 cards
     */
    List<RegDevelopmentCard> getTier2PurchasableDevelopmentCards();

    /**
     * Get the current four visible tier 3 cards.
     *
     * @return list of visible tier 3 cards
     */
    List<RegDevelopmentCard> getTier3PurchasableDevelopmentCards();

    /**
     * Retrieve a card from the decks within the board.
     *
     * @param id card id
     * @return card from the board (could be null, if doesn't exist)
     */
    Card getCardFromId(String id);

    /**
     * Take a card from whichever deck it resides in.
     *
     * @param card card to take from the board
     * @return card taken successfully
     */
    boolean takeCard(Card card);

    /**
     * Add tokens to the board.
     *
     * @param tokensInput tokens to be added
     */
    void addTokens(Map<TokenType, Integer> tokensInput);

    /**
     * Remove tokens from the board.
     *
     * @param tokensToRemove tokens to be removed
     * @return if tokens were successfully removed (e.g. if enough tokens on board to take)
     */
    boolean removeTokens(Map<TokenType, Integer> tokensToRemove);

    /**
     * Gets the board's tokens.
     *
     * @return hashmap of tokens that the board has
     */
    HashMap<TokenType, Integer> getTokens();


    /**
     * Get the player who is playing next.
     *
     * @return the next player who is going to play.
     */
    Player incrementTurn();

    /**
     * Get the player whose turn it is.
     *
     * @return player who has to play
     */
    Player getTurnCurrentPlayer();

    /**
     * Get the turn counter at the current state.
     *
     * @return the turn counter.
     */

    int getTurnCounter();

    /**
     * Retrieve player instance from player name.
     *
     * @param name player name
     * @return player (null if not found)
     */
    Player getPlayerFromName(String name);

    /**
     * Set the players in the game.
     *
     * @param players list players to be added to game
     */
    void setPlayers(List<Player> players);

    /**
     * Retrieve all players in this game.
     *
     * @return list of all players in this game
     */
    List<Player> getPlayers();

    /**
     * Get valid actions for current player (turn is done when the list is empty).
     *
     * @return list of valid action for current player
     */
    List<Actions> getCurValidActions();

    /**
     * Get the version of this game (DLC).
     *
     * @return game version of instance
     */
    GameVersions getGameVersion();

    /**
     * Adds action to list of valid actions for current player.
     *
     * @param action being added to valid actions
     */
    void addValidAction(Actions action);

    /**
     * Removes action to list of valid actions for current player.
     *
     * @param action being removed for valid actions
     */
    void removeValidAction(Actions action);

    /**
     * Clears list of valid actions for current player.
     */
    void clearValidActions();

    /**
     * Clears list of main three valid actions for current player.
     */
    void clearMainValidActions();


    /**
     * Allows player to perform an action.
     *
     * @param playerName player performing action
     * @param action     action being executed
     * @return result of action execution
     */
    ArrayList<ActionResult> takeAction(String playerName, Action action);

    /**
     * Create an instance of player for this game version.
     *
     * @param name   player name
     * @param colour player color
     * @return player instance
     */
    Player createPlayer(String name, String colour);

    /**
     * Check for winner(s) at the end of each round.
     *
     * @return Player who is a winner (null if there are no winners at this moment)
     */
    Player checkForWin();

    /**
     * Check if a given player qualifies to win the game.
     *
     * @param player The player being checked for win qualification.
     * @return Whether the given player qualifies to win the game.
     */
    boolean canPlayerWin(Player player);

    /**
     * Add player who qualifies to win to list.
     *
     * @param player Player added to list of potential winners
     */
    void addPlayersWhoCanWin(Player player);

    /**
     * Return the winner of this game.
     *
     * @return Player who won the game
     */
    Player getWinner();

    /**
     * Returns true if the game is over and a winner is decided.
     *
     * @return if game is over
     */
    boolean isGameOver();


}
