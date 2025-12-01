package sk.uniba.fmph.dcs.terra_futura.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.pile.PileImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PileImplTest {

    private PileImpl pile;

    private List<Card> sourceCards;

    @Before
    public void setUp() {
        sourceCards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sourceCards.add(new CardImpl(new ArrayList<>(), i, null, null));
        }

        pile = new PileImpl(new ArrayList<>(sourceCards));
    }

    @Test
    public void testInitialization() {
        Optional<Card> card0 = pile.getCard(0);
        Optional<Card> card1 = pile.getCard(1);

        Assert.assertTrue(card0.isPresent());
        Assert.assertEquals(sourceCards.get(9), card0.get());

        Assert.assertTrue(card1.isPresent());
        Assert.assertEquals(sourceCards.get(8), card1.get());

        Assert.assertFalse(pile.getCard(4).isPresent());
    }

    @Test
    public void testGetCard_OutOfBounds() {
        Assert.assertFalse(pile.getCard(-1).isPresent());
        Assert.assertFalse(pile.getCard(4).isPresent());
        Assert.assertFalse(pile.getCard(100).isPresent());
    }

    @Test
    public void testTakeCard_WithHiddenCardsRefill() {
        Card cardToTake = pile.getCard(1).get();
        pile.takeCard(1);

        for (int i = 0; i < 4; i++) {
            Optional<Card> c = pile.getCard(i);
            if (c.isPresent()) {
                Assert.assertNotEquals("Card 8 should be removed", cardToTake, c.get());
            }
        }

        Assert.assertTrue("Should refill to 4 cards", pile.getCard(3).isPresent());

        boolean foundNewCard = false;
        for (int i = 0; i < 4; i++) {
            if (pile.getCard(i).get() == sourceCards.get(5)) {
                foundNewCard = true;
                break;
            }
        }
        Assert.assertTrue("Should draw new card from hidden pile (card #5)", foundNewCard);
    }

    @Test
    public void testTakeCard_NoHiddenCards_ShrinksPile() {
        List<Card> smallList = new ArrayList<>();
        for (int i = 0; i < 4; i++) smallList.add(new CardImpl(new ArrayList<>(), i, null, null));

        PileImpl smallPile = new PileImpl(smallList);

        smallPile.takeCard(0);

        Assert.assertTrue(smallPile.getCard(0).isPresent());
        Assert.assertTrue(smallPile.getCard(1).isPresent());
        Assert.assertTrue(smallPile.getCard(2).isPresent());
        Assert.assertFalse("Should shrink to 3 cards", smallPile.getCard(3).isPresent());
    }

    @Test
    public void testTakeCard_InvalidIndex() {
        Card c0 = pile.getCard(0).get();

        pile.takeCard(10);
        pile.takeCard(-5);

        Assert.assertEquals(c0, pile.getCard(0).get());
        Assert.assertTrue(pile.getCard(3).isPresent());
    }

    @Test
    public void testRemoveLastCard() {
        pile.removeLastCard();

        Assert.assertTrue("Should still have 4 cards if hidden pile exists", pile.getCard(3).isPresent());

        boolean foundNewCard = false;
        for (int i = 0; i < 4; i++) {
            if (pile.getCard(i).get() == sourceCards.get(5)) {
                foundNewCard = true;
                break;
            }
        }
        Assert.assertTrue("Should refill from hidden", foundNewCard);
    }

    @Test
    public void testRemoveLastCard_EmptyHidden() {
        List<Card> smallList = new ArrayList<>();
        for (int i = 0; i < 4; i++) smallList.add(new CardImpl(new ArrayList<>(), i, null, null));
        PileImpl smallPile = new PileImpl(smallList);

        smallPile.removeLastCard();

        Assert.assertFalse(smallPile.getCard(3).isPresent());
    }

    @Test
    public void testState() {
        String state = pile.state();
        Assert.assertNotNull(state);
        Assert.assertTrue(state.startsWith("PileImpl{cards=["));
        Assert.assertTrue(state.contains("CardImpl"));
    }

    @Test
    public void testConstructor_WithLessThan4Cards() {
        List<Card> tinyList = new ArrayList<>();
        tinyList.add(new CardImpl(new ArrayList<>(), 1, null, null));

        try {
            new PileImpl(tinyList);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IndexOutOfBoundsException
                    || e instanceof java.util.NoSuchElementException);
        }
    }
}
