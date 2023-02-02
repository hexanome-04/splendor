package ca.hexanome04.splendorgame.model.gameversions;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class that represents the current state of a game.
 */
public abstract class Game {

    /** List of players in game. */
    protected List<Player> players = new ArrayList<>();
    /** Prestige points in game. */
    protected final int prestigePointsToWin;
    /** Counter keeping track of player turns. */
    protected int turnCounter;

    /** Tier 1 deck. */
    protected final Deck<RegDevelopmentCard> tier1Deck = new Deck<>();
    /** Tier 2 deck. */
    protected final Deck<RegDevelopmentCard> tier2Deck = new Deck<>();
    /** Tier 3 deck. */
    protected final Deck<RegDevelopmentCard> tier3Deck = new Deck<>();

    /** Tokens on the board. */
    protected final HashMap<TokenType, Integer> tokens = new HashMap<>();

    /**
     * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
     *
     * @param prestigePointsToWin       The amount of prestige points needed to win the game.
     * @param players                   The player order in the game.
     * @param turnCounter               The turn id associated with the player.
     * @param is                        Input stream for file.
     * @throws FileNotFoundException    If file is not found.
     */
    public Game(int prestigePointsToWin, List<Player> players, int turnCounter, InputStream is)
            throws FileNotFoundException {
        this.prestigePointsToWin = prestigePointsToWin;
        this.players.addAll(players);
        this.turnCounter = turnCounter;
        createSplendorBoard(is);
        initBoard();
    }

    /**
     * Constructor for the splendorBoard, initializes all the decks with cards from a file.
     *
     * @param inputStream input stream for cards csv
     */
    public abstract void createSplendorBoard(InputStream inputStream);

    /**
     * Initialize state of the board (cards, nobles, tokens).
     */
    public abstract void initBoard();

    /**
     * Get the current four visible tier 1 cards.
     *
     * @return list of visible tier 1 cards
     */
    public List<RegDevelopmentCard> getTier1PurchasableDevelopmentCards() {
        return this.tier1Deck.getVisibleCards();
    }

    /**
     * Get the current four visible tier 2 cards.
     *
     * @return list of visible tier 2 cards
     */
    public List<RegDevelopmentCard> getTier2PurchasableDevelopmentCards() {
        return this.tier2Deck.getVisibleCards();
    }

    /**
     * Get the current four visible tier 3 cards.
     *
     * @return list of visible tier 3 cards
     */
    public List<RegDevelopmentCard> getTier3PurchasableDevelopmentCards() {
        return this.tier3Deck.getVisibleCards();
    }

    /**
     * Retrieve a card from the decks within the board.
     *
     * @param id card id
     * @return card from the board (could be null, if doesn't exist)
     */
    public abstract Card getCardFromId(String id);

    /**
     * Take a card from whichever deck it resides in.
     *
     * @param card card to take from the board
     * @return card taken successfully
     */
    public abstract boolean takeCard(Card card);

    /**
     * Add tokens to the board.
     *
     * @param tokensInput tokens to be added
     */
    public void addTokens(Map<TokenType, Integer> tokensInput) {
        for (Map.Entry<TokenType, Integer> entry : tokensInput.entrySet()) {
            TokenType tokenType = entry.getKey();
            int amount = entry.getValue();

            if (this.tokens.containsKey(tokenType) && amount > 0) {
                this.tokens.put(tokenType, this.tokens.get(tokenType) + amount);
            }
        }
    }

    /**
     * Remove tokens from the board.
     *
     * @param tokensToRemove tokens to be removed
     * @return if tokens were successfully removed (e.g. if enough tokens on board to take)
     */
    public boolean removeTokens(Map<TokenType, Integer> tokensToRemove) {
        for (Map.Entry<TokenType, Integer> entry : tokensToRemove.entrySet()) {
            int tokensLeft = tokens.get(entry.getKey()) - entry.getValue();

            // check for taking too many tokens
            if (tokensLeft < 0) {
                return false;
            }
            this.tokens.put(entry.getKey(), tokensLeft);
        }
        return true;
    }

    /**
     * Gets the board's tokens.
     *
     * @return hashmap of tokens that the board has
     */
    public HashMap<TokenType, Integer> getTokens() {
        return new HashMap<>(this.tokens);
    }


    /**
     * Get the player who is playing next.
     *
     * @return the next player who is going to play.
     */
    public Player incrementTurn() {
        if (turnCounter == this.players.size() - 1) {
            turnCounter = 0;
        } else {
            turnCounter++;
        }

        return players.get(turnCounter);
    }

    /**
     * Get the player whose turn it is.
     *
     * @return player who has to play
     */
    public Player getTurnCurrentPlayer() {
        return players.get(turnCounter);
    }

    /**
     * Get the turn counter at the current state.
     *
     * @return the turn counter.
     */

    public int getTurnCounter() {
        return turnCounter;
    }

    /**
     * Retrieve player instance from player name.
     *
     * @param name player name
     * @return player (null if not found)
     */
    public Player getPlayerFromName(String name) {
        Player player = null;

        for (Player p : this.players) {
            if (p.getName().equals(name)) {
                player = p;
                break;
            }
        }

        return player;
    }

    /**
     * Retrieve all players in this game.
     *
     * @return list of all players in this game
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Allows player to perform an action.
     *
     * @param playerName player performing action
     * @param action action being executed
     * @return result of action execution
     */
    public abstract ActionResult takeAction(String playerName, Action action);


}
