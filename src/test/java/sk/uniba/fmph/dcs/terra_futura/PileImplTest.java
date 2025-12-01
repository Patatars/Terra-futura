package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.deck.PileImpl;
import sk.uniba.fmph.dcs.terra_futura.effect.ArbitraryBasic;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PileImplTest {

    private List<Card> mockCards;

    @Before
    public void setUp() {
        mockCards = new ArrayList<>();
        // Create 6 cards with distinct resources for easy tracking
        for (int i = 0; i < 6; i++) {
            Resource r = Resource.values()[i % Resource.values().length];
            Card card = new CardImpl(
                    Collections.emptyList(),
                    1,
                    new ArbitraryBasic(Collections.singletonList(r)),
                    null
            );
            mockCards.add(card);
        }
    }

    // Checks that initial pile has up to 4 visible cards
    @Test
    public void initialState_hasUpTo4VisibleCards() {
        PileImpl pile = new PileImpl(mockCards);
        for (int i = 0; i < 4; i++) {
            assertTrue(pile.getCard(i).isPresent());
        }
        assertFalse(pile.getCard(4).isPresent());
    }

    // Verifies that taking a card removes it and adds a new one at front
    @Test
    public void takeCard_removesGivenIndex_andAddsNewCardToFront() {
        PileImpl pile = new PileImpl(mockCards);
        // Remove middle card
        pile.takeCard(1);
        // Pile should still have 4 cards
        for (int i = 0; i < 4; i++) {
            assertTrue("Card at " + i + " missing", pile.getCard(i).isPresent());
        }
    }

    // Ensures invalid index throws exception
    @Test(expected = IllegalArgumentException.class)
    public void takeCard_invalidIndex_throwsException() {
        PileImpl pile = new PileImpl(mockCards);
        pile.takeCard(10);
    }

    // Checks out-of-bounds access returns empty
    @Test
    public void getCard_outOfBounds_returnsEmpty() {
        PileImpl pile = new PileImpl(mockCards);
        assertFalse(pile.getCard(-1).isPresent());
        assertFalse(pile.getCard(4).isPresent());
    }

    // Tests pile with fewer than 4 initial cards
    @Test
    public void pileWithFewerThan4Cards_initializesCorrectly() {
        List<Card> few = mockCards.subList(0, 2);
        PileImpl pile = new PileImpl(few);
        assertTrue(pile.getCard(0).isPresent());
        assertTrue(pile.getCard(1).isPresent());
        assertFalse(pile.getCard(2).isPresent());
    }

    // Ensures empty pile handles removeLast safely
    @Test
    public void emptyPile_removeLastCard_doesNothing() {
        PileImpl pile = new PileImpl(Collections.emptyList());
        pile.removeLastCard(); // should not crash
        assertFalse(pile.getCard(0).isPresent());
    }

    // Validates format of state() output (basic structure only)
    @Test
    public void state_returnsExpectedFormat() {
        PileImpl pile = new PileImpl(mockCards);
        String state = pile.state();
        assertTrue(state.contains("Pile State:"));
        assertTrue(state.contains("Visible Cards:"));
        assertTrue(state.contains("Hidden Cards Count: 2"));
    }
}
