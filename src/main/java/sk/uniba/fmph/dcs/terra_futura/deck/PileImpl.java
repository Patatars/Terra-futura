package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a visible card pile as defined in Terra Futura.
 * The pile maintains up to 4 visible cards. When a card is taken or the oldest
 * card is discarded, a new card is drawn from the front of the hidden deck (FIFO).
 * The newest card is always at index 0, and the oldest at index 3.
 */
public final class PileImpl implements Pile {

    private static final int MAX_VISIBLE = 4;

    private final LinkedList<Card> visible = new LinkedList<>();
    private final LinkedList<Card> hidden = new LinkedList<>();

    /**
     * Constructs a pile from a list of cards
     * The initial visible cards are filled from the beginning of the provided list.
     * Remaining cards form the hidden deck.
     *
     * @param allCards list of all cards to initialize the pile; must not be null
     * @throws IllegalArgumentException if {@code allCards} is null
     */
    public PileImpl(final List<Card> allCards) {
        if (allCards == null) {
            throw new IllegalArgumentException("Card list must not be null");
        }
        this.hidden.addAll(allCards);
        refillVisible();
    }

    private void refillVisible() {
        while (visible.size() < MAX_VISIBLE && !hidden.isEmpty()) {
            visible.addLast(hidden.removeFirst());
        }
    }

    /**
     * Retrieves the card at the specified index in the visible pile.
     *
     * @param index zero-based index in the visible pile (0 = newest, 3 = oldest)
     * @return an {@link Optional} containing the card, or empty if index is invalid
     */
    @Override
    public Optional<Card> getCard(final int index) {
        if (index < 0 || index >= visible.size()) {
            return Optional.empty();
        }
        return Optional.of(visible.get(index));
    }

    /**
     * Takes a card from the visible pile at the given index
     * After removal, a new card (if available) is drawn from the hidden deck
     * and inserted at the front (index 0), becoming the new newest card.
     *
     * @param index zero-based index of the card to take
     * @throws IllegalArgumentException if the index is out of bounds
     */
    @Override
    public void takeCard(final int index) {
        if (index < 0 || index >= visible.size()) {
            throw new IllegalArgumentException("Invalid card index: " + index);
        }
        visible.remove(index);
        if (!hidden.isEmpty()) {
            visible.addFirst(hidden.removeFirst());
        }
    }

    /**
     * Discards the oldest visible card (last in the list)
     * If the hidden deck is not empty, a new card is drawn and added to the front,
     * preserving the pile size.
     */
    @Override
    public void removeLastCard() {
        if (!visible.isEmpty()) {
            visible.removeLast();
            if (!hidden.isEmpty()) {
                visible.addFirst(hidden.removeFirst());
            }
        }
    }

    /**
     * Includes all visible cards (with their internal state) and the count of hidden cards.
     *
     * @return a formatted string describing the pile
     */
    @Override
    public String state() {
        StringBuilder out = new StringBuilder("Pile State:\nVisible Cards:\n");
        for (int i = 0; i < visible.size(); i++) {
            out.append(i).append(": ").append(visible.get(i).state()).append('\n');
        }
        out.append("Hidden Cards Count: ").append(hidden.size());
        return out.toString();
    }
}
