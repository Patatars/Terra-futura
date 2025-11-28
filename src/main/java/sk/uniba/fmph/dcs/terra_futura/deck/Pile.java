package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

/**
 * Pile of visible cards.
 */
public interface Pile {
    Card takeCard(int cardIndex);
    boolean discardCard();
    String state();
}
