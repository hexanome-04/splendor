package ca.hexanome04.splendorgame.model.gameversions.cities;

import static ca.hexanome04.splendorgame.model.TokenType.*;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.gameversions.GameVersions;
import ca.hexanome04.splendorgame.model.gameversions.orient.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;


/**
 * Class that represents the current state of an orient + cities game.
 */
public class CitiesGame extends OrientGame {

    private static final String cityCardsFilename = "classpath:citycards.csv";

    private Deck<CityCard> citiesDeck;
    static final Logger logger = LoggerFactory.getLogger(CitiesGame.class);

    /**
     * Creates a Splendor Cities game with the board state, number of prestige points to win and ordered player list.
     *
     * @param turnCounter         The turn id associated with the player.
     */
    public CitiesGame(int turnCounter) {
        super(GameVersions.BASE_ORIENT_CITIES, -1, turnCounter);
    }

    /**
     * Constructor for the cities splendorBoard, initializes the city deck with cards from a file.
     */
    @Override
    public void createSplendorBoard() {
        super.createSplendorBoard();

        String line = "";
        citiesDeck = new Deck<>();

        try (InputStream inputStream = ResourceUtils.getURL(cityCardsFilename).openStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()))) {
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
                for (TokenType tokenType : TokenType.values()) {
                    tokenCost.put(tokenType, 0);
                }

                for (int i = 0; i < 5; i++) {
                    if (!cost[i].equals("0")) {
                        tokenCost.put(types[i], Integer.parseInt(cost[i]));
                    }
                }
                int numSameBonuses = Integer.parseInt(cost[5]);

                int prestigePoints = Integer.parseInt(card[2]);
                String cardId = card[6];

                switch (card[5]) {
                    case "C" -> citiesDeck.add(new CityCard(prestigePoints, tokenCost, cardId, numSameBonuses));
                    default -> throw new Exception("File not in proper format");
                }

                lines++;
            }
        } catch (Exception e) {
            logger.error("Could not read file"); // Testing
        }

    }

    /**
     * Initialize cities-specific state of board.
     */
    @Override
    public void initBoard() {
        super.initBoard();

        citiesDeck.shuffle();
        citiesDeck.drawCards(3);

    }

    /**
     * Retrieve a card from the decks within the board.
     *
     * @param id card id
     * @return card from the board (could be null, if doesn't exist)
     */
    @Override
    public Card getCardFromId(String id) {
        Card card = super.getCardFromId(id);

        for (Card c : this.citiesDeck.getVisibleCards()) {
            if (c.getId().equals(id)) {
                card = c;
            }
        }

        return card;
    }

    @Override
    public boolean canPlayerWin(Player player) {
        List<CityCard> cities = new ArrayList<>(citiesDeck.getVisibleCards());

        // TODO: Make player choose city if they qualify for more than one

        for (CityCard city : cities) {

            boolean qualifiesForCity = true;

            // check for same bonus requirement
            int maxSameNumBonuses = 0;
            for (Map.Entry<TokenType, Integer> entry : player.getBonuses().entrySet()) {
                TokenType color = entry.getKey();
                // make sure same bonus color is not already in normal bonus price
                if (entry.getValue() > maxSameNumBonuses && (city.getTokenCost().get(color) == 0)) {
                    maxSameNumBonuses = entry.getValue();
                }
            }
            if (maxSameNumBonuses < city.getNumSameBonuses()) {
                qualifiesForCity = false;
            }

            // check for normal bonus requirements
            for (Map.Entry<TokenType, Integer> entry : city.getTokenCost().entrySet()) {
                TokenType color = entry.getKey();
                if (player.getBonuses().get(color) < entry.getValue()) {
                    qualifiesForCity = false;
                }
            }

            // check for prestige point requirement
            if (player.getPrestigePoints() < city.getPrestigePoints()) {
                qualifiesForCity = false;
            }

            // if nothing disqualifies city, return true
            if (qualifiesForCity) {
                CitiesPlayer p = (CitiesPlayer) player;
                p.addCity(city);
                citiesDeck.take(city);
                return true;
            }

        }

        return false;
    }


}
