package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;

/**
 * Class that represents the current state of a game.
 */
public class SplendorGame {

    private SplendorBoard boardState;
    private ArrayList<Player> players = new ArrayList<>();
    private final int prestigePointsToWin;
    private int turnId;

    /**
    * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
     
    * @param boardState          The state of the gameboard.
    * @param prestigePointsToWin The amount of prestige points needed to win the game.
    * @param players             The player order in the game.
    */
    public SplendorGame(SplendorBoard boardState, int prestigePointsToWin, ArrayList<Player> players, int turnId) {
        this.boardState = boardState;
        this.prestigePointsToWin = prestigePointsToWin;
        this.players.addAll(players);        
        this.turnId = turnId;
    }

    /**
     * Get the player who is playing next.
     
     * @return the next player who is going to play. 
     */

    public Player getNextPlayer() {
        if (turnId == 3) {
            Player nextPlayer = players.get(turnId);
            turnId = 0;
            return nextPlayer;
        } else {
            Player nextPlayer = players.get(turnId);
            turnId++;
            return nextPlayer;
        }  
    }
}
