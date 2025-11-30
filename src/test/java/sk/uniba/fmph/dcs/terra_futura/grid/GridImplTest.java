package sk.uniba.fmph.dcs.terra_futura.grid;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.player.Player;

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

    @Test(expected = IllegalStateException.class)
    public void testCanBeActivatedWithNoPattern() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, mockCard);
        grid.canBeActivated(pos);
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
        GridPosition pos2 = new GridPosition(2, 0);
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

    // Mock Card implementation for testing
    private static class MockCard implements Card {
        @Override
        public boolean canGetResources(List<Resource> resources) {
            return false;
        }

        @Override
        public boolean getResources(List<Resource> resources) {
            return false;
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
