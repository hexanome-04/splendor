package ca.hexanome04.splendorgame.model;

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
    }

    /**
     * Get the player who is playing next.
     *
     * @return the next player who is going to play. 
     */

    public Player incrementTurn() {
        if (turnCounter == 3) {
            Player nextPlayer = players.get(turnCounter);
            turnCounter = 0;
            return nextPlayer;
        } else {
            Player nextPlayer = players.get(turnCounter);
            turnCounter++;
            return nextPlayer;
        }  
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



}
