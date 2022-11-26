package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the current state of a game.
 */
public class SplendorGame {

    private SplendorBoard boardState;
    private List<Player> players = new ArrayList<>();
    private final int prestigePointsToWin;
    private int turnCounter;

    /**
    * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
    *
    * @param boardState          The state of the gameboard.
    * @param prestigePointsToWin The amount of prestige points needed to win the game.
    * @param players             The player order in the game.
    * @param turnCounter         The turn id associated with the player
    */
    public SplendorGame(SplendorBoard boardState, int prestigePointsToWin, List<Player> players, int turnCounter) {
        this.boardState = boardState;
        this.prestigePointsToWin = prestigePointsToWin;
        this.players.addAll(players);        
        this.turnCounter = turnCounter;
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
    * Get the board state of the game. 
    *
    * @return the board state.
    */

    public SplendorBoard getBoardState() {
        return boardState;
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
    public ActionResult takeAction(String playerName, Action action) {

        if (players.indexOf(this.getPlayerFromName(playerName)) != this.turnCounter) {
            return ActionResult.INVALID_PLAYER;
        }

        Player p = this.getPlayerFromName(playerName);

        ActionResult ar = action.execute(this, p);

        // update gameboard?

        if (ar == ActionResult.VALID_ACTION) {
            // success and valid action
            this.incrementTurn();
        }

        return ar;
    }



}
