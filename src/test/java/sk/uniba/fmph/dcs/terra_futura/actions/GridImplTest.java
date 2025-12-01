package sk.uniba.fmph.dcs.terra_futura.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.grid.GridImpl;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GridImplTest {

    private GridImpl grid;

    @Before
    public void setUp() {
        grid = new GridImpl();
    }

    @Test
    public void testCanPutCard_ValidPosition() {
        Assert.assertTrue(grid.canPutCard(new GridPosition(0, 0)));
        Assert.assertTrue(grid.canPutCard(new GridPosition(-2, -2)));
        Assert.assertTrue(grid.canPutCard(new GridPosition(2, 2)));
    }

    @Test
    public void testCanPutCard_InvalidPosition() {
        Assert.assertFalse("x > 2", grid.canPutCard(new GridPosition(3, 0)));
        Assert.assertFalse("x < -2", grid.canPutCard(new GridPosition(-3, 0)));
        Assert.assertFalse("y > 2", grid.canPutCard(new GridPosition(0, 3)));
        Assert.assertFalse("y < -2", grid.canPutCard(new GridPosition(0, -3)));
        Assert.assertFalse("Null position", grid.canPutCard(null));
    }

    @Test
    public void testCanPutCard_OccupiedPosition() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, new FakeCard());

        Assert.assertFalse(grid.canPutCard(pos));
    }

    @Test
    public void testPutAndGetCard_Success() {
        GridPosition pos = new GridPosition(1, 1);
        FakeCard card = new FakeCard();

        grid.putCard(pos, card);

        Optional<Card> retrieved = grid.getCard(pos);
        Assert.assertTrue(retrieved.isPresent());
        Assert.assertEquals(card, retrieved.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutCard_ThrowsIfOccupied() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, new FakeCard());
        // Попытка положить вторую карту туда же
        grid.putCard(pos, new FakeCard());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutCard_ThrowsIfOutOfBounds() {
        grid.putCard(new GridPosition(3, 3), new FakeCard());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutCard_ThrowsIfCardNull() {
        grid.putCard(new GridPosition(0, 0), null);
    }

    @Test
    public void testGetCard_EmptyOrNull() {
        Assert.assertFalse(grid.getCard(new GridPosition(0, 0)).isPresent());
        Assert.assertFalse(grid.getCard(null).isPresent());
    }

    @Test
    public void testActivationFlow() {
        GridPosition pos = new GridPosition(0, 0);
        FakeCard card = new FakeCard();

        grid.putCard(pos, card);

        Assert.assertFalse(grid.canBeActivated(pos));

        grid.setActivationPattern(Collections.singletonList(pos));
        Assert.assertTrue(grid.canBeActivated(pos));

        grid.setActivated(pos);

        Assert.assertTrue(grid.state().contains("activated=[" + pos.toString() + "]"));
    }

    @Test
    public void testCanBeActivated_NoCardAtPosition() {
        GridPosition pos = new GridPosition(0, 0);

        grid.setActivationPattern(Collections.singletonList(pos));

        Assert.assertFalse(grid.canBeActivated(pos));
    }

    @Test(expected = IllegalStateException.class)
    public void testSetActivated_ThrowsIfNotActivatable() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, new FakeCard());

        grid.setActivated(pos);
    }

    @Test
    public void testEndTurn_ClearsState() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, new FakeCard());
        grid.setActivationPattern(Collections.singletonList(pos));
        grid.setActivated(pos);

        grid.endTurn();

        Assert.assertFalse(grid.canBeActivated(pos));

        Assert.assertFalse(grid.state().contains("activated=[" + pos));
    }

    @Test
    public void testState() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, new FakeCard());

        String state = grid.state();
        Assert.assertTrue(state.startsWith("GridImpl{"));
        Assert.assertTrue(state.contains("FakeCard"));
    }

    private static class FakeCard implements Card {
        @Override public boolean canGetResources(List<Resource> resources) { return false; }
        @Override public void getResources(List<Resource> resources) {}
        @Override public boolean canPutResources(List<Resource> resources) { return false; }
        @Override public void putResources(List<Resource> resources) {}
        @Override public boolean check(List<Resource> input, List<Resource> output, int pollution) { return false; }
        @Override public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) { return false; }
        @Override public boolean hasAssistance() { return false; }
        @Override public String state() { return "FakeCard"; }
    }
}
