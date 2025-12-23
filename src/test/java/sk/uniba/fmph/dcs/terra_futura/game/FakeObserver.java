package sk.uniba.fmph.dcs.terra_futura.game;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.grid.Grid;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

import java.util.Optional;

import static org.junit.Assert.*;

public class FakeObserver implements ObserverInterface {

    @Override
    public void notify(String state) {}

    public void assertCardAt(Grid grid, GridPosition position, String expectedCardName) {
        Optional<Card> cardOpt = grid.getCard(position);
        assertTrue("No card at position " + position, cardOpt.isPresent());
        Card card = cardOpt.get();
        assertTrue("Card at " + position + " should contain '" + expectedCardName + "' but was '" + card + "'",
                card.toString().contains(expectedCardName));
    }
}

