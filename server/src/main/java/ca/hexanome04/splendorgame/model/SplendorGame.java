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
 * Creates a Splendor Game with the given board state, the number of prestige points to win and the ordered list of the players.
 * 
 * @param boardState The state of the gameboard.
 * @param prestigePointsToWin The amount of prestige points needed to win the game.
 * @param players The player order in the game.
 */
    public SplendorGame(SplendorBoard boardState, int prestigePointsToWin, ArrayList<Player> players, int turnId){
        this.boardState = boardState;
        this.prestigePointsToWin = prestigePointsToWin;
        this.players.addAll(players);        
        this.turnId = turnId;
    }

    /**
     * 
     * @return the next player who is going to play. 
     */

    public Player getNextPlayer(){
        if(turnId == 3){
            Player nextPlayer = players.get(turnId);
            turnId = 0;
            return nextPlayer;
        }else{
            Player nextPlayer = players.get(turnId);
            turnId++;
            return nextPlayer;
        }
        
    }

}
