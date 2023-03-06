package ca.hexanome04.splendorgame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Represents a deck of cards, with contains visible (face-up) and non-visible (face-down) cards.
 *
 * @param <T> must extend card
 */
public class Deck<T extends Card> {

    private final Stack<T> cards;
    private final List<T> visibleCards;
    /**
     * This field will not work properly with a deck that contains Noble cards.
     */
    private boolean canDraw;

    /**
     * Construct a deck of cards with nothing inside.
     */
    public Deck() {
        this.cards = new Stack<>();
        this.visibleCards = new ArrayList<>();
        this.canDraw = false;
    }

    /**
     * Obtain all the visible (face-up) cards in this deck.
     *
     * @return list of all visible cards
     */
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
        this.canDraw = true;
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
        for (i = 0; i < this.visibleCards.size(); i++) {
            T c = this.visibleCards.get(i);
            if (c.getId().equals(card.getId())) {
                takenCard = c;
                break;
            }
        }

        if (takenCard == null) {
            // no card taken (invalid selection?)
            // not sure what to do in this card (maybe this shouldn't be possible in the first place)

            return null;
        }

        // take card out of visible list
        this.visibleCards.remove(i);

        // also replenish the taken card (if there's enough in the cards stack, and if it's not a noble card)
        if (!(card instanceof NobleCard)) {
            drawCards(1);
        }

        this.updateDrawable();

        // return the card taken
        return takenCard;
    }

    /**
     * Update internal variable for canDraw.
     */
    private void updateDrawable() {
        this.canDraw = this.cards.size() > 0;
    }

    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Take cards from non-visible (face-down) cards and put it in the visible (face-up) card list.
     *
     * @param count how many cards to make visible
     */
    public void drawCards(int count) {
        for (int i = 0; i < count; i++) {
            if (!this.cards.isEmpty()) {
                this.visibleCards.add(this.cards.pop());
            }
        }
        this.updateDrawable();
    }

}
