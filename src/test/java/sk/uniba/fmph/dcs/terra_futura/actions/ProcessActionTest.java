package sk.uniba.fmph.dcs.terra_futura.actions;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.grid.Grid;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Test class for ProcessAction.
 */
public class ProcessActionTest {

    private ProcessAction processAction;
    private FakeGrid grid;
    private FakeCard card1;
    private FakeCard card2;

    @Before
    public void setUp() {
        processAction = new ProcessAction();
        grid = new FakeGrid();
        card1 = new FakeCard();
        card2 = new FakeCard();

        // Setup grid with cards
        grid.putCard(new GridPosition(0, 0), card1);
        grid.putCard(new GridPosition(1, 1), card2);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullTransferService() {
        new ProcessAction(null);
    }

    @Test
    public void testActivateCardWithNullCard() {
        List<Pair<Resource, GridPosition>> inputs = new ArrayList<>();
        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(null, grid, inputs, outputs, pollution);

        assertFalse("activateCard should return false when card is null", result);
    }

    @Test
    public void testActivateCardWithNullGrid() {
        List<Pair<Resource, GridPosition>> inputs = new ArrayList<>();
        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, null, inputs, outputs, pollution);

        assertFalse("activateCard should return false when grid is null", result);
    }

    @Test
    public void testActivateCardSuccessfulTransaction() {
        // Setup: card1 has resources, card2 can accept them
        card1.addResourcesToCard(List.of(Resource.GREEN, Resource.RED));
        card2.setCanAcceptResources(true);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)));
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.YELLOW, new GridPosition(1, 1)));
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertTrue("activateCard should return true for valid transaction", result);
    }

    @Test
    public void testActivateCardInsufficientResources() {
        // Setup: card1 doesn't have required resources
        card1.addResourcesToCard(List.of(Resource.GREEN));
        card2.setCanAcceptResources(true);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)),
                Pair.of(Resource.RED, new GridPosition(0, 0)) // card1 doesn't have RED
        );
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.YELLOW, new GridPosition(1, 1)));
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertFalse("activateCard should return false when resources insufficient", result);
    }

    @Test
    public void testActivateCardCannotPlaceOutput() {
        // Setup: card1 has resources, but card2 cannot accept them
        card1.addResourcesToCard(List.of(Resource.GREEN));
        card2.setCanAcceptResources(false);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)));
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.YELLOW, new GridPosition(1, 1)));
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertFalse("activateCard should return false when output cannot be placed", result);
    }

    @Test
    public void testActivateCardWithPollution() {
        // Setup: card1 has resources, card2 can accept pollution
        card1.addResourcesToCard(List.of(Resource.GREEN));
        card2.setCanAcceptResources(true);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)));
        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();
        List<GridPosition> pollution = List.of(new GridPosition(1, 1));

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertTrue("activateCard should return true with pollution", result);
        assertTrue("Card should have received pollution", card2.getReceivedResources().contains(Resource.POLLUTION));
    }

    @Test
    public void testActivateCardMultipleResourcesFromSameCard() {
        // Setup: card1 has multiple resources
        card1.addResourcesToCard(List.of(Resource.GREEN, Resource.GREEN, Resource.RED));
        card2.setCanAcceptResources(true);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)),
                Pair.of(Resource.GREEN, new GridPosition(0, 0)),
                Pair.of(Resource.RED, new GridPosition(0, 0)));
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.YELLOW, new GridPosition(1, 1)));
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertTrue("Should handle multiple resources from same card", result);
        assertEquals("Card1 should have 0 resources left", 0, card1.resources.size());
    }

    @Test
    public void testActivateCardMultipleOutputsToSameCard() {
        // Setup: card1 has resources, card2 can accept multiple
        card1.addResourcesToCard(List.of(Resource.GREEN));
        card2.setCanAcceptResources(true);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)));
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.YELLOW, new GridPosition(1, 1)),
                Pair.of(Resource.RED, new GridPosition(1, 1)),
                Pair.of(Resource.MONEY, new GridPosition(1, 1)));
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertTrue("Should handle multiple outputs to same card", result);
        assertEquals("Card2 should receive 3 resources", 3, card2.getReceivedResources().size());
    }

    @Test
    public void testActivateCardResourcesFromMultipleCards() {
        // Setup: both cards have resources
        FakeCard card3 = new FakeCard();
        grid.putCard(new GridPosition(2, 2), card3);

        card1.addResourcesToCard(List.of(Resource.GREEN, Resource.RED));
        card2.addResourcesToCard(List.of(Resource.YELLOW));
        card3.setCanAcceptResources(true);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)),
                Pair.of(Resource.YELLOW, new GridPosition(1, 1)));
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.MONEY, new GridPosition(2, 2)));
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertTrue("Should handle inputs from multiple cards", result);
        assertEquals("Card1 should have 1 resource (RED) left", 1, card1.resources.size());
        assertEquals("Card2 should have 0 resources left", 0, card2.resources.size());
    }

    @Test
    public void testActivateCardOutputsToMultipleCards() {
        // Setup
        FakeCard card3 = new FakeCard();
        grid.putCard(new GridPosition(2, 2), card3);

        card1.addResourcesToCard(List.of(Resource.GREEN));
        card2.setCanAcceptResources(true);
        card3.setCanAcceptResources(true);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)));
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.YELLOW, new GridPosition(1, 1)),
                Pair.of(Resource.RED, new GridPosition(2, 2)));
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertTrue("Should handle outputs to multiple cards", result);
        assertTrue("Card2 should receive resources", card2.getReceivedResources().contains(Resource.YELLOW));
        assertTrue("Card3 should receive resources", card3.getReceivedResources().contains(Resource.RED));
    }

    @Test
    public void testActivateCardPollutionToMultipleCards() {
        // Setup
        FakeCard card3 = new FakeCard();
        grid.putCard(new GridPosition(2, 2), card3);

        card1.addResourcesToCard(List.of(Resource.GREEN));
        card2.setCanAcceptResources(true);
        card3.setCanAcceptResources(true);

        List<Pair<Resource, GridPosition>> inputs = new ArrayList<>();
        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();
        List<GridPosition> pollution = List.of(
                new GridPosition(1, 1),
                new GridPosition(2, 2));

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertTrue("Should handle pollution to multiple cards", result);
        assertTrue("Card2 should have pollution", card2.getReceivedResources().contains(Resource.POLLUTION));
        assertTrue("Card3 should have pollution", card3.getReceivedResources().contains(Resource.POLLUTION));
    }

    @Test
    public void testActivateCardCombinedOutputsAndPollutionOnSameCard() {
        // Setup: card2 receives both output and pollution
        card1.addResourcesToCard(List.of(Resource.GREEN));
        card2.setCanAcceptResources(true);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)));
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.YELLOW, new GridPosition(1, 1)));
        List<GridPosition> pollution = List.of(new GridPosition(1, 1));

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertTrue("Should handle outputs and pollution on same card", result);
        List<Resource> received = card2.getReceivedResources();
        assertTrue("Card2 should have YELLOW", received.contains(Resource.YELLOW));
        assertTrue("Card2 should have POLLUTION", received.contains(Resource.POLLUTION));
        assertEquals("Card2 should have exactly 2 items", 2, received.size());
    }

    @Test
    public void testActivateCardInvalidGridPosition() {
        // Try to get resources from position without a card
        card1.addResourcesToCard(List.of(Resource.GREEN));

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(5, 5)) // No card here
        );
        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertFalse("Should fail when input position has no card", result);
    }

    @Test
    public void testActivateCardInvalidOutputPosition() {
        // Try to put resources to position without a card
        card1.addResourcesToCard(List.of(Resource.GREEN));

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)));
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.YELLOW, new GridPosition(9, 9)) // No card here
        );
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertFalse("Should fail when output position has no card", result);
    }

    @Test
    public void testActivateCardInvalidPollutionPosition() {
        // Try to add pollution to position without a card
        List<Pair<Resource, GridPosition>> inputs = new ArrayList<>();
        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();
        List<GridPosition> pollution = List.of(new GridPosition(7, 7));

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertFalse("Should fail when pollution position has no card", result);
    }

    @Test
    public void testActivateCardAtomicityOnFailure() {
        // Setup: first card has resources, second cannot accept
        FakeCard card3 = new FakeCard();
        grid.putCard(new GridPosition(2, 2), card3);

        card1.addResourcesToCard(List.of(Resource.GREEN, Resource.RED));
        card2.setCanAcceptResources(true);
        card3.setCanAcceptResources(false); // This will cause failure

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)));
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.YELLOW, new GridPosition(1, 1)),
                Pair.of(Resource.MONEY, new GridPosition(2, 2)) // This will fail
        );
        List<GridPosition> pollution = new ArrayList<>();

        // Store initial state
        int initialCard1Resources = card1.resources.size();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertFalse("Should fail due to card3 cannot accept", result);
        assertEquals("Card1 resources should be unchanged (atomic)", initialCard1Resources, card1.resources.size());
        assertEquals("Card2 should not receive anything (atomic)", 0, card2.getReceivedResources().size());
    }

    @Test
    public void testActivateCardComplexScenario() {
        // Complex scenario: multiple inputs, outputs, and pollution
        FakeCard card3 = new FakeCard();
        FakeCard card4 = new FakeCard();
        grid.putCard(new GridPosition(2, 2), card3);
        grid.putCard(new GridPosition(3, 3), card4);

        card1.addResourcesToCard(List.of(Resource.GREEN, Resource.GREEN, Resource.RED));
        card2.addResourcesToCard(List.of(Resource.YELLOW));
        card3.setCanAcceptResources(true);
        card4.setCanAcceptResources(true);

        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(0, 0)),
                Pair.of(Resource.GREEN, new GridPosition(0, 0)),
                Pair.of(Resource.YELLOW, new GridPosition(1, 1)));
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.MONEY, new GridPosition(2, 2)),
                Pair.of(Resource.MONEY, new GridPosition(2, 2)),
                Pair.of(Resource.RED, new GridPosition(3, 3)));
        List<GridPosition> pollution = List.of(
                new GridPosition(2, 2),
                new GridPosition(3, 3));

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertTrue("Complex scenario should succeed", result);
        assertEquals("Card1 should have 1 resource left (RED)", 1, card1.resources.size());
        assertEquals("Card2 should have 0 resources left", 0, card2.resources.size());
        assertEquals("Card3 should receive 3 items (2 MONEY + 1 POLLUTION)", 3, card3.getReceivedResources().size());
        assertEquals("Card4 should receive 2 items (1 RED + 1 POLLUTION)", 2, card4.getReceivedResources().size());
    }

    @Test
    public void testActivateCardEmptyLists() {
        // Test with all empty lists - should succeed
        List<Pair<Resource, GridPosition>> inputs = new ArrayList<>();
        List<Pair<Resource, GridPosition>> outputs = new ArrayList<>();
        List<GridPosition> pollution = new ArrayList<>();

        boolean result = processAction.activateCard(card1, grid, inputs, outputs, pollution);

        assertTrue("activateCard should return true with empty lists", result);
    }

    // Fake implementations for testing

    private static class FakeCard implements Card {
        private final List<Resource> resources = new ArrayList<>();

        private final List<Resource> receivedResources = new ArrayList<>();
        private boolean canAcceptResources = true;

        public void addResourcesToCard(List<Resource> res) {
            resources.addAll(res);
        }

        public void setCanAcceptResources(boolean can) {
            this.canAcceptResources = can;
        }

        public List<Resource> getReceivedResources() {
            return receivedResources;
        }

        @Override
        public boolean canGetResources(List<Resource> resources) {
            List<Resource> temp = new ArrayList<>(this.resources);
            for (Resource r : resources) {
                if (!temp.remove(r)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean getResources(List<Resource> resources) {
            if (!canGetResources(resources)) {
                return false;
            }
            for (Resource r : resources) {
                this.resources.remove(r);
            }
            return true;
        }

        @Override
        public boolean canPutResources(List<Resource> resources) {
            return canAcceptResources;
        }

        @Override
        public void putResources(List<Resource> resources) {
            receivedResources.addAll(resources);
        }

        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return true;
        }

        @Override
        public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
            return true;
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

    private static class FakeGrid implements Grid {
        private final Map<GridPosition, Card> cards = new HashMap<>();

        @Override
        public Optional<Card> getCard(GridPosition coordinate) {
            return Optional.ofNullable(cards.get(coordinate));
        }

        @Override
        public boolean canPutCard(GridPosition coordinate) {
            return !cards.containsKey(coordinate);
        }

        @Override
        public void putCard(GridPosition coordinate, Card card) {
            cards.put(coordinate, card);
        }

        @Override
        public boolean canBeActivated(GridPosition coordinate) {
            return cards.containsKey(coordinate);
        }

        @Override
        public void setActivated(GridPosition coordinate) {
            // No-op for fake
        }

        @Override
        public void endTurn() {
            // No-op for fake
        }

        @Override
        public String state() {
            return "{}";
        }

        @Override
        public void setActivationPattern(Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern) {

        }
    }
}
