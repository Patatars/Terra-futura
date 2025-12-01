package sk.uniba.fmph.dcs.terra_futura.moveCard;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.Optional;

/**
 * Pile of visible cards.
 */
public interface Pile {
    Optional<Card> getCard(int index);
    Card takeCard(int cardIndex);
    void removeLastCard();
    String state();
}
