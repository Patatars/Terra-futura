package sk.uniba.fmph.dcs.terra_futura.deck;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.effect.Effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class PileImplTest {

    private PileImpl pile;
    private CardImpl card1;
    private CardImpl card2;
    private CardImpl card3;
    private CardImpl card4;
    private CardImpl card5;

    @Before
    public void setUp() {
        List<Resource> res = List.of(Resource.MONEY, Resource.GREEN);
        Effect dummyEffect = new Effect() {
            @Override
            public boolean check(List<Resource> input, List<Resource> output, int pollution) {
                return true;
            }

            @Override
            public boolean hasAssistance() {
                return false;
            }

            @Override
            public String state() {
                return "dummy";
            }
        };

        card1 = new CardImpl(new ArrayList<>(res), 1, dummyEffect, dummyEffect);
        card2 = new CardImpl(new ArrayList<>(res), 1, dummyEffect, dummyEffect);
        card3 = new CardImpl(new ArrayList<>(res), 1, dummyEffect, dummyEffect);
        card4 = new CardImpl(new ArrayList<>(res), 1, dummyEffect, dummyEffect);
        card5 = new CardImpl(new ArrayList<>(res), 1, dummyEffect, dummyEffect);

        List<Card> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        cards.add(card5);

        pile = new PileImpl(cards);
    }

    @Test
    public void testInitialVisibleCards() {
        // должно быть максимум 4 видимых карт
        Optional<Card> c0 = pile.getCard(0);
        assertTrue(c0.isPresent());
        assertEquals(card1, c0.get());

        Optional<Card> c3 = pile.getCard(3);
        assertTrue(c3.isPresent());
        assertEquals(card4, c3.get());

        // индекс вне диапазона
        assertFalse(pile.getCard(4).isPresent());
    }

    @Test
    public void testTakeCardValidIndex() {
        pile.takeCard(0); // берём первую карту
        // после взятия карта1 должна исчезнуть из видимых
        assertFalse(pile.getCard(0).get().equals(card1));
        // видимых карт всё ещё должно быть 4 (пополнено из hidden)
        assertEquals(4, pile.state().split("\n").length - 3); // строки с картами
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTakeCardInvalidIndexThrows() {
        pile.takeCard(10);
    }

    @Test
    public void testRemoveLastCard() {
        pile.removeLastCard();
        // последняя видимая карта (card4) должна быть удалена
        assertFalse(pile.getCard(3).isPresent());
        // но на её место должна прийти card5 из hidden
        assertTrue(pile.getCard(0).isPresent());
    }

    @Test
    public void testRemoveLastCardOnEmptyVisible() {
        // создаём пустую стопку
        PileImpl emptyPile = new PileImpl(new ArrayList<>());
        emptyPile.removeLastCard(); // не должно упасть
        assertEquals("DeckPile:\nVisible:\nHidden count: 0", emptyPile.state());
    }

    @Test
    public void testStateContainsCards() {
        String state = pile.state();
        assertTrue(state.contains("DeckPile:"));
        assertTrue(state.contains("Visible:"));
        assertTrue(state.contains("Hidden count"));
    }
}
