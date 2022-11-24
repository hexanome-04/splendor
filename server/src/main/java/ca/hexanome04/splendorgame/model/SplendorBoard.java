package ca.hexanome04.splendorgame.model;

import static ca.hexanome04.splendorgame.model.TokenType.Blue;
import static ca.hexanome04.splendorgame.model.TokenType.Brown;
import static ca.hexanome04.splendorgame.model.TokenType.Green;
import static ca.hexanome04.splendorgame.model.TokenType.Red;
import static ca.hexanome04.splendorgame.model.TokenType.White;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;



/**
 * Class that represents the current state of the board.
 */
public class SplendorBoard {
    private final Stack<Card> greenDeck = new Stack<Card>();
    private final Stack<Card> yellowDeck = new Stack<Card>();
    private final Stack<Card> blueDeck = new Stack<Card>();

    /**
     * Constructor for the splendorBoard, initializes all the decks with cards from a file.
     *
     * @throws Exception Throws an exception if file cannot be read.
     */
    public SplendorBoard() throws Exception {
        String line = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader("cards.csv"));
            TokenType[] types = {White, Blue, Green, Red, Brown};
            while ((line = br.readLine()) != null) {
                String[] card = line.split(",");
                String[] cost = card[4].split(";");
                HashMap<TokenType, Integer> tokenCost = new HashMap<>();

                for (int i = 0; i <= 4; i++) {
                    if (!cost[i].equals("0")) {
                        tokenCost.put(types[i], Integer.parseInt(cost[i]));
                    }
                }
                switch (card[5]) {
                    case "G" -> {
                        greenDeck.add(new RegDevelopmentCard(Enum.valueOf(TokenType.class, card[0]), Integer.parseInt(card[1]),
                                Integer.parseInt(card[2]), Enum.valueOf(CostType.class, card[3]), tokenCost));
                    }
                    case "Y" -> {
                        yellowDeck.add(new RegDevelopmentCard(Enum.valueOf(TokenType.class, card[0]), Integer.parseInt(card[1]),
                                Integer.parseInt(card[2]), Enum.valueOf(CostType.class, card[3]), tokenCost));
                    }
                    case "B" -> {
                        blueDeck.add(new RegDevelopmentCard(Enum.valueOf(TokenType.class, card[0]), Integer.parseInt(card[1]),
                                Integer.parseInt(card[2]), Enum.valueOf(CostType.class, card[3]), tokenCost));
                    }
                    default -> throw new Exception("File not in proper format");
                }
                br.close();
            }
        } catch (IOException e) {
            System.out.println("Could not read file"); // Testing
        }

        Collections.shuffle(greenDeck);
        Collections.shuffle(yellowDeck);
        Collections.shuffle(blueDeck);
    }


}
