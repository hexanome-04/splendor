package ca.hexanome04.splendorgame.model;

import static ca.hexanome04.splendorgame.model.TokenType.Blue;
import static ca.hexanome04.splendorgame.model.TokenType.Brown;
import static ca.hexanome04.splendorgame.model.TokenType.Green;
import static ca.hexanome04.splendorgame.model.TokenType.Red;
import static ca.hexanome04.splendorgame.model.TokenType.White;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents the current state of the board.
 */
public class SplendorBoard {

    final Logger logger = LoggerFactory.getLogger(SplendorBoard.class);

    private final Deck<NobleCard> nobleDeck = new Deck<>();
    // Maybe rename them to tier instead of the color?

    private final Deck<RegDevelopmentCard> tier1Deck = new Deck<>();
    private final Deck<RegDevelopmentCard> tier2Deck = new Deck<>();
    private final Deck<RegDevelopmentCard> tier3Deck = new Deck<>();


    /**
     * Constructor for the splendorBoard, initializes all the decks with cards from a file.
     *
     * @throws Exception Throws an exception if file cannot be read.
     */
    public SplendorBoard(String filename) {
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            TokenType[] types = {White, Blue, Green, Red, Brown};
            int lines = 1;
            while ((line = br.readLine()) != null) {
                if (lines == 1) {
                    // skip first line (headers)
                    lines++;
                    continue;
                }

                String[] card = line.split(",");
                String[] cost = card[4].split(";");
                HashMap<TokenType, Integer> tokenCost = new HashMap<>();

                for (int i = 0; i <= 4; i++) {
                    if (!cost[i].equals("0")) {
                        tokenCost.put(types[i], Integer.parseInt(cost[i]));
                    }
                }

                int prestigePoints = Integer.parseInt(card[2]);
                TokenType tokenType = null;
                int bonusCount = Integer.parseInt(card[1]);
                CostType costType = Enum.valueOf(CostType.class, card[3]);
                if (!card[0].isBlank()) {
                    tokenType = Enum.valueOf(TokenType.class, card[0]);
                }

                String cardId = card[6];

                switch (card[5]) {
                    case "1" -> {
                        tier1Deck.add(new RegDevelopmentCard(CardTier.TIER_1, tokenType, bonusCount,
                                prestigePoints, costType, tokenCost, cardId));
                    }
                    case "2" -> {
                        tier2Deck.add(new RegDevelopmentCard(CardTier.TIER_2, tokenType, bonusCount,
                                prestigePoints, costType, tokenCost, cardId));
                    }
                    case "3" -> {
                        tier3Deck.add(new RegDevelopmentCard(CardTier.TIER_3, tokenType, bonusCount,
                                prestigePoints, costType, tokenCost, cardId));
                    }
                    case "N" -> {
                        nobleDeck.add(new NobleCard(prestigePoints, costType, tokenCost, cardId));
                    }
                    default -> throw new Exception("File not in proper format");
                }

                lines++;
            }
        } catch (Exception e) {
            logger.error("Could not read file"); // Testing
        }

        tier1Deck.shuffle();
        tier2Deck.shuffle();
        tier3Deck.shuffle();

        tier1Deck.drawCards(4);
        tier2Deck.drawCards(4);
        tier3Deck.drawCards(4);
    }

    public List<RegDevelopmentCard> getTier1PurchasableDevelopmentCards() {
        return this.tier1Deck.getVisibleCards();
    }

    public List<RegDevelopmentCard> getTier2PurchasableDevelopmentCards() {
        return this.tier2Deck.getVisibleCards();
    }

    public List<RegDevelopmentCard> getTier3PurchasableDevelopmentCards() {
        return this.tier3Deck.getVisibleCards();
    }

    /**
     * Retrieve a card from the decks within the board.
     *
     * @param id card id
     * @return card from the board (could be null, if doesn't exist)
     */
    public Card getCardFromId(String id) {
        // look through all the decks
        Card card = null;
        for (Card c : this.tier1Deck.getVisibleCards()) {
            if (c.getId().equals(id)) {
                card = c;
            }
        }

        for (Card c : this.tier2Deck.getVisibleCards()) {
            if (c.getId().equals(id)) {
                card = c;
            }
        }

        for (Card c : this.tier3Deck.getVisibleCards()) {
            if (c.getId().equals(id)) {
                card = c;
            }
        }

        for (Card c : this.nobleDeck.getVisibleCards()) {
            if (c.getId().equals(id)) {
                card = c;
            }
        }

        return card;
    }

    /**
     * Take a card from whichever deck it resides in.
     *
     * @param card card to take from the board
     * @return card taken successfully
     */
    public boolean takeCard(Card card) {

        Card takenCard = null;

        if (card instanceof RegDevelopmentCard rdcard) {
            takenCard = this.tier1Deck.take(rdcard);

            if (takenCard == null) {
                takenCard = this.tier2Deck.take(rdcard);
            }

            if (takenCard == null) {
                takenCard = this.tier3Deck.take(rdcard);
            }
        } else if (card instanceof NobleCard nbcard) {
            takenCard = this.nobleDeck.take(nbcard);
        }

        return takenCard != null;
    }

}
