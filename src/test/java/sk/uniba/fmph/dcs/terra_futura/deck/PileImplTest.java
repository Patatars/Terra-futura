package sk.uniba.fmph.dcs.terra_futura.deck;

import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class PileImplTest {

    private Card createTestCard(String name) {
        List<Resource> resources = new ArrayList<>();
        return new CardImpl(resources, 3, null, null);
    }

    private List<Card> createTestCards(int count) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cards.add(createTestCard("Card" + i));
        }
        return cards;
    }

    @Test
    public void testConstructorInitializesVisibleAndHidden() {
        List<Card> cards = createTestCards(6);
        PileImpl pile = new PileImpl(cards);

        int visibleCount = 0;
        while (pile.getCard(visibleCount).isPresent()) {
            visibleCount++;
        }
        assertEquals(4, visibleCount);
    }

    @Test
    public void testConstructorWithEmptyList() {
        List<Card> cards = new ArrayList<>();
        PileImpl pile = new PileImpl(cards);
        assertFalse(pile.getCard(0).isPresent());
    }

    @Test
    public void testConstructorWithLessThanFourCards() {
        List<Card> cards = createTestCards(3);
        PileImpl pile = new PileImpl(cards);

        assertTrue(pile.getCard(0).isPresent());
        assertTrue(pile.getCard(1).isPresent());
        assertTrue(pile.getCard(2).isPresent());
        assertFalse(pile.getCard(3).isPresent());
    }

    @Test
    public void testGetCardWithValidIndex() {
        List<Card> cards = createTestCards(4);
        PileImpl pile = new PileImpl(cards);

        Optional<Card> card = pile.getCard(2);
        assertTrue(card.isPresent());
    }

    @Test
    public void testGetCardWithNegativeIndex() {
        List<Card> cards = createTestCards(2);
        PileImpl pile = new PileImpl(cards);

        Optional<Card> card = pile.getCard(-1);
        assertFalse(card.isPresent());
    }

    @Test
    public void testGetCardWithIndexOutOfBounds() {
        List<Card> cards = createTestCards(2);
        PileImpl pile = new PileImpl(cards);

        Optional<Card> card = pile.getCard(10);
        assertFalse(card.isPresent());
    }

    @Test
    public void testTakeCardValidIndex() {
        List<Card> cards = createTestCards(5);
        PileImpl pile = new PileImpl(cards);
        Card expectedTakenCard = pile.getCard(2).get();

        pile.takeCard(2);
        assertNotEquals(expectedTakenCard, pile.getCard(2).orElse(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTakeCardInvalidIndexThrowsException() {
        List<Card> cards = createTestCards(2);
        PileImpl pile = new PileImpl(cards);

        pile.takeCard(5);
    }

    @Test
    public void testTakeCardWhenHiddenNotEmpty() {
        List<Card> cards = createTestCards(6);
        PileImpl pile = new PileImpl(cards);

        pile.takeCard(1);

        int visibleCount = 0;
        while (pile.getCard(visibleCount).isPresent()) {
            visibleCount++;
        }
        assertEquals(4, visibleCount);
    }

    @Test
    public void testTakeCardWhenHiddenEmpty() {
        List<Card> cards = createTestCards(3);
        PileImpl pile = new PileImpl(cards);

        pile.takeCard(1);

        int visibleCount = 0;
        while (pile.getCard(visibleCount).isPresent()) {
            visibleCount++;
        }
        assertEquals(2, visibleCount);
    }

    @Test
    public void testRemoveLastCardFromEmptyPile() {
        List<Card> cards = new ArrayList<>();
        PileImpl pile = new PileImpl(cards);

        pile.removeLastCard();
        assertFalse(pile.getCard(0).isPresent());
    }

    @Test
    public void testRemoveLastCardRefillsFromHidden() {
        List<Card> cards = createTestCards(5);
        PileImpl pile = new PileImpl(cards);

        pile.removeLastCard();

        int visibleCount = 0;
        while (pile.getCard(visibleCount).isPresent()) {
            visibleCount++;
        }
        assertEquals(4, visibleCount);
    }


    @Test
    public void testTwoArgsConstructor() {
        LinkedList<Card> visible = new LinkedList<>(createTestCards(2));
        LinkedList<Card> hidden = new LinkedList<>(createTestCards(2));

        PileImpl pile = new PileImpl(hidden, visible);

        assertTrue(pile.getCard(0).isPresent());
        assertTrue(pile.getCard(1).isPresent());
        assertFalse(pile.getCard(2).isPresent());
    }

}
