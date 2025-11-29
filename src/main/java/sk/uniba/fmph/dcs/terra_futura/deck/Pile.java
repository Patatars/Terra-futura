package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.Optional;

/**
 * Pile of visible cards.
 */
public interface Pile {
    Optional<Card> getCard(int index);
    void takeCardP(int cardIndex);
    void removeLastCard();
    String state();
}
