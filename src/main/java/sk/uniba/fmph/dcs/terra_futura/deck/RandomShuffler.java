package sk.uniba.fmph.dcs.terra_futura.deck;

import java.util.Collections;
import java.util.List;

public class RandomShuffler implements Shuffler {

    @Override
    public void shuffle(final List<?> list) {
        Collections.shuffle(list);
    }
}

