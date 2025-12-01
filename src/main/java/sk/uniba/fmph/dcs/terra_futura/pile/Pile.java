package sk.uniba.fmph.dcs.terra_futura.pile;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import java.util.Optional;

public interface Pile {

    /**
     * Returns the card at the specified index, without removing it from the pile.
     * Returns Optional.empty() if the index has no value or if there is no card.
     *
     * @param index the index of the card to retrieve.
     * @return an Optional containing the card if present, otherwise Optional.empty().
     */
    Optional<Card> getCard(int index);

    /**
     * Removes the card at the specified index from pile.
     *
     * @param index the index of the card to be removed.
     */
    void takeCard(int index);

    /**
     * Removes a card from the top of pile.
     */
    void removeLastCard();

    /**
     * returns a string representation of the pile state.
     *
     * @return a descriptive string of the pile.
     */
    String state();
}