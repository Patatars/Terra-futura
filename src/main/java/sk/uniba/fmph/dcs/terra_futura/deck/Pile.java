package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.Optional;

/**
 * Pile of visible cards.
 */
public interface Pile {
    Optional<Card> getCard(int index);
    Card takeCard(int cardIndex);
    boolean discardCard();
    String state();
}
