package sk.uniba.fmph.dcs.terra_futura.deck;

import java.util.List;


/**
 * Shuffler classes are used by Pile to shuffle the deck.
 */

public interface Shuffler {
    /**
     * Shuffle.
     * @param list cards to shuffle.
     */
    void shuffle(List<?> list);
}
