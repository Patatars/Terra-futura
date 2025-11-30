package sk.uniba.fmph.dcs.terra_futura.deck;

import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class PileTest {

    static class FakeCard implements Card {
        private final String state;

        public FakeCard(String state) {
            this.state = state;
        }

        @Override
        public boolean canGetResources(List<Resource> resources) {
            return false;
        }

        @Override
        public void getResources(List<Resource> resources) {
        }

        @Override
        public boolean canPutResources(List<Resource> resources) {
            return false;
        }

        @Override
        public void putResources(List<Resource> resources) {

        }

        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return false;
        }

        @Override
        public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
            return false;
        }

        @Override
        public boolean hasAssistance() {
            return false;
        }

        @Override
        public String state() {
            return state;
        }
    }

    @Test
    public void testInitialization() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cards.add(new FakeCard("Card" + i));
        }

        Pile pile = new PileImpl(cards);
        String state = pile.state();

        assertTrue(state.contains("0: Card0"));
        assertTrue(state.contains("1: Card1"));
        assertTrue(state.contains("2: Card2"));
        assertTrue(state.contains("3: Card3"));
        assertTrue(state.contains("Hidden Cards Count: 6"));
    }

    @Test
    public void testTakeCard() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cards.add(new FakeCard("Card" + i));
        }

        Pile pile = new PileImpl(cards);
        Optional<Card> taken = pile.getCard(1);
        pile.takeCard(1);
        assertEquals("Card1", taken.get().state());

        String state = pile.state();

        assertTrue(state.contains("0: Card4"));
        assertTrue(state.contains("1: Card0"));
        assertTrue(state.contains("2: Card2"));
        assertTrue(state.contains("3: Card3"));
        assertTrue(state.contains("Hidden Cards Count: 5"));
    }

    @Test
    public void testDiscardCard() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            cards.add(new FakeCard("Card" + i));
        }

        Pile pile = new PileImpl(cards);


        pile.removeLastCard();

        String state = pile.state();
        assertTrue(state.contains("0: Card4"));
        assertTrue(state.contains("1: Card0"));
        assertTrue(state.contains("2: Card1"));
        assertTrue(state.contains("3: Card2"));
        assertFalse(state.contains("Card3"));
        assertTrue(state.contains("Hidden Cards Count: 5"));
    }

    @Test
    public void testEmptyHidden() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            cards.add(new FakeCard("Card" + i));
        }

        Pile pile = new PileImpl(cards);


        Optional<Card> taken = pile.getCard(0);
        pile.takeCard(0);
        assertEquals("Card0", taken.get().state());

        String state = pile.state();
        assertTrue(state.contains("0: Card1"));
        assertTrue(state.contains("1: Card2"));
        assertTrue(state.contains("2: Card3"));
        assertTrue(state.contains("Hidden Cards Count: 0"));


        pile.removeLastCard();

        state = pile.state();
        assertTrue(state.contains("0: Card1"));
        assertTrue(state.contains("1: Card2"));
        assertFalse(state.contains("Card3"));
    }
}
