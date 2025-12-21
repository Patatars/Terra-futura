package sk.uniba.fmph.dcs.terra_futura.actions;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SelectRewardTest {

    private SelectReward selectReward;
    private MockCard mockCard;

    @Before
    public void setUp() {
        selectReward = new SelectReward();
        mockCard = new MockCard();
    }

    @Test
    public void testInitialState() {
        assertEquals("Player: None, Selection: []", selectReward.state());
    }

    @Test
    public void testSetReward() {
        boolean result = selectReward.setReward(1, mockCard, List.of(Resource.GREEN, Resource.RED));
        assertTrue("setReward should return true", result);
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
        assertTrue(selectReward.canSelectReward(Resource.RED));
        assertFalse(selectReward.canSelectReward(Resource.YELLOW));
        assertEquals("Player: 1, Selection: [GREEN, RED]", selectReward.state());
    }

    @Test
    public void testSetRewardWithNullReward() {
        boolean result = selectReward.setReward(1, mockCard, null);
        assertFalse("setReward should return false for null reward", result);
    }

    @Test
    public void testSelectReward() {
        selectReward.setReward(1, mockCard, List.of(Resource.GREEN, Resource.RED));

        selectReward.selectReward(Resource.GREEN);
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
        assertTrue(selectReward.canSelectReward(Resource.RED));
        assertEquals("Player: 1, Selection: [RED]", selectReward.state());
        assertEquals(List.of(Resource.GREEN), mockCard.getReceivedResources());

        selectReward.selectReward(Resource.RED);
        assertFalse(selectReward.canSelectReward(Resource.RED));
        assertEquals("Player: 1, Selection: []", selectReward.state());
        assertEquals(List.of(Resource.GREEN, Resource.RED), mockCard.getReceivedResources());
    }

    @Test
    public void testSelectInvalidReward() {
        selectReward.setReward(1, mockCard, List.of(Resource.GREEN));

        selectReward.selectReward(Resource.RED);
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
        assertEquals("Player: 1, Selection: [GREEN]", selectReward.state());
        assertTrue(mockCard.getReceivedResources().isEmpty());
    }

    @Test
    public void testCardReceivesRewardOnSelection() {
        selectReward.setReward(1, mockCard, List.of(Resource.YELLOW, Resource.GREEN, Resource.RED));

        assertTrue(mockCard.getReceivedResources().isEmpty());

        selectReward.selectReward(Resource.YELLOW);
        assertEquals(1, mockCard.getReceivedResources().size());
        assertEquals(Resource.YELLOW, mockCard.getReceivedResources().get(0));

        selectReward.selectReward(Resource.GREEN);
        assertEquals(2, mockCard.getReceivedResources().size());
        assertEquals(List.of(Resource.YELLOW, Resource.GREEN), mockCard.getReceivedResources());
    }

    @Test
    public void testClearResetsState() {
        selectReward.setReward(1, mockCard, List.of(Resource.GREEN, Resource.RED));
        assertTrue(selectReward.canSelectReward(Resource.GREEN));

        selectReward.clear();

        assertFalse(selectReward.canSelectReward(Resource.GREEN));
        assertEquals("Player: None, Selection: []", selectReward.state());
    }

    @Test
    public void testSetRewardWithEmptyList() {
        boolean result = selectReward.setReward(1, mockCard, List.of());
        assertFalse("setReward should return false for empty reward list", result);
        assertEquals("Player: None, Selection: []", selectReward.state());
    }

    @Test
    public void testCannotSelectRewardWithoutPlayer() {
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
    }

    @Test
    public void testSelectRewardDoesNothingForInvalidResource() {
        selectReward.setReward(1, mockCard, List.of(Resource.GREEN));
        selectReward.selectReward(Resource.YELLOW);
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
        assertTrue(mockCard.getReceivedResources().isEmpty());
    }

    @Test
    public void testMultipleRewardsOfSameType() {
        selectReward.setReward(1, mockCard, List.of(Resource.GREEN, Resource.GREEN, Resource.RED));
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
        selectReward.selectReward(Resource.GREEN);
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
        selectReward.selectReward(Resource.GREEN);
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
        assertTrue(selectReward.canSelectReward(Resource.RED));
        assertEquals(2, mockCard.getReceivedResources().size());
        assertEquals(List.of(Resource.GREEN, Resource.GREEN), mockCard.getReceivedResources());
    }

    @Test
    public void testSetRewardOverwritesPrevious() {
        selectReward.setReward(1, mockCard, List.of(Resource.GREEN));
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
        MockCard newMockCard = new MockCard();
        selectReward.setReward(2, newMockCard, List.of(Resource.RED, Resource.YELLOW));
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
        assertTrue(selectReward.canSelectReward(Resource.RED));
        assertTrue(selectReward.canSelectReward(Resource.YELLOW));
        assertEquals("Player: 2, Selection: [RED, YELLOW]", selectReward.state());
    }

    @Test
    public void testCardReceivesCorrectResourceOnEachSelection() {
        selectReward.setReward(1, mockCard, List.of(Resource.GREEN, Resource.RED, Resource.YELLOW));
        selectReward.selectReward(Resource.RED);
        selectReward.selectReward(Resource.YELLOW);
        selectReward.selectReward(Resource.GREEN);
        assertEquals(3, mockCard.getReceivedResources().size());
        assertEquals(Resource.RED, mockCard.getReceivedResources().get(0));
        assertEquals(Resource.YELLOW, mockCard.getReceivedResources().get(1));
        assertEquals(Resource.GREEN, mockCard.getReceivedResources().get(2));
    }

    private static class MockCard implements Card {
        private final List<Resource> receivedResources = new ArrayList<>();

        @Override
        public boolean canGetResources(List<Resource> resources) {
            return false;
        }

        @Override
        public void getResources(List<Resource> resources) {
        }

        @Override
        public boolean canPutResources(List<Resource> resources) {
            return true;
        }

        @Override
        public void putResources(List<Resource> resources) {
            receivedResources.addAll(resources);
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
            return "FakeCard";
        }

        public List<Resource> getReceivedResources() {
            return new ArrayList<>(receivedResources);
        }
    }
}
