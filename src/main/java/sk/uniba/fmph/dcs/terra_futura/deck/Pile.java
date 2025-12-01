package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.Optional;

/**
 * Interface representing a pile of cards in a card game.
 * A pile manages a collection of cards with specific operations for card access and manipulation.
 */
public interface Pile {

    /**
     * Retrieves a card from the pile at the specified index without removing it.
     *
     * @param index the position of the card in the visible portion of the pile
     * @return an Optional containing the card if the index is valid and a card exists at that position,
     *         or an empty Optional if the index is out of bounds
     */
    Optional<Card> getCard(int index);

    /**
     * Takes (removes) a card from the pile at the specified index.
     * After removal, the pile may be replenished from its hidden deck according to implementation rules.
     *
     * @param cardIndex the position of the card to take from the visible portion of the pile
     * @throws IllegalArgumentException if the index is out of bounds (negative or greater than or equal
     *         to the number of visible cards)
     */
    void takeCard(int cardIndex);

    /**
     * Removes the last (oldest) card from the visible portion of the pile.
     * After removal, the pile may be replenished from its hidden deck according to implementation rules.
     * If the pile is empty, this method has no effect.
     */
    void removeLastCard();

    /**
     * Returns a string representation of the pile's current state.
     * The state typically includes information about visible cards, hidden cards, and pile configuration.
     *
     * @return a formatted string describing the current state of the pile
     */
    String state();
}
