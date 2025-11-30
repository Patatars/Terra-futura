package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the Pile interface.
 * Manages a pile of cards with a visible set and a hidden deck.
 */
public class PileImpl implements Pile {

    private final int MAX_VISIBLE_CARDS = 4;

    private final List<Card> visibleCards;
    private final List<Card> hiddenCards;

    /**
     * The first 4 cards are moved to the visible set.
     *
     * @param cards the list of cards to initialize the pile with
     */
    public PileImpl(final List<Card> cards) {
        this.hiddenCards = new ArrayList<>(cards);
        this.visibleCards = new ArrayList<>();
        replenishVisibleCards();
    }

    private void replenishVisibleCards() {
        while (visibleCards.size() < MAX_VISIBLE_CARDS && !hiddenCards.isEmpty()) {
            visibleCards.add(hiddenCards.removeFirst());
        }
    }

    /**
     * Get card from visible deck.
     * @param index index of card
     * @return Optional type of card
     */
    @Override
    public Optional<Card> getCard(final int index) {
        if  (index >= visibleCards.size() || index < 0) {
            return Optional.empty();
        }
        return Optional.of(visibleCards.get(index));
    }

    /**
     * Takes a card from the visible cards at the specified index.
     * The taken card is replaced by a new card from the hidden deck (if available).
     *
     * @param cardIndex the index of the card to take (0-based)
     * @return the taken card
     * @throws IllegalArgumentException if the index is invalid
     */
    @Override
    public Card takeCard(final int cardIndex) {
        if (cardIndex < 0 || cardIndex >= visibleCards.size()) {
            throw new IllegalArgumentException("Invalid card index: " + cardIndex);
        }
        Card takenCard = visibleCards.remove(cardIndex);
        if (!hiddenCards.isEmpty()) {
            visibleCards.addFirst(hiddenCards.removeFirst());
        }
        return takenCard;
    }

    /**
     * Removes the last visible card (the oldest one).
     * The discarded card is replaced by a new card from the hidden deck (if
     * available).
     */
    @Override
    public void removeLastCard() {
        if (visibleCards.isEmpty()) {
            return;
        }
        visibleCards.removeLast();
        if (!hiddenCards.isEmpty()) {
            visibleCards.addFirst(hiddenCards.removeFirst());
        }
    }

    /**
     * Returns a string representation of the pile's state.
     *
     * @return the state of the pile
     */
    @Override
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pile State:\n");
        sb.append("Visible Cards:\n");
        for (int i = 0; i < visibleCards.size(); i++) {
            sb.append(i).append(": ").append(visibleCards.get(i).state()).append("\n");
        }
        sb.append("Hidden Cards Count: ").append(hiddenCards.size());
        return sb.toString();
    }
}
