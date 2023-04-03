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
        bonuses.forEach((key, value) -> this.bonuses.put(key, this.bonuses.get(key) - value));
    }

    /**
     * Burns bonuses from the player's inventory by doing an optimal choice.
     *
     * @param bonuses Bonuses to burn from player inventory
     */
    public void burnBonuses(HashMap<TokenType, Integer> bonuses) {
        // When you remove bonuses, it's always 2 of the same colour, and cards
        // With exactly 2 bonus also exist, so either you use one of those or 2 regulars
        // We have to decide is it better to remove 2 single or 1 double
        List<DevelopmentCard> toRemove = new ArrayList<>();
        for (TokenType type : bonuses.keySet()) {
            if (bonuses.get(type) > 0) {
                toRemove = findOptimizedBurn(type);
                break;
            }
        }

        for (DevelopmentCard c : toRemove) {
            this.removeCard(c);
            this.bonuses.put(c.getTokenType(), this.bonuses.get(c.getTokenType()) - c.getBonus());
            this.prestigePoints -= c.getPrestigePoints();
        }

    }

    /**
     * Removes bonuses from player inventory in an optimized fashion (i.e. lowest PP).
     *
     * @param type type of bonus to remove from player inventory by lowest PP first
     */
    private List<DevelopmentCard> findOptimizedBurn(TokenType type) {
        // Either you choose a single double bonus, or 2 single bonuses
        DevelopmentCard lowestSingle = null;
        DevelopmentCard lowestDouble = null;
        boolean satchel = false;
        ArrayList<DevelopmentCard> list = new ArrayList<>();
        List<DevelopmentCard> cards = this.getDevCards();

        // Function does its first pass, finds the lowest double bonus card and the lowest single bonus card, or satchel card
        // Lowest as in least amount of prestige points
        for (DevelopmentCard c : cards) {
            if (c.getTokenType() == type) {
                if (c.getBonus() == 2) {
                    if (lowestDouble == null || c.getPrestigePoints() < lowestDouble.getPrestigePoints()) {
                        lowestDouble = c;
                    }

                } else {
                    if (lowestSingle == null || c.getPrestigePoints() < lowestSingle.getPrestigePoints()
                            || (c instanceof OrientDevelopmentCard && ((OrientDevelopmentCard) c).isSatchel())) {
                        lowestSingle = c;
                        if (c instanceof OrientDevelopmentCard && ((OrientDevelopmentCard) c).isSatchel()) {
                            satchel = true;
                        }
                    }
                }
            }
        }
        list.add(lowestSingle);
        cards.remove(lowestSingle);
        lowestSingle = null;

        // Function does its second pass, finding the second lowest single bonus card or more satchel cards
        for (DevelopmentCard c : cards) {
            if (c.getTokenType() == type) {
                if (c.getBonus() == 1 && (lowestSingle == null || c.getPrestigePoints() < lowestSingle.getPrestigePoints()
                        || (c instanceof OrientDevelopmentCard && ((OrientDevelopmentCard) c).isSatchel()))) {
                    lowestSingle = c;
                    if (c instanceof OrientDevelopmentCard && ((OrientDevelopmentCard) c).isSatchel()) {
                        satchel = true;
                    }
                }
            }
        }
        list.add(lowestSingle);

        // If a satchel was found, then skip the logic below to return the 2 lowest chosen cards (or satchels)
        if (satchel) {
            return list;
        }

        // Else, clear the list of single bonus cards and instead put in the lowest double bonus card and return that
        if (lowestDouble != null) {
            if (list.get(0) == null || list.get(1) == null) {
                list.clear();
                list.add(lowestDouble);
                return list;

            } else if (list.get(0).getPrestigePoints() + list.get(1).getPrestigePoints() > lowestDouble.getPrestigePoints()) {
                list.clear();
                list.add(lowestDouble);
                return list;
            } else {
                return list;
            }
        }
        return list;
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
