package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the Pile interface.
 * Manages a pile of cards with a visible portion and a hidden deck.
 * The pile maintains up to MAX_VISIBLE cards in the visible area,
 * replenishing from the hidden deck when cards are removed.
 */
public final class PileImpl implements Pile {

    private static final int MAX_VISIBLE = 4;

    private final LinkedList<Card> visible;
    private final LinkedList<Card> hidden;

    /**
     * Constructs a new PileImpl with the given cards.
     * Initially, up to MAX_VISIBLE cards are placed in the visible area,
     * and the remaining cards are placed in the hidden deck.
     *
     * @param cards the initial cards to populate the pile
     */
    public PileImpl(final List<Card> cards) {
        this.hidden = new LinkedList<>(cards);
        this.visible = new LinkedList<>();
        refillVisible();
    }

    /**
     * Constructs a new PileImpl with the specified visible and hidden cards.
     * This constructor is primarily intended for testing purposes.
     *
     * @param visible the list of visible cards
     * @param hidden the list of hidden cards
     */
    public PileImpl(final LinkedList<Card> visible, final LinkedList<Card> hidden) {
        this.visible = visible;
        this.hidden = hidden;
    }

    /**
     * Replenishes the visible area from the hidden deck until it contains
     * MAX_VISIBLE cards or the hidden deck is empty.
     */
    private void refillVisible() {
        while (visible.size() < MAX_VISIBLE && !hidden.isEmpty()) {
            visible.add(hidden.removeFirst());
        }
    }

    @Override
    public Optional<Card> getCard(final int index) {
        if (index < 0 || index >= visible.size()) {
            return Optional.empty();
        }
        return Optional.of(visible.get(index));
    }

    @Override
    public void takeCard(final int cardIndex) {
        if (cardIndex < 0 || cardIndex >= visible.size()) {
            throw new IllegalArgumentException(
                    "Invalid visible card index: " + cardIndex
                            + ". Visible cards count: " + visible.size()
            );
        }

        visible.remove(cardIndex);

        // Replenish from hidden deck if available
        if (!hidden.isEmpty()) {
            visible.addFirst(hidden.removeFirst());
        }
    }

    @Override
    public void removeLastCard() {
        if (visible.isEmpty()) {
            return;
        }

        visible.removeLast();

        // Replenish from hidden deck if available
        if (!hidden.isEmpty()) {
            visible.addFirst(hidden.removeFirst());
        }
    }

    @Override
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeckPile:\n");
        sb.append("Visible cards (").append(visible.size()).append("):\n");

        for (int i = 0; i < visible.size(); i++) {
            sb.append("  ").append(i).append(": ")
                    .append(visible.get(i).state()).append("\n");
        }

        sb.append("Hidden cards: ").append(hidden.size());
        return sb.toString();
    }
}
