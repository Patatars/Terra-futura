package sk.uniba.fmph.dcs.terra_futura.pile;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import java.util.Optional;

public interface Pile {

    /**
     * Returns the card at the specified index, without removing it from the pile.
     * Returns Optional.empty() if the index has no value or if there is no card.
     */
    Optional<Card> getCard(int index);

    /**
     * Removes the card at the specified index from pile.
     */
    void takeCard(int index);

    /**
     * Removes a card from the top of pile.
     */
    void removeLastCard();

    /**
     * returns a string representation of the pile state.
     */
    String state();
}