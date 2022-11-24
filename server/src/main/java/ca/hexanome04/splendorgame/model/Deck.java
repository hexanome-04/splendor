package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck<T extends Card> {

    private final Stack<T> cards;
    private final List<T> visibleCards;

    public Deck() {
        this.cards = new Stack<>();
        this.visibleCards = new ArrayList<>();
    }

    public List<T> getVisibleCards() {
        return new ArrayList<>(this.visibleCards);
    }

    /**
     * Add a card to the deck (face down).
     *
     * @param card card to add
     */
    public void add(T card) {
        this.cards.push(card);
    }

    /**
     * Take a card from the visible stack of cards.
     *
     * @param card card to take
     * @return card taken
     */
    public Card take(T card) {
        // or input a card id?

        // get the card from the visible cards list
        T takenCard = null;
        int i;
        for(i = 0; i < this.visibleCards.size(); i++) {
            T c = this.visibleCards.get(i);
            if(c.getID().equals(card.getID())) {
                takenCard = c;
                break;
            }
        }

        if(takenCard == null) {
            // no card taken (invalid selection?)
            // not sure what to do in this card (maybe this shouldn't be possible in the first place)

            return null;
        }

        // take card out of visible list
        this.visibleCards.remove(i);

        // also replenish the taken card (if there's enough in the cards stack)
        drawCards(1);

        // return the card taken
        return takenCard;
    }

    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void drawCards(int count) {
        for(int i = 0; i < count; i++) {
            if(!this.cards.isEmpty()) {
                this.visibleCards.add(this.cards.pop());
            }
        }
    }

}
