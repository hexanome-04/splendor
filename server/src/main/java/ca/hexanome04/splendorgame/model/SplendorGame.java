package ca.hexanome04.splendorgame.model;

import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;

import java.util.ArrayList;

/**
 * Class that represents the current state of a game.
 */
public class SplendorGame {

    private SplendorBoard boardState;
    private ArrayList<Player> players = new ArrayList<>();
    private final int prestigePointsToWin;
    private int turnCounter;

    /**
    * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
    *
    * @param boardState          The state of the gameboard.
    * @param prestigePointsToWin The amount of prestige points needed to win the game.
    * @param players             The player order in the game.
    * @param turnCounter              The turn id associated with the player
    */
    public SplendorGame(SplendorBoard boardState, int prestigePointsToWin, ArrayList<Player> players, int turnCounter) {
        this.boardState = boardState;
        this.prestigePointsToWin = prestigePointsToWin;
        this.players.addAll(players);        
        this.turnCounter = turnCounter;

        // set player id for the game
        for(int i = 0 ; i < players.size(); i++) {
            players.get(i).setID(i);
        }
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
     * Retrieve player instance from player id.
     *
     * @param playerID player id
     * @return player (null if not found)
     */
    public Player getPlayerFromID(int playerID) {
        Player player = null;

        for(Player p : this.players) {
            if (p.getID() == playerID) {
                player = p;
                break;
            }
        }

        return player;
    }

    /**
     *
     *
     * @param p
     * @param action
     * @return
     */
    public ActionResult takeAction(int playerID, Action action) {

        if(playerID != this.turnCounter) {
            return ActionResult.INVALID_PLAYER;
        }

        Player p = this.players.get(playerID);

        ActionResult ar = action.executeAction(this, p);

        // update gameboard?

        if(ar == ActionResult.VALID_ACTION) {
            // success and valid action
            this.incrementTurn();
        }

        return ar;
    }



}
