package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.actions.SelectRewardImpl;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SelectRewardTest {

    private static class TestCard implements Card {
        @Override
        public boolean canGetResources(List<Resource> resources) { return true; }
        @Override
        public boolean getResources(List<Resource> resources) { return true; }
        @Override
        public boolean canPutResources(List<Resource> resources) { return true; }
        @Override
        public void putResources(List<Resource> resources) {}
        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) { return true; }
        @Override
        public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) { return true; }
        @Override
        public boolean hasAssistance() { return false; }
        @Override
        public String state() { return "TestCard"; }
    }

    private SelectRewardImpl selectReward;
    private TestCard card;

    @Before
    public void setup() {
        selectReward = new SelectRewardImpl();
        card = new TestCard();
    }

    @Test
    public void testRewardListCopiedNotReferenced() {
        List<Resource> rewards = new ArrayList<>(Arrays.asList(Resource.RED, Resource.GREEN));
        selectReward.setReward(1, card, rewards);

        // Modifying original list should not affect internal selection
        rewards.clear();

        assertTrue(selectReward.canSelectReward(Resource.RED));
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
    }

    @Test
    public void testSelectingSameResourceMultipleTimesWithDuplicates() {
        List<Resource> rewards = Arrays.asList(Resource.MONEY, Resource.MONEY);
        selectReward.setReward(1, card, rewards);

        selectReward.selectReward(Resource.MONEY);

        // One instance should still remain
        assertTrue(selectReward.canSelectReward(Resource.MONEY));

        selectReward.selectReward(Resource.MONEY);
        assertFalse(selectReward.canSelectReward(Resource.MONEY));
    }

    @Test
    public void testRewardPhaseWithoutPlayer() {
        // Directly check state when no player has been set
        assertEquals("Player: Undefined", selectReward.state());
        assertFalse(selectReward.canSelectReward(Resource.RED));
    }

    @Test
    public void testSelectRewardThatWasNeverAvailable() {
        selectReward.setReward(1, card, Arrays.asList(Resource.YELLOW));
        selectReward.selectReward(Resource.CAR); // Not in the list

        assertTrue(selectReward.canSelectReward(Resource.YELLOW));
        assertFalse(selectReward.canSelectReward(Resource.CAR));
    }

    @Test
    public void testSwitchRewardPhaseBetweenPlayers() {
        List<Resource> rewards1 = Arrays.asList(Resource.YELLOW, Resource.RED);
        selectReward.setReward(1, card, rewards1);
        selectReward.selectReward(Resource.YELLOW);

        List<Resource> rewards2 = Arrays.asList(Resource.GREEN);
        selectReward.setReward(2, card, rewards2);

        // Previous selections of player 1 should not affect player 2
        assertFalse(selectReward.canSelectReward(Resource.YELLOW));
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
    }

    @Test
    public void testEmptySelectionAfterAllChosen() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW);
        selectReward.setReward(1, card, rewards);
        selectReward.selectReward(Resource.YELLOW);

        // No resources left
        assertFalse(selectReward.canSelectReward(Resource.YELLOW));
        assertEquals("Player: 1, Selection: []", selectReward.state());
    }

    @Test
    public void testStateContainsCorrectPlayerAfterMultipleSelections() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW, Resource.RED);
        selectReward.setReward(5, card, rewards);

        selectReward.selectReward(Resource.RED);

        String state = selectReward.state();
        assertTrue(state.contains("5"));
        assertTrue(state.contains("YELLOW"));
        assertFalse(state.contains("RED"));
    }


    @Test
    public void testSequentialSelectionsDoNotBreakState() {
        List<Resource> rewards = Arrays.asList(Resource.RED, Resource.GREEN, Resource.YELLOW);
        selectReward.setReward(1, card, rewards);

        selectReward.selectReward(Resource.RED);
        selectReward.selectReward(Resource.GREEN);
        selectReward.selectReward(Resource.YELLOW);

        // All resources should be gone
        for (Resource r : rewards) {
            assertFalse(selectReward.canSelectReward(r));
        }

        String state = selectReward.state();
        assertTrue(state.contains("1"));
        assertTrue(state.contains("Selection: []"));
    }

    @Test
    public void testSelectingAfterRewardPhaseReset() {
        List<Resource> rewards1 = Arrays.asList(Resource.YELLOW);
        selectReward.setReward(1, card, rewards1);
        selectReward.selectReward(Resource.YELLOW);

        List<Resource> rewards2 = Arrays.asList(Resource.RED);
        selectReward.setReward(1, card, rewards2);

        assertFalse(selectReward.canSelectReward(Resource.YELLOW));
        assertTrue(selectReward.canSelectReward(Resource.RED));
    }

}