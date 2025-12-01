package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the Pile interface.
 * Manages a pile of cards with a visible set and a hidden deck.
 */
public class PileImpl implements Pile {

    private static final int MAX_VISIBLE = 4;

    private final LinkedList<Card> visible;
    private final LinkedList<Card> hidden;

    /**
     * Initializes pile.
     * First up to MAX_VISIBLE cards become visible; rest are hidden.
     */
    public PileImpl(List<Card> cards) {
        this.hidden = new LinkedList<>(cards);
        this.visible = new LinkedList<>();
        refillVisible();
    }

    public PileImpl(LinkedList<Card> visible, LinkedList<Card> hidden) {
        this.visible = visible;
        this.hidden = hidden;
    }

    /**
     * Moves cards from hidden to visible until visible contains MAX_VISIBLE cards.
     */
    private void refillVisible() {
        while (visible.size() < MAX_VISIBLE && !hidden.isEmpty()) {
            visible.add(hidden.removeFirst());
        }
    }

    @Override
    public Optional<Card> getCard(int index) {
        if (index < 0 || index >= visible.size()) {
            return Optional.empty();
        }
        return Optional.of(visible.get(index));
    }

    @Override
    public void takeCard(int index) {
        if (index < 0 || index >= visible.size()) {
            throw new IllegalArgumentException("Invalid visible card index: " + index);
        }

        Card taken = visible.remove(index);

        // refill from hidden
        if (!hidden.isEmpty()) {
            visible.addFirst(hidden.removeFirst());
        }

    }

    /**
     * Removes the last (oldest) visible card.
     * Then refills visible with the next hidden card.
     */
    @Override
    public void removeLastCard() {
        if (visible.isEmpty()) return;

        visible.removeLast();

        if (!hidden.isEmpty()) {
            visible.addFirst(hidden.removeFirst());
        }
    }

    @Override
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeckPile:\n");
        sb.append("Visible:\n");
        for (int i = 0; i < visible.size(); i++) {
            sb.append("  ").append(i).append(": ")
                    .append(visible.get(i).state()).append("\n");
        }
        sb.append("Hidden count: ").append(hidden.size());
        return sb.toString();
    }
}
