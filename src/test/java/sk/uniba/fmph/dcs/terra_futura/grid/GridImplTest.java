package sk.uniba.fmph.dcs.terra_futura.grid;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.*;

import static org.junit.Assert.*;

public class GridImplTest {
    private GridImpl grid;
    private Card mockCard;

    @Before
    public void setUp() {
        grid = new GridImpl();
        mockCard = new MockCard();
    }

    @Test
    public void testCanPutCardOnEmptyPosition() {
        GridPosition pos = new GridPosition(0, 0);
        assertTrue(grid.canPutCard(pos));
    }

    @Test
    public void testPutCardAddsCardToGrid() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, mockCard);
        assertTrue(grid.getCard(pos).isPresent());
        assertEquals(mockCard, grid.getCard(pos).get());
    }

    @Test
    public void testCannotPutCardOnOccupiedPosition() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, mockCard);
        assertFalse(grid.canPutCard(pos));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutCardThrowsOnOccupiedPosition() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, mockCard);
        grid.putCard(pos, mockCard); // Should throw
    }

    @Test
    public void testGetCardReturnsEmptyForNonexistentCard() {
        GridPosition pos = new GridPosition(5, 5);
        assertFalse(grid.getCard(pos).isPresent());
    }

    @Test
    public void testCanBeActivatedWithNoPattern() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, mockCard);
        assertFalse(grid.canBeActivated(pos));
    }

    @Test
    public void testCannotBeActivatedWithoutCard() {
        GridPosition pos = new GridPosition(0, 0);
        assertFalse(grid.canBeActivated(pos));
    }

    @Test
    public void testSetActivatedMarksCardAsActivated() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, mockCard);
        grid.setActivationPattern(Collections.singleton(new AbstractMap.SimpleEntry<>(0, 0)));
        assertTrue(grid.canBeActivated(pos));
        grid.setActivated(pos);
        assertFalse(grid.canBeActivated(pos)); // Cannot activate twice in same turn
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetActivatedThrowsWhenCannotActivate() {
        GridPosition pos = new GridPosition(0, 0);
        grid.setActivated(pos); // Should throw - no card at position
    }

    @Test
    public void testEndTurnResetsActivations() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, mockCard);
        grid.setActivationPattern(Collections.singleton(new AbstractMap.SimpleEntry<>(0, 0)));
        grid.setActivated(pos);
        assertFalse(grid.canBeActivated(pos));

        grid.endTurn();
        assertTrue(grid.canBeActivated(pos));
    }

    @Test
    public void testSetActivationPatternWithList() {
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(1, 0);
        grid.putCard(pos1, mockCard);
        grid.putCard(pos2, mockCard);

        var pattern = List.of(new AbstractMap.SimpleEntry<>(0, 0),
                new AbstractMap.SimpleEntry<>(1,0));

        grid.setActivationPattern(pattern);

        // Should be able to activate cards matching pattern
        assertTrue(grid.canBeActivated(pos1));
        assertTrue(grid.canBeActivated(pos2));
    }

    @Test
    public void testSetActivationPatternWithCollection() {
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(1, 0);
        grid.putCard(pos1, mockCard);
        grid.putCard(pos2, mockCard);

        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern = new ArrayList<>();
        pattern.add(new AbstractMap.SimpleEntry<>(0, 0));
        pattern.add(new AbstractMap.SimpleEntry<>(1, 0));
        grid.setActivationPattern(pattern);

        // Should be able to activate cards matching pattern
        assertTrue(grid.canBeActivated(pos1));
        assertTrue(grid.canBeActivated(pos2));
    }

    @Test
    public void testCannotActivateCardNotInPattern() {
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(1, 0);
        grid.putCard(pos1, mockCard);
        grid.putCard(pos2, mockCard);

        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern = new ArrayList<>();
        pattern.add(new AbstractMap.SimpleEntry<>(0, 0));
        grid.setActivationPattern(pattern);

        assertTrue(grid.canBeActivated(pos1));
        assertFalse(grid.canBeActivated(pos2));
    }

    @Test
    public void testActivationPatternWithOffset() {
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(1, 0);
        grid.putCard(pos1, mockCard);
        grid.putCard(pos2, mockCard);

        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern = new ArrayList<>();
        pattern.add(new AbstractMap.SimpleEntry<>(1, 0));
        grid.setActivationPattern(pattern);

        assertTrue(grid.canBeActivated(pos2));
    }


    @Test
    public void testStateIncludesCards() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, mockCard);
        String state = grid.state();
        assertTrue(state.contains("\"cards\""));
    }

    @Test
    public void testCanPutCardWithinBounds() {
        assertTrue(grid.canPutCard(new GridPosition(0, 0)));
        assertTrue(grid.canPutCard(new GridPosition(2, 2)));
        assertTrue(grid.canPutCard(new GridPosition(1, 1)));
    }

    @Test
    public void testCannotPutCardOutOfBounds() {
        assertFalse(grid.canPutCard(new GridPosition(-1, 0)));
        assertFalse(grid.canPutCard(new GridPosition(0, -1)));
        assertFalse(grid.canPutCard(new GridPosition(3, 0)));
        assertFalse(grid.canPutCard(new GridPosition(0, 3)));
        assertFalse(grid.canPutCard(new GridPosition(5, 5)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutCardOutOfBoundsThrows() {
        grid.putCard(new GridPosition(3, 3), mockCard);
    }

    @Test
    public void testFirstCardCanBePlacedAnywhere() {
        assertTrue(grid.canPutCard(new GridPosition(0, 0)));
        assertTrue(grid.canPutCard(new GridPosition(1, 1)));
        assertTrue(grid.canPutCard(new GridPosition(2, 2)));
    }

    @Test
    public void testSecondCardMustBeAdjacent() {
        grid.putCard(new GridPosition(1, 1), mockCard);

        assertTrue(grid.canPutCard(new GridPosition(0, 1)));
        assertTrue(grid.canPutCard(new GridPosition(2, 1)));
        assertTrue(grid.canPutCard(new GridPosition(1, 0)));
        assertTrue(grid.canPutCard(new GridPosition(1, 2)));

        assertFalse(grid.canPutCard(new GridPosition(0, 0)));
        assertFalse(grid.canPutCard(new GridPosition(2, 2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotPutCardNonAdjacent() {
        grid.putCard(new GridPosition(0, 0), mockCard);
        grid.putCard(new GridPosition(2, 2), mockCard);
    }

    @Test
    public void testPutCardChainedAdjacent() {
        grid.putCard(new GridPosition(0, 0), mockCard);
        grid.putCard(new GridPosition(1, 0), mockCard);
        grid.putCard(new GridPosition(2, 0), mockCard);

        assertTrue(grid.getCard(new GridPosition(0, 0)).isPresent());
        assertTrue(grid.getCard(new GridPosition(1, 0)).isPresent());
        assertTrue(grid.getCard(new GridPosition(2, 0)).isPresent());
    }

    @Test
    public void testPutCardReturnsCorrectCard() {
        GridPosition pos = new GridPosition(1, 1);
        Card differentCard = new MockCard();
        grid.putCard(pos, differentCard);

        Optional<Card> retrieved = grid.getCard(pos);
        assertTrue(retrieved.isPresent());
        assertSame(differentCard, retrieved.get());
    }

    @Test
    public void testCanPutCardAfterAdjacentPlaced() {
        grid.putCard(new GridPosition(0, 0), mockCard);
        assertTrue(grid.canPutCard(new GridPosition(1, 0)));
        assertTrue(grid.canPutCard(new GridPosition(0, 1)));
        assertFalse(grid.canPutCard(new GridPosition(1, 1))); // Diagonal, not adjacent
        assertFalse(grid.canPutCard(new GridPosition(2, 0))); // Not adjacent
    }

    @Test
    public void testMultipleAdjacentPositionsAfterSecondCard() {
        grid.putCard(new GridPosition(1, 1), mockCard);
        grid.putCard(new GridPosition(1, 0), mockCard);

        assertTrue(grid.canPutCard(new GridPosition(0, 0)));
        assertTrue(grid.canPutCard(new GridPosition(2, 0)));
        assertTrue(grid.canPutCard(new GridPosition(0, 1)));
        assertTrue(grid.canPutCard(new GridPosition(2, 1)));
        assertTrue(grid.canPutCard(new GridPosition(1, 2)));
    }

    @Test
    public void testCanActivateMultipleCardsInPattern() {
        grid.putCard(new GridPosition(1, 1), mockCard);
        grid.putCard(new GridPosition(1, 0), mockCard);
        grid.putCard(new GridPosition(0, 1), mockCard);

        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern = Arrays.asList(
            new AbstractMap.SimpleEntry<>(1, 1),
            new AbstractMap.SimpleEntry<>(1, 0),
            new AbstractMap.SimpleEntry<>(0, 1)
        );
        grid.setActivationPattern(pattern);

        assertTrue(grid.canBeActivated(new GridPosition(1, 1)));
        assertTrue(grid.canBeActivated(new GridPosition(1, 0)));
        assertTrue(grid.canBeActivated(new GridPosition(0, 1)));

        grid.setActivated(new GridPosition(1, 1));

        assertFalse(grid.canBeActivated(new GridPosition(1, 1)));
        assertTrue(grid.canBeActivated(new GridPosition(1, 0)));
        assertTrue(grid.canBeActivated(new GridPosition(0, 1)));
    }

    @Test
    public void testActivateAllCardsInPattern() {
        grid.putCard(new GridPosition(0, 0), mockCard);
        grid.putCard(new GridPosition(1, 0), mockCard);

        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern = Arrays.asList(
            new AbstractMap.SimpleEntry<>(0, 0),
            new AbstractMap.SimpleEntry<>(1, 0)
        );
        grid.setActivationPattern(pattern);

        grid.setActivated(new GridPosition(0, 0));
        grid.setActivated(new GridPosition(1, 0));

        assertFalse(grid.canBeActivated(new GridPosition(0, 0)));
        assertFalse(grid.canBeActivated(new GridPosition(1, 0)));

        grid.endTurn();
        assertTrue(grid.canBeActivated(new GridPosition(0, 0)));
        assertTrue(grid.canBeActivated(new GridPosition(1, 0)));
    }

    // Mock Card implementation for testing
    private static class MockCard implements Card {
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
            return "{}";
        }
    }
}
