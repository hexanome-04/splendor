package ca.hexanome04.splendorgame.model.gameversions.orient;

import ca.hexanome04.splendorgame.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A concrete implementation of Player for Orient game.
 */
public class OrientPlayer implements Player {

    private String name;
    private String colour;

    private int prestigePoints;
    private HashMap<TokenType, Integer> tokens;
    private HashMap<TokenType, Integer> bonuses;
    private ArrayList<DevelopmentCard> devCards;
    private ArrayList<NobleCard> nobleCards;
    private ArrayList<DevelopmentCard> reservedCards;
    private ArrayList<NobleCard> reservedNobles;

    /**
     * Creates a Player object with a given name and colour.
     *
     * @param name   The name of the player.
     * @param colour The color associated to the player.
     */
    public OrientPlayer(String name, String colour) {
        this.name = name;
        this.colour = colour;
        prestigePoints = 0;
        tokens = new HashMap<>();
        bonuses = new HashMap<>();
        devCards = new ArrayList<>();
        reservedCards = new ArrayList<>();
        reservedNobles = new ArrayList<>();
        nobleCards = new ArrayList<>();

        for (TokenType tokenType : TokenType.values()) {
            tokens.put(tokenType, 0);
            bonuses.put(tokenType, 0);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getColour() {
        return colour;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setColour(String colour) {
        this.colour = colour;
    }

    @Override
    public void addTokens(HashMap<TokenType, Integer> tokens) {
        tokens.forEach((key, value) -> this.tokens.put(key, this.tokens.get(key) + value));
    }

    @Override
    public void addBonus(TokenType bonus, int numBonuses) {
        this.bonuses.put(bonus, bonuses.get(bonus) + numBonuses);
    }

    @Override
    public void addNoble(NobleCard nobleCard) {
        this.nobleCards.add(nobleCard);
        this.prestigePoints += 3;
    }

    @Override
    public void addPrestigePoints(int prestigePoints) {
        this.prestigePoints += prestigePoints;
    }

    @Override
    public void addCard(DevelopmentCard card) {
        devCards.add(card);
    }

    @Override
    public void removeCard(DevelopmentCard card) {
        this.devCards.remove(card);
    }

    @Override
    public void removeReservedCard(DevelopmentCard card) {
        this.reservedCards.remove(card);
    }

    @Override
    public void removeReservedNoble(NobleCard card) {
        this.reservedNobles.remove(card);
    }

    @Override
    public HashMap<TokenType, Integer> getTokens() {
        return new HashMap<>(this.tokens);
    }

    @Override
    public ArrayList<NobleCard> getNobles() {
        return new ArrayList<>(this.nobleCards);
    }

    @Override
    public List<DevelopmentCard> getReservedCards() {
        return new ArrayList<>(this.reservedCards);
    }

    @Override
    public List<NobleCard> getReservedNobles() {
        return new ArrayList<>(this.reservedNobles);
    }

    @Override
    public Map<TokenType, Integer> getBonuses() {
        return new HashMap<>(this.bonuses);
    }

    @Override
    public void removeTokens(HashMap<TokenType, Integer> tokens) {
        tokens.forEach((key, value) -> this.tokens.put(key, this.tokens.get(key) - value));
    }

    @Override
    public void removeBonuses(HashMap<TokenType, Integer> bonuses) {
        bonuses.forEach((key, value) -> {
            Integer currentValue = this.bonuses.get(key);
            this.bonuses.put(key, currentValue - value);
        });
    }

    @Override
    public void takeTokens(HashMap<TokenType, Integer> take, HashMap<TokenType, Integer> putBack) {
        addTokens(take);
        removeTokens(putBack);
    }

    @Override
    public void reserveCard(DevelopmentCard card) {
        reservedCards.add(card);
    }

    @Override
    public void reserveNoble(NobleCard noble) {
        reservedNobles.add(noble);
    }

    @Override
    public boolean hasTokens(HashMap<TokenType, Integer> checkTokens) {

        for (TokenType key : checkTokens.keySet()) {

            if ((this.tokens.get(key) - checkTokens.get(key)) < 0) {
                return false;
            }

        }
        return true;

    }

    @Override
    public boolean hasBonuses(HashMap<TokenType, Integer> checkBonuses) {
        for (TokenType key : checkBonuses.keySet()) {
            if ((this.bonuses.get(key) - checkBonuses.get(key)) < 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasNobles() {
        return (this.nobleCards.size() > 0);
    }

    @Override
    public List<DevelopmentCard> getDevCards() {
        return new ArrayList<DevelopmentCard>(this.devCards);
    }

    @Override
    public DevelopmentCard getPurchasedDevelopmentCard(String cid) {
        for (DevelopmentCard c : this.devCards) {
            if (c.getId().equals(cid)) {
                return c;
            }
        }
        return null;

    }

    @Override
    public DevelopmentCard getReservedDevelopmentCard(String cid) {
        for (DevelopmentCard c : this.reservedCards) {
            if (c.getId().equals(cid)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public int getPrestigePoints() {
        return this.prestigePoints;
    }

    @Override
    public String toString() {
        return "Player name: " + name + " / Player colour: " + colour;
    }

}
