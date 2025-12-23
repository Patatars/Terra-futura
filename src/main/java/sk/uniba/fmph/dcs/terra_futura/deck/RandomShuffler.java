package sk.uniba.fmph.dcs.terra_futura.deck;

import java.util.Collections;
import java.util.List;

public class RandomShuffler implements Shuffler {
    /**
     * Uses default Collections.shuffle.
     * @param list cards to shuffle.
     */
    @Override
    public void shuffle(final List<?> list) {
        Collections.shuffle(list);
    }
}

