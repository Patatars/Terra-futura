package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a pile consisting of:
 * - a visible set of up to 4 cards
 * - a hidden deck providing replacement cards
 *
 * The first four cards from the provided list become visible (or fewer if not enough exist).
 * All remaining cards stay hidden until needed.
 */
public class PileImpl implements Pile {

    /** Maximum number of visible cards at any time. */
    private final static int MAX_VISIBLE_CARDS = 4;

    /** Cards currently visible to the players (maximum of 4). */
    private final List<Card> visibleCards;

    /** Remaining hidden cards forming the deck. */
    private final List<Card> hiddenCards;

    /**
     * Initializes the pile with a given list of cards.
     * The first 4 cards become visible; the rest remain hidden.
     *
     * @param cards initial list of cards forming the pile
     */
    public PileImpl(final List<Card> cards) {
        this.hiddenCards = new ArrayList<>(cards);
        this.visibleCards = new ArrayList<>();

        // Move up to 4 cards from hidden to visible
        while (visibleCards.size() < MAX_VISIBLE_CARDS && !hiddenCards.isEmpty()) {
            visibleCards.add(hiddenCards.removeFirst());
        }
    }

    /**
     * Returns the visible card at the given index if it exists.
     *
     * @param index index of the visible card (0-based)
     * @return Optional containing the card, or empty if index is invalid
     */
    @Override
    public Optional<Card> getCard(final int index) {
        if (index < 0 || index >= visibleCards.size()) {
            return Optional.empty();
        }
        return Optional.of(visibleCards.get(index));
    }

    /**
     * Removes and returns the visible card at the specified index.
     * If the hidden deck is not empty, a new card is inserted at the front of the visible list.
     *
     * @param cardIndex index of the card to take (0-based)
     * @throws IllegalArgumentException if the index is invalid
     */
    @Override
    public void takeCard(final int cardIndex) {
        if (cardIndex < 0 || cardIndex >= visibleCards.size()) {
            throw new IllegalArgumentException("Invalid card index: " + cardIndex);
        }

        Card takenCard = visibleCards.remove(cardIndex);

        if (!hiddenCards.isEmpty()) {
            visibleCards.addFirst(hiddenCards.removeFirst());
        }
    }

    /**
     * Removes the last (rightmost) visible card.
     * A new card from the hidden deck is inserted at the front if available.
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
     * Returns a textual representation of the pile for debugging or logging.
     *
     * @return formatted string showing visible cards and count of hidden cards
     */
    @Override
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pile State:\n");
        sb.append("Visible Cards:\n");

        for (int i = 0; i < visibleCards.size(); i++) {
            sb.append(i)
                    .append(": ")
                    .append(visibleCards.get(i).state())
                    .append("\n");
        }

        sb.append("Hidden Cards Count: ").append(hiddenCards.size());
        return sb.toString();
    }
}
