package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.deck.PileImpl;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class PileImplTest {

    // Simple mock card for testing
    static class TestCard implements Card {
        private final String name;

        TestCard(String name) {
            this.name = name;
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
            return name;
        }
    }

    private Card c1 = new TestCard("C1");
    private Card c2 = new TestCard("C2");
    private Card c3 = new TestCard("C3");
    private Card c4 = new TestCard("C4");
    private Card c5 = new TestCard("C5");
    private Card c6 = new TestCard("C6");

    /**
     * Utility: creates a new pile with 6 cards.
     */
    private PileImpl create6CardPile() {
        return new PileImpl(List.of(c1, c2, c3, c4, c5, c6));
    }

    /**
     * Utility: creates a new pile with exactly 3 cards.
     */
    private PileImpl create3CardPile() {
        return new PileImpl(List.of(c1, c2, c3));
    }


    @Test
    public void testInitializationWithMoreThan4Cards() {
        PileImpl pile = create6CardPile();

        // first 4 should be visible
        assertEquals(Optional.of(c1), pile.getCard(0));
        assertEquals(Optional.of(c2), pile.getCard(1));
        assertEquals(Optional.of(c3), pile.getCard(2));
        assertEquals(Optional.of(c4), pile.getCard(3));

        // no more visible than 4
        assertTrue(pile.getCard(4).isEmpty());
    }

    @Test
    public void testInitializationWithLessThan4Cards() {
        PileImpl pile = create3CardPile();

        assertEquals(Optional.of(c1), pile.getCard(0));
        assertEquals(Optional.of(c2), pile.getCard(1));
        assertEquals(Optional.of(c3), pile.getCard(2));

        assertTrue(pile.getCard(3).isEmpty());
    }

    @Test
    public void testGetCardValidIndex() {
        PileImpl pile = create6CardPile();

        assertEquals(Optional.of(c2), pile.getCard(1));
    }

    @Test
    public void testGetCardInvalidIndex() {
        PileImpl pile = create6CardPile();

        assertTrue(pile.getCard(-1).isEmpty());
        assertTrue(pile.getCard(5).isEmpty());
    }

    @Test
    public void testTakeCardWhenHiddenIsEmpty() {
        PileImpl pile = create3CardPile();

        pile.takeCard(1); // remove C2, no replacement

        assertEquals(Optional.of(c1), pile.getCard(0));
        assertEquals(Optional.of(c3), pile.getCard(1));
        assertTrue(pile.getCard(2).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTakeCardThrowsOnInvalidIndex() {
        PileImpl pile = create6CardPile();

        pile.takeCard(-1);
        pile.takeCard(4);
    }

}