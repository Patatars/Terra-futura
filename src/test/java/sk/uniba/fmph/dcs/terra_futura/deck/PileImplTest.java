package sk.uniba.fmph.dcs.terra_futura.deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PileImplTest {

    private PileImpl pile;
    private CardImpl card1;
    private CardImpl card2;
    private CardImpl card3;

    @BeforeEach
    void setUp() {
        List<Resource> resources = List.of(Resource.MONEY, Resource.GREEN);
        card1 = new CardImpl(resources, 1);
        card2 = new CardImpl(resources, 1);
        card3 = new CardImpl(resources, 1);
        List<CardImpl> cards = List.of(card1, card2, card3);
        pile = new PileImpl(cards);
    }

    @Test
    void getCard_WithValidIndex_ShouldReturnCard() {
        // When
        Optional<Card> result = pile.getCard(1);

        // Then
        assertTrue(result.isPresent(), "Card should be present");
        assertEquals(card2, result.get(), "Should return correct card");
    }

    @Test
    void getCard_WithNegativeIndex_ShouldReturnEmpty() {
        // When
        Optional<Card> result = pile.getCard(-1);

        // Then
        assertTrue(result.isEmpty(), "Should return empty for negative index");
    }

    @Test
    void getCard_WithIndexOutOfBounds_ShouldReturnEmpty() {
        // When
        Optional<Card> result = pile.getCard(100);

        // Then
        assertTrue(result.isEmpty(), "Should return empty for out-of-bounds index");
    }

    @Test
    void getCard_WithLastIndex_ShouldReturnLastCard() {
        // When
        Optional<Card> result = pile.getCard(2);

        // Then
        assertTrue(result.isPresent(), "Last card should be present");
        assertEquals(card3, result.get(), "Should return last card");
    }

    @Test
    void takeCard_WithValidIndex_ShouldRemoveCard() {
        // Given
        int initialSize = pile.cards.size();

        // When
        pile.takeCardP(1);

        // Then
        assertEquals(initialSize - 1, pile.cards.size(), "Pile size should decrease");
        assertFalse(pile.cards.contains(card2), "Card should be removed from pile");
    }

    @Test
    void takeCard_WithInvalidIndex_ShouldNotModifyPile() {
        // Given
        int initialSize = pile.cards.size();

        // When
        pile.takeCardP(-1);
        pile.takeCardP(100);

        // Then
        assertEquals(initialSize, pile.cards.size(), "Pile size should remain unchanged");
    }

    @Test
    void takeCard_WithFirstIndex_ShouldRemoveFirstCard() {
        // When
        pile.takeCardP(0);

        // Then
        assertEquals(2, pile.cards.size(), "Should have 2 cards remaining");
        assertFalse(pile.cards.contains(card1), "First card should be removed");
        assertTrue(pile.cards.contains(card2), "Second card should remain");
        assertTrue(pile.cards.contains(card3), "Third card should remain");
    }

    @Test
    void removeLastCard_OnNonEmptyPile_ShouldRemoveLastCard() {
        // Given
        int initialSize = pile.cards.size();

        // When
        pile.removeLastCard();

        // Then
        assertEquals(initialSize - 1, pile.cards.size(), "Pile size should decrease");
        assertFalse(pile.cards.contains(card3), "Last card should be removed");
        assertTrue(pile.cards.contains(card1), "First card should remain");
        assertTrue(pile.cards.contains(card2), "Second card should remain");
    }

    @Test
    void removeLastCard_OnEmptyPile_ShouldThrowException() {
        // Given
        PileImpl emptyPile = new PileImpl(new ArrayList<>());

        // When & Then
        assertThrows(IllegalStateException.class,
                emptyPile::removeLastCard,
                "Should throw exception when removing from empty pile");
    }

    @Test
    void removeLastCard_MultipleTimes_ShouldRemoveAllCards() {
        // When
        pile.removeLastCard(); // Remove card3
        pile.removeLastCard(); // Remove card2
        pile.removeLastCard(); // Remove card1

        // Then
        assertTrue(pile.cards.isEmpty(), "Pile should be empty after removing all cards");
    }

    @Test
    void state_ShouldReturnCorrectFormat() {
        // When
        String state = pile.state();

        // Then
        assertNotNull(state, "State should not be null");
        assertTrue(state.contains("PileImpl"), "State should contain class name");
        assertTrue(state.contains("cards="), "State should contain cards information");
    }

    @Test
    void state_OnEmptyPile_ShouldReturnEmptyState() {
        // Given
        PileImpl emptyPile = new PileImpl(new ArrayList<>());

        // When
        String state = emptyPile.state();

        // Then
        assertNotNull(state, "State should not be null");
        assertTrue(state.contains("PileImpl"), "State should contain class name");
        assertTrue(state.contains("cards=[]") || state.contains("size=0"),
                "State should indicate empty pile");
    }
}
