package ca.hexanome04.splendorgame.model.gameversions.orient;

import static ca.hexanome04.splendorgame.model.TokenType.*;

import ca.hexanome04.splendorgame.model.*;
import ca.hexanome04.splendorgame.model.action.Action;
import ca.hexanome04.splendorgame.model.action.ActionResult;
import ca.hexanome04.splendorgame.model.action.Actions;
import ca.hexanome04.splendorgame.model.action.actions.ChooseNobleAction;
import ca.hexanome04.splendorgame.model.gameversions.*;
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
 * Class that represents the current state of a base game + orient game.
 */
public class OrientGame implements Game {

    private static final String cardsFilename = "classpath:cards.csv";

    static final Logger logger = LoggerFactory.getLogger(OrientGame.class);

    private final GameVersions gameVersion;
    private final int prestigePointsToWin;
    private int turnCounter;

    private List<Actions> curValidActions = new ArrayList<>();

    private List<Player> players = new ArrayList<>();

    /** Board decks. */
    private final Deck<RegDevelopmentCard> tier1Deck = new Deck<>();
    private final Deck<RegDevelopmentCard> tier2Deck = new Deck<>();
    private final Deck<RegDevelopmentCard> tier3Deck = new Deck<>();
    private Deck<OrientDevelopmentCard> tier1OrientDeck;
    private Deck<OrientDevelopmentCard> tier2OrientDeck;
    private Deck<OrientDevelopmentCard> tier3OrientDeck;
    private Deck<NobleCard> nobleDeck;
    private final HashMap<TokenType, Integer> tokens = new HashMap<>();

    /** End of game info. */
    private List<Player> playersWhoCanWin = new ArrayList<>();
    private Player winner;
    private boolean gameOver = false;


    /**
     * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
     *
     * @param prestigePointsToWin The amount of prestige points needed to win the game.
     * @param turnCounter         The turn id associated with the player.
     */
    public OrientGame(int prestigePointsToWin, int turnCounter) {
        this(GameVersions.BASE_ORIENT, prestigePointsToWin, turnCounter);
    }

    /**
     * Creates a Splendor Game with the board state, number of prestige points to win and ordered player list.
     *
     * @param gameVersion         The game version of this game instance.
     * @param prestigePointsToWin The amount of prestige points needed to win the game.
     * @param turnCounter         The turn id associated with the player.
     */
    public OrientGame(GameVersions gameVersion, int prestigePointsToWin, int turnCounter) {
        this.gameVersion = gameVersion;
        this.prestigePointsToWin = prestigePointsToWin;
        this.turnCounter = turnCounter;

        curValidActions.add(Actions.BUY_CARD);
        curValidActions.add(Actions.TAKE_TOKEN);
        curValidActions.add(Actions.RESERVE_CARD);
    }

    /**
     * Constructor for the splendorBoard, initializes all the decks with cards from a file.
     */
    @Override
    public void createSplendorBoard() {

        String line = "";

        nobleDeck = new Deck<>();
        tier1OrientDeck = new Deck<>();
        tier2OrientDeck = new Deck<>();
        tier3OrientDeck = new Deck<>();

        try (InputStream inputStream = ResourceUtils.getURL(cardsFilename).openStream();
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

                int prestigePoints = Integer.parseInt(card[2]);
                TokenType tokenType = null;
                boolean isSatchel = false;
                int bonusCount = Integer.parseInt(card[1]);
                CostType costType = Enum.valueOf(CostType.class, card[3]);
                if (!card[0].isBlank()) {
                    tokenType = Enum.valueOf(TokenType.class, card[0]);
                }

                String cardId = card[6];

                if (tokenType == Satchel) {
                    isSatchel = true;
                }

                switch (card[5]) {
                    case "1" -> tier1Deck.add(new RegDevelopmentCard(CardTier.TIER_1, tokenType, bonusCount,
                            prestigePoints, costType, tokenCost, cardId));

                    case "2" -> tier2Deck.add(new RegDevelopmentCard(CardTier.TIER_2, tokenType, bonusCount,
                            prestigePoints, costType, tokenCost, cardId));

                    case "3" -> tier3Deck.add(new RegDevelopmentCard(CardTier.TIER_3, tokenType, bonusCount,
                            prestigePoints, costType, tokenCost, cardId));

                    case "N" -> nobleDeck.add(new NobleCard(prestigePoints, costType, tokenCost, cardId));

                    case "O1" -> tier1OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_1, tokenType, bonusCount,
                            CascadeType.None, false, prestigePoints, costType, tokenCost, cardId, isSatchel));

                    case "O2" -> {
                        if (!card[7].isBlank()) {
                            tier2OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_2, tokenType, bonusCount,
                                    CascadeType.Tier1, false, prestigePoints, costType, tokenCost, cardId, isSatchel));
                        } else if (!card[8].isBlank()) {
                            tier2OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_2, tokenType, bonusCount,
                                    CascadeType.None, true, prestigePoints, costType, tokenCost, cardId, isSatchel));
                        } else {
                            tier2OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_2, tokenType, bonusCount,
                                    CascadeType.None, false, prestigePoints, costType, tokenCost, cardId, isSatchel));
                        }
                    }

                    case "O3" -> {
                        if (!card[7].isBlank()) {
                            tier3OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_3, tokenType, bonusCount,
                                    CascadeType.Tier2, false, prestigePoints, costType, tokenCost, cardId, isSatchel));
                        } else {
                            tier3OrientDeck.add(new OrientDevelopmentCard(CardTier.TIER_3, tokenType, bonusCount,
                                    CascadeType.None, false, prestigePoints, costType, tokenCost, cardId, isSatchel));
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

        ArrayList<ActionResult> results = new ArrayList<>();

        if (players.indexOf(this.getPlayerFromName(playerName)) != this.turnCounter) {
            results.add(ActionResult.INVALID_PLAYER);
            return results;
        }

        Player p = this.getPlayerFromName(playerName);

        List<ActionResult> ar = action.execute(this, p);
        results.addAll(ar);

        // Check if player qualifies for noble card at end of turn
        ArrayList<NobleCard> nobleCards = qualifiesForNoble(p);
        if (nobleCards.size() != 0 && results.contains(ActionResult.TURN_COMPLETED)) {
            // only when player did not do choose noble
            if (nobleCards.size() == 1 && !(action instanceof ChooseNobleAction)) {
                NobleCard noble = nobleCards.get(0);
                takeCard(noble);
                p.addNoble(noble);

                if (p.getReservedNobles().contains(noble)) {
                    p.removeReservedNoble(noble);
                }
            } else if (!(action instanceof ChooseNobleAction)) {
                // only make player choose noble if they didn't just choose one
                results.add(ActionResult.MUST_CHOOSE_NOBLE);
            }
        }

        if (results.contains(ActionResult.TURN_COMPLETED) && results.contains(ActionResult.VALID_ACTION)
                && results.size() == 2 && this.getCurValidActions().size() == 0) {
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

        // check for winners
        if (canPlayerWin(p)) {
            addPlayersWhoCanWin(p);
        }
        if (checkForWin() != null) {
            turnCounter = -10000;
            curValidActions.clear();
        }

        return results;
    }

    /**
     * Get the list of nobles on the game board.
     *
     * @return list of nobles visible
     */
    public List<NobleCard> getNobles() {
        return new ArrayList<>(this.nobleDeck.getVisibleCards());
    }

    @Override
    public List<RegDevelopmentCard> getTier1PurchasableDevelopmentCards() {
        return this.tier1Deck.getVisibleCards();
    }

    @Override
    public List<RegDevelopmentCard> getTier2PurchasableDevelopmentCards() {
        return this.tier2Deck.getVisibleCards();
    }

    @Override
    public List<RegDevelopmentCard> getTier3PurchasableDevelopmentCards() {
        return this.tier3Deck.getVisibleCards();
    }

    @Override
    public Player createPlayer(String name, String colour) {
        return new OrientPlayer(name, colour);
    }

    @Override
    public void addTokens(Map<TokenType, Integer> tokensInput) {
        for (Map.Entry<TokenType, Integer> entry : tokensInput.entrySet()) {
            TokenType tokenType = entry.getKey();
            int amount = entry.getValue();

            if (this.tokens.containsKey(tokenType) && amount > 0) {
                this.tokens.put(tokenType, this.tokens.get(tokenType) + amount);
            }
        }
    }

    @Override
    public boolean removeTokens(Map<TokenType, Integer> tokensToRemove) {

        // TODO: Check first, technically this could remove some tokens before returning false.
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

    @Override
    public HashMap<TokenType, Integer> getTokens() {
        return new HashMap<>(this.tokens);
    }


    @Override
    public Player incrementTurn() {
        if (turnCounter == this.players.size() - 1) {
            turnCounter = 0;
        } else {
            turnCounter++;
        }

        // reset list of current player valid actions
        curValidActions.clear();
        curValidActions.add(Actions.BUY_CARD);
        curValidActions.add(Actions.TAKE_TOKEN);
        curValidActions.add(Actions.RESERVE_CARD);

        return players.get(turnCounter);
    }

    @Override
    public Player getTurnCurrentPlayer() {
        return players.get(turnCounter);
    }

    @Override
    public int getTurnCounter() {
        return turnCounter;
    }

    @Override
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

    @Override
    public void setPlayers(List<Player> players) {
        this.players.addAll(players);
    }

    @Override
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    @Override
    public List<Actions> getCurValidActions() {
        return new ArrayList<>(curValidActions);
    }

    @Override
    public GameVersions getGameVersion() {
        return this.gameVersion;
    }

    @Override
    public void addValidAction(Actions action) {
        curValidActions.add(action);
    }

    @Override
    public void removeValidAction(Actions action) {
        curValidActions.remove(action);
    }

    @Override
    public void clearValidActions() {
        curValidActions.clear();
    }

    @Override
    public void clearMainValidActions() {
        curValidActions.remove(Actions.BUY_CARD);
        curValidActions.remove(Actions.TAKE_TOKEN);
        curValidActions.remove(Actions.RESERVE_CARD);
    }

    @Override
    public Player checkForWin() {
        if (playersWhoCanWin.size() > 0 && turnCounter % players.size() == 0) {
            winner = playersWhoCanWin.get(0);
            for (Player p : playersWhoCanWin) {
                if (p.getDevCards().size() < winner.getDevCards().size()) {
                    winner = p;
                }
            }
            gameOver = true;
            return winner;
        }
        return null;
    }

    @Override
    public boolean canPlayerWin(Player player) {
        return (player.getPrestigePoints() >= prestigePointsToWin);
    }

    @Override
    public void addPlayersWhoCanWin(Player player) {
        playersWhoCanWin.add(player);
    }

    @Override
    public Player getWinner() {
        return winner;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

}
