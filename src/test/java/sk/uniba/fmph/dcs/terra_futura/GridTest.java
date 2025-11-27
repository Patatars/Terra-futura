package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.GridImpl;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;


import java.util.*;

import static org.junit.Assert.*;

// Unit tests for GridImpl.

public class GridTest {

    /**
     * Minimal Card implementation so tests can run.
     */
    static class TestCard implements Card {
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
            return "TestCard";
        }
    }

    private GridImpl grid;

    // Some coordinates that are used repeatedly in tests
    private final GridPosition P0 = new GridPosition(0, 0);
    private final GridPosition P1 = new GridPosition(1, 1);
    private final GridPosition P2 = new GridPosition(-1, 0);

    @Before
    public void setUp() {
        grid = new GridImpl();
    }

    /**
     * SITUATION 1:
     * Putting a card on an EMPTY position should succeed.
     * After putting the card, getCard() should return it.
     */
    @Test
    public void testPutCardSuccessfully() {
        Card card = new TestCard();

        // Position is empty -> canPutCard should be true
        assertTrue(grid.canPutCard(P0));

        grid.putCard(P0, card);

        // After placing, getCard should return the card
        Optional<Card> result = grid.getCard(P0);
        assertTrue(result.isPresent());
        assertEquals(card, result.get());
    }


    /**
     * SITUATION 2:
     * Getting a card from a position where NO card exists.
     * getCard() should return Optional.empty().
     */
    @Test
    public void testGetCardEmptyPosition() {
        assertTrue(grid.getCard(P1).isEmpty());
    }

    /**
     * SITUATION 3:
     * Checking whether a card CAN be activated.
     *
     * A card can be activated ONLY IF:
     *  - the card exists
     *  - the coordinate is listed in the activation pattern
     *
     * We test three different coordinates:
     *  - P0: card exists + in activation pattern ⇒ canBeActivated = true
     *  - P1: no card ⇒ false
     *  - P2: card exists? no; pattern contains it? no ⇒ false
     */
    @Test
    public void testCanBeActivatedOnlyIfInPatternAndExists() {
        Card card = new TestCard();
        grid.putCard(P0, card);

        // Activation pattern contains only P0
        grid.setActivationPattern(List.of(P0));

        assertTrue(grid.canBeActivated(P0));  // valid
        assertFalse(grid.canBeActivated(P1)); // no card exists there
        assertFalse(grid.canBeActivated(P2)); // not in pattern
    }



    /**
     * SITUATION 4:
     * endTurn() should clear:
     *  - activatedThisTurn
     *  - activationPattern
     *
     * After calling endTurn(), even previously valid activations become invalid.
     */
    @Test
    public void testEndTurnResetsActivationsAndPattern() {
        Card card = new TestCard();
        grid.putCard(P0, card);
        grid.setActivationPattern(List.of(P0));
        grid.setActivated(P0);

        grid.endTurn();

        // Now the activation pattern is empty -> activation should no longer be allowed
        assertFalse(grid.canBeActivated(P0));
    }

    /**
     * SITUATION 5:
     * state() should return a string containing readable information.
     * This test checks that it does NOT crash and includes the expected fields.
     */
    @Test
   public void testStateDoesNotCrashAndContainsExpectedParts() {
        Card card = new TestCard();
        grid.putCard(P0, card);
        grid.setActivationPattern(List.of(P0));

        String state = grid.state();

        // Check that state() contains some meaningful identifiers
        assertTrue(state.contains("Grid"));
        assertTrue(state.contains("TestCard"));
        assertTrue(state.contains("activationPattern"));
        assertTrue(state.contains("activatedThisTurn"));
    }

    /**
     * SITUATION 6:
     * Setting a NEW activation pattern should overwrite the old one.
     * After changing it, activation checks use the new pattern.
     */
    @Test
    public void testActivationPatternOverwrite() {
        // First pattern
        grid.setActivationPattern(List.of(P0));

        // Now overwrite pattern with new coordinates
        grid.setActivationPattern(List.of(P1, P2));

        // There is still no card at any of these positions, so activation = false
        assertFalse(grid.canBeActivated(P0));
        assertFalse(grid.canBeActivated(P1));
        assertFalse(grid.canBeActivated(P2));
    }

    /**
     * SITUATION 7:
     * My GridImpl has TWO setActivationPattern methods:
     *   1) List<GridPosition>          (used normally)
     *   2) Collection<SimpleEntry>     (EMPTY implementation)
     *
     * This test checks that calling the "empty" one:
     *  - does NOT crash
     *  - does NOT modify the real activation pattern
     */
    @Test
    public void testSetActivationPatternWithDifferentCollectionSignatureDoesNothing() {

        Collection<AbstractMap.SimpleEntry<Integer,Integer>> pattern =
                List.of(
                        new AbstractMap.SimpleEntry<>(0, 0),
                        new AbstractMap.SimpleEntry<>(1, 1)
                );

        // This method does nothing — must not crash
        grid.setActivationPattern(pattern);

        // Activation still impossible (pattern remained empty)
        assertFalse(grid.canBeActivated(P0));
    }
}
