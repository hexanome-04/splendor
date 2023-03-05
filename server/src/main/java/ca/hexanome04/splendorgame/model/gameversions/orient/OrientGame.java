package ca.hexanome04.splendorgame.model.gameversions.orient;

import static ca.hexanome04.splendorgame.model.TokenType.*;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.gameversions.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Class that represents the current state of a base game + orient game.
 */
public class OrientGame extends Game {

    final Logger logger = LoggerFactory.getLogger(OrientGame.class);

    private Deck<NobleCard> nobleDeck;
    /** Tier 1 Orient deck. */
    protected Deck<OrientDevelopmentCard> tier1OrientDeck;
    /** Tier 2 Orient deck. */
    protected Deck<OrientDevelopmentCard> tier2OrientDeck;
    /** Tier 3 Orient deck. */
    protected Deck<OrientDevelopmentCard> tier3OrientDeck;

    /**
     * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
     *
     *
     * @param prestigePointsToWin The amount of prestige points needed to win the game.
     * @param turnCounter         The turn id associated with the player.
     */
    public OrientGame(int prestigePointsToWin, int turnCounter) {
        super(GameVersions.BASE_ORIENT, prestigePointsToWin, turnCounter);
    }

    /**
     * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
     *
     * @param gameVersion         The game version of this game instance.
     * @param prestigePointsToWin The amount of prestige points needed to win the game.
     * @param turnCounter         The turn id associated with the player.
     */
    protected OrientGame(GameVersions gameVersion, int prestigePointsToWin, int turnCounter) {
        super(gameVersion, prestigePointsToWin, turnCounter);
    }

    /**
     * Constructor for the splendorBoard, initializes all the decks with cards from a file.
     *
     * @param inputStream input stream for cards csv
     */
    public void createSplendorBoard(InputStream inputStream) {
        String line = "";

        nobleDeck = new Deck<>();
        tier1OrientDeck = new Deck<>();
        tier2OrientDeck = new Deck<>();
        tier3OrientDeck = new Deck<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()))) {
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
                    case "1" -> tier1Deck.add(new RegDevelopmentCard(CardTier.TIER_1, tokenType, bonusCount,
                            prestigePoints, costType, tokenCost, cardId));

                    case "2" -> tier2Deck.add(new RegDevelopmentCard(CardTier.TIER_2, tokenType, bonusCount,
                            prestigePoints, costType, tokenCost, cardId));

                    case "3" -> tier3Deck.add(new RegDevelopmentCard(CardTier.TIER_3, tokenType, bonusCount,
                            prestigePoints, costType, tokenCost, cardId));

                    case "N" -> nobleDeck.add(new NobleCard(prestigePoints, costType, tokenCost, cardId));

                    case "O1" -> tier1OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_1, tokenType, bonusCount,
                            CascadeType.None, false, prestigePoints, costType, tokenCost, cardId));

                    case "O2" -> {
                        if (!card[7].isBlank()) {
                            tier2OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_2, tokenType, bonusCount,
                                    CascadeType.Tier1, false, prestigePoints, costType, tokenCost, cardId));
                        } else if (!card[8].isBlank()) {
                            tier2OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_2, tokenType, bonusCount,
                                    CascadeType.None, true, prestigePoints, costType, tokenCost, cardId));
                        } else {
                            tier2OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_2, tokenType, bonusCount,
                                    CascadeType.None, false, prestigePoints, costType, tokenCost, cardId));
                        }
                    }

                    case "O3" -> {
                        if (!card[7].isBlank()) {
                            tier3OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_3, tokenType, bonusCount,
                                    CascadeType.Tier2, false, prestigePoints, costType, tokenCost, cardId));
                        } else {
                            tier3OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_3, tokenType, bonusCount,
                                    CascadeType.None, false, prestigePoints, costType, tokenCost, cardId));
                        }
                    }

                    default -> throw new Exception("File not in proper format");
                }

                lines++;
            }
        } catch (Exception e) {
            logger.error("Could not read file"); // Testing
        }
    }

    /**
     * Initialize state of the board (cards, nobles, tokens).
     */
    public void initBoard() {
        tier1Deck.shuffle();
        tier2Deck.shuffle();
        tier3Deck.shuffle();
        tier1OrientDeck.shuffle();
        tier2OrientDeck.shuffle();
        tier3OrientDeck.shuffle();

        tier1Deck.drawCards(4);
        tier2Deck.drawCards(4);
        tier3Deck.drawCards(4);
        tier1OrientDeck.drawCards(2);
        tier2OrientDeck.drawCards(2);
        tier3OrientDeck.drawCards(2);

        nobleDeck.shuffle();
        nobleDeck.drawCards(players.size() + 1);

        // Now initialize tokens
        int numTokens;

        switch (players.size()) {
            case 2 -> numTokens = 4;
            case 3 -> numTokens = 5;
            case 4 -> numTokens = 7;
            default -> {
                numTokens = 0;
                //logger.error("Invalid number of players."); // Testing
            }
        }

        for (TokenType tokenType : TokenType.values()) {
            tokens.put(tokenType, numTokens);
        }

        tokens.put(Satchel, 0);
        tokens.put(Gold, 5);
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

        for (Card c : this.tier1OrientDeck.getVisibleCards()) {
            if (c.getId().equals(id)) {
                card = c;
            }
        }

        for (Card c : this.tier2OrientDeck.getVisibleCards()) {
            if (c.getId().equals(id)) {
                card = c;
            }
        }

        for (Card c : this.tier3OrientDeck.getVisibleCards()) {
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

        } else if (card instanceof OrientDevelopmentCard ordcard) {
            takenCard = this.tier1OrientDeck.take(ordcard);

            if (takenCard == null) {
                takenCard = this.tier2OrientDeck.take(ordcard);
            }

            if (takenCard == null) {
                takenCard = this.tier3OrientDeck.take(ordcard);
            }

        } else if (card instanceof NobleCard nbcard) {
            takenCard = this.nobleDeck.take(nbcard);
        }

        return takenCard != null;
    }

    /**
     * Check if player has the right bonuses to qualify for any nobles.
     *
     * @param player Player to check
     * @return List of nobles player qualifies for
     */
    public ArrayList<NobleCard> qualifiesForNoble(Player player) {
        ArrayList<NobleCard> noblesQualifiedFor = new ArrayList<>();

        ArrayList<NobleCard> nobles = new ArrayList<>(nobleDeck.getVisibleCards());
        nobles.addAll(player.getReservedNobles());

        for (NobleCard noble : nobles) {
            int notEnoughBonusesCounter = 0;

            for (TokenType tokenType : noble.getTokenCost().keySet()) {
                // subtract player's bonuses of this TokenType from noble card cost
                int bonusRemaining = noble.getTokenCost().get(tokenType) - player.getBonuses().get(tokenType);
                if (bonusRemaining > 0) {
                    notEnoughBonusesCounter++;
                }
            }

            if (notEnoughBonusesCounter == 0) {
                noblesQualifiedFor.add(noble);
            }
        }

        return noblesQualifiedFor;
    }

    /**
     * Allows player to perform an action.
     *
     * @param playerName player performing action
     * @param action     action being executed
     * @return result of action execution
     */
    public ArrayList<ActionResult> takeAction(String playerName, Action action) {

        this.clearMainValidActions();

        ArrayList<ActionResult> results = new ArrayList<>();

        if (players.indexOf(this.getPlayerFromName(playerName)) != this.turnCounter) {
            results.add(ActionResult.INVALID_PLAYER);
            return results;
        }

        Player p = this.getPlayerFromName(playerName);

        List<ActionResult> ar = action.execute(this, p);

        // Check if player qualifies for noble card at end of turn
        ArrayList<NobleCard> nobleCards = qualifiesForNoble(p);
        if (nobleCards.size() != 0) {
            if (nobleCards.size() == 1) {
                NobleCard noble = nobleCards.get(0);
                takeCard(noble);
                p.addNoble(noble);
            } else {
                // don't increment turn if player must choose noble
                results.add(ActionResult.MUST_CHOOSE_NOBLE);
            }
        }

        results.addAll(ar);
        if (results.contains(ActionResult.TURN_COMPLETED) && results.contains(ActionResult.VALID_ACTION)
                && results.size() == 2) {
            // increment action also resets current valid actions list
            this.incrementTurn();
        } else {

            for (ActionResult ares : results) {
                switch (ares) {
                    case MUST_CHOOSE_NOBLE -> this.addValidAction(Actions.CHOOSE_NOBLE);
                    case MUST_CHOOSE_CASCADE_CARD_TIER_1 -> this.addValidAction(Actions.CASCADE_1);
                    case MUST_CHOOSE_CASCADE_CARD_TIER_2 -> this.addValidAction(Actions.CASCADE_2);
                    case MUST_CHOOSE_TOKEN_TYPE -> this.addValidAction(Actions.CHOOSE_SATCHEL_TOKEN);
                    case MUST_RESERVE_NOBLE -> this.addValidAction(Actions.RESERVE_NOBLE);
                    default -> {
                        continue;
                    }
                }
            }
        }

        return results;
    }

    @Override
    public Player createPlayer(String name, String colour) {
        return new OrientPlayer(name, colour);
    }

    @Override
    public boolean canPlayerWin(Player player) {
        return false;
    }

    @Override
    public List<Player> getPlayersWhoCanWin() {
        return null;
    }


}
