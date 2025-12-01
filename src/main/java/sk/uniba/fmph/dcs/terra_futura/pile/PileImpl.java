package sk.uniba.fmph.dcs.terra_futura.pile;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PileImpl implements Pile {

    private final List<Card> hiddenCards;
    private final List<Card> visibleCards;
    private final int standartVisibleCards = 4;

    public PileImpl(final List<Card> cards) {

        this.hiddenCards = cards;
        this.visibleCards = new ArrayList<>();

        if (hiddenCards.size() > standartVisibleCards) {
            for (int i = 0; i < standartVisibleCards; i++) {
                visibleCards.add(hiddenCards.removeLast());
            }
        } else {
            visibleCards.addAll(hiddenCards);
            hiddenCards.clear();
        }
    }

    /**
     * Retrieves a card at the specified index without removing it.
     *
     * @param index The index of the card to retrieve.
     * @return An Optional containing the card if the index is valid, otherwise Optional.empty().
     */

    public Optional<Card> getCard(final int index) {
        if (index < 0 || index >= visibleCards.size()) {
            return Optional.empty();
        }
        return Optional.of(visibleCards.get(index));
    }

    /**
     * Removes the card at the specified index from the pile.
     * If the index is invalid, no action is performed.
     *
     * @param index The index of the card to remove.
     */

    public void takeCard(final int index) {
        if (index >= 0 && index < visibleCards.size()) {
            if (hiddenCards.isEmpty()) {
                visibleCards.remove(index);
                return;
            }
            ArrayList<Card> willRemain = new ArrayList<>();
            for (int i = 0; i < visibleCards.size(); i++) {
                if (i == index) {
                    continue;
                }
            willRemain.add(visibleCards.get(i));
            }
            visibleCards.clear();
            visibleCards.add(hiddenCards.removeLast());
            visibleCards.addAll(willRemain);
        }

    }

    /**
     * Removes the last (top) card from the pile.
     * If the pile is empty, no action is performed.
     */

    public void removeLastCard() {
        if (!visibleCards.isEmpty()) {

            if (hiddenCards.isEmpty()) {
                visibleCards.removeLast();
                return;
            }
            ArrayList<Card> willRemain = new ArrayList<>();
            willRemain.addAll(visibleCards);
            willRemain.removeLast();
            visibleCards.clear();
            visibleCards.add(hiddenCards.removeLast());
            visibleCards.addAll(willRemain);
        }
    }

    /**
     * Returns a string representation of the pile's state, listing the states of all contained cards.
     *
     * @return A formatted string describing the pile.
     */

    public String state() {
        String cardsState = visibleCards.stream()
                .map(Card::state)
                .collect(Collectors.joining(", "));
        return String.format("PileImpl{cards=[%s]}", cardsState);
    }
}
