package sk.uniba.fmph.dcs.terra_futura.actions;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive test suite for SelectRewardImpl
 * Tests cover basic functionality, edge cases, and boundary conditions
 */
public class SelectRewardImplTest {

    private SelectRewardImpl selectReward;
    private FakeCard card;

    @Before
    public void setup() {
        selectReward = new SelectRewardImpl();
        card = new FakeCard();
    }

    // Basic functionality

    @Test
    public void testInitialState() {
        String state = selectReward.state();
        assertEquals("Initial state should indicate undefined player",
                "Player: Undefined", state);
    }

    @Test
    public void testCannotSelectRewardInitially() {
        assertFalse("Should not be able to select reward initially",
                selectReward.canSelectReward(Resource.YELLOW));
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
        assertFalse(selectReward.canSelectReward(Resource.MONEY));
    }

    @Test
    public void testSetRewardWithSingleResource() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW);

        selectReward.setReward(1, card, rewards);

        assertTrue("Should be able to select YELLOW",
                selectReward.canSelectReward(Resource.YELLOW));
        assertFalse("Should not be able to select GREEN",
                selectReward.canSelectReward(Resource.GREEN));
    }

    @Test
    public void testSetRewardWithMultipleResources() {
        List<Resource> rewards = Arrays.asList(
                Resource.YELLOW, Resource.GREEN, Resource.RED
        );

        selectReward.setReward(2, card, rewards);

        assertTrue("Should be able to select YELLOW",
                selectReward.canSelectReward(Resource.YELLOW));
        assertTrue("Should be able to select GREEN",
                selectReward.canSelectReward(Resource.GREEN));
        assertTrue("Should be able to select RED",
                selectReward.canSelectReward(Resource.RED));
        assertFalse("Should not be able to select MONEY",
                selectReward.canSelectReward(Resource.MONEY));
    }

    @Test
    public void testSelectRewardRemovesResource() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW, Resource.GREEN);
        selectReward.setReward(1, card, rewards);

        assertTrue("YELLOW should be selectable",
                selectReward.canSelectReward(Resource.YELLOW));

        selectReward.selectReward(Resource.YELLOW);

        assertFalse("YELLOW should no longer be selectable",
                selectReward.canSelectReward(Resource.YELLOW));
        assertTrue("GREEN should still be selectable",
                selectReward.canSelectReward(Resource.GREEN));
    }

    @Test
    public void testSelectAllRewards() {
        List<Resource> rewards = Arrays.asList(
                Resource.YELLOW, Resource.GREEN, Resource.RED
        );
        selectReward.setReward(1, card, rewards);

        selectReward.selectReward(Resource.YELLOW);
        selectReward.selectReward(Resource.GREEN);
        selectReward.selectReward(Resource.RED);

        assertFalse("No resources should be selectable",
                selectReward.canSelectReward(Resource.YELLOW));
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
        assertFalse(selectReward.canSelectReward(Resource.RED));
    }

    @Test
    public void testStateShowsPlayerAndSelection() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW, Resource.GREEN);
        selectReward.setReward(5, card, rewards);

        String state = selectReward.state();

        assertTrue("State should contain player ID", state.contains("5"));
        assertTrue("State should contain selection",
                state.contains("Selection") || state.contains("YELLOW"));
    }

    // ==================== EDGE CASES ====================

    @Test
    public void testSetRewardWithEmptyList() {
        selectReward.setReward(1, card, Collections.emptyList());

        assertEquals("State should remain undefined for empty reward list",
                "Player: Undefined", selectReward.state());
        assertFalse(selectReward.canSelectReward(Resource.YELLOW));
    }

    @Test
    public void testSetRewardWithNullCard() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW);

        // Should not throw exception
        selectReward.setReward(1, null, rewards);

        assertTrue("Should work with null card",
                selectReward.canSelectReward(Resource.YELLOW));
    }

    @Test
    public void testSelectNonExistentResource() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW);
        selectReward.setReward(1, card, rewards);

        selectReward.selectReward(Resource.GREEN); // не в списке

        // Состояние не должно измениться
        assertTrue("YELLOW should still be selectable",
                selectReward.canSelectReward(Resource.YELLOW));
    }

    @Test
    public void testSelectSameResourceTwice() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW);
        selectReward.setReward(1, card, rewards);

        selectReward.selectReward(Resource.YELLOW);
        selectReward.selectReward(Resource.YELLOW); // второй раз

        assertFalse("Resource should not be selectable after first selection",
                selectReward.canSelectReward(Resource.YELLOW));
    }

    @Test
    public void testSelectRewardBeforeSetReward() {
        selectReward.selectReward(Resource.YELLOW);

        // Не должно быть ошибки, просто ничего не происходит
        assertFalse(selectReward.canSelectReward(Resource.YELLOW));
    }

    // ==================== DUPLICATE RESOURCES ====================

    @Test
    public void testSetRewardWithDuplicateResources() {
        List<Resource> rewards = Arrays.asList(
                Resource.YELLOW, Resource.YELLOW, Resource.GREEN
        );
        selectReward.setReward(1, card, rewards);

        assertTrue("YELLOW should be selectable",
                selectReward.canSelectReward(Resource.YELLOW));

        selectReward.selectReward(Resource.YELLOW);

        // После первого удаления YELLOW, второй YELLOW должен остаться
        assertTrue("Second YELLOW should still be selectable",
                selectReward.canSelectReward(Resource.YELLOW));

        selectReward.selectReward(Resource.YELLOW);

        assertFalse("YELLOW should no longer be selectable",
                selectReward.canSelectReward(Resource.YELLOW));
    }

    @Test
    public void testSelectAllDuplicates() {
        List<Resource> rewards = Arrays.asList(
                Resource.MONEY, Resource.MONEY, Resource.MONEY
        );
        selectReward.setReward(1, card, rewards);

        selectReward.selectReward(Resource.MONEY);
        selectReward.selectReward(Resource.MONEY);
        selectReward.selectReward(Resource.MONEY);

        assertFalse("All MONEY should be gone",
                selectReward.canSelectReward(Resource.MONEY));
    }

    // ==================== MULTIPLE PLAYERS ====================

    @Test
    public void testSwitchingPlayers() {
        List<Resource> rewards1 = Arrays.asList(Resource.YELLOW);
        selectReward.setReward(1, card, rewards1);

        assertTrue("Player 1 should have YELLOW available",
                selectReward.canSelectReward(Resource.YELLOW));

        // Переключаемся на другого игрока
        List<Resource> rewards2 = Arrays.asList(Resource.GREEN);
        selectReward.setReward(2, card, rewards2);

        assertFalse("YELLOW should no longer be available",
                selectReward.canSelectReward(Resource.YELLOW));
        assertTrue("GREEN should now be available",
                selectReward.canSelectReward(Resource.GREEN));
    }

    @Test
    public void testPlayerZero() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW);
        selectReward.setReward(0, card, rewards);

        String state = selectReward.state();
        assertTrue("State should contain player 0", state.contains("0"));
    }

    @Test
    public void testNegativePlayerId() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW);
        selectReward.setReward(-1, card, rewards);

        // Система должна принять отрицательный ID
        assertTrue("Should accept negative player ID",
                selectReward.canSelectReward(Resource.YELLOW));
    }

    @Test
    public void testLargePlayerId() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW);
        selectReward.setReward(999999, card, rewards);

        String state = selectReward.state();
        assertTrue("State should contain large player ID", state.contains("999999"));
    }

    // All resources

    @Test
    public void testAllResourceTypes() {
        List<Resource> rewards = Arrays.asList(
                Resource.YELLOW,
                Resource.GREEN,
                Resource.RED,
                Resource.CAR,
                Resource.BULB,
                Resource.POLLUTION,
                Resource.MONEY
        );
        selectReward.setReward(1, card, rewards);

        for (Resource resource : rewards) {
            assertTrue("Resource " + resource + " should be selectable",
                    selectReward.canSelectReward(resource));
        }

        // Выбираем все
        for (Resource resource : rewards) {
            selectReward.selectReward(resource);
        }

        // Проверяем что все удалены
        for (Resource resource : rewards) {
            assertFalse("Resource " + resource + " should no longer be selectable",
                    selectReward.canSelectReward(resource));
        }
    }

    // States

    @Test
    public void testStateAfterSelection() {
        List<Resource> rewards = Arrays.asList(
                Resource.YELLOW, Resource.GREEN, Resource.RED
        );
        selectReward.setReward(1, card, rewards);

        String stateBefore = selectReward.state();

        selectReward.selectReward(Resource.YELLOW);

        String stateAfter = selectReward.state();

        assertNotEquals("State should change after selection",
                stateBefore, stateAfter);
    }

    @Test
    public void testStateWithEmptySelection() {
        List<Resource> rewards = Arrays.asList(Resource.YELLOW);
        selectReward.setReward(1, card, rewards);

        selectReward.selectReward(Resource.YELLOW);

        String state = selectReward.state();
        assertTrue("State should still show player after all selections",
                state.contains("Player: 1"));
    }

    // OverWriting Rewards

    @Test
    public void testOverwritePreviousReward() {
        List<Resource> rewards1 = Arrays.asList(Resource.YELLOW, Resource.GREEN);
        selectReward.setReward(1, card, rewards1);

        selectReward.selectReward(Resource.YELLOW);

        // Устанавливаем новую награду
        List<Resource> rewards2 = Arrays.asList(Resource.RED, Resource.MONEY);
        selectReward.setReward(1, card, rewards2);

        assertFalse("YELLOW should no longer be available",
                selectReward.canSelectReward(Resource.YELLOW));
        assertFalse("GREEN should no longer be available",
                selectReward.canSelectReward(Resource.GREEN));
        assertTrue("RED should be available",
                selectReward.canSelectReward(Resource.RED));
        assertTrue("MONEY should be available",
                selectReward.canSelectReward(Resource.MONEY));
    }

    //Modify

    @Test
    public void testModifyOriginalListDoesNotAffectReward() {
        List<Resource> rewards = new ArrayList<>(Arrays.asList(Resource.YELLOW));
        selectReward.setReward(1, card, rewards);

        // Модифицируем исходный список
        rewards.add(Resource.GREEN);
        rewards.clear();

        // Внутреннее состояние не должно измениться
        assertTrue("YELLOW should still be selectable",
                selectReward.canSelectReward(Resource.YELLOW));
        assertFalse("GREEN should not be selectable",
                selectReward.canSelectReward(Resource.GREEN));
    }


    @Test
    public void testMaximumResourceVariety() {
        // Все возможные типы ресурсов
        List<Resource> rewards = new ArrayList<>();
        for (Resource resource : Resource.values()) {
            rewards.add(resource);
        }

        selectReward.setReward(1, card, rewards);

        int initialCount = rewards.size();

        for (Resource resource : Resource.values()) {
            assertTrue("All resources should be initially selectable",
                    selectReward.canSelectReward(resource));
        }
    }

    // sequence tests

    @Test
    public void testSelectInDifferentOrder() {
        List<Resource> rewards = Arrays.asList(
                Resource.YELLOW, Resource.GREEN, Resource.RED
        );
        selectReward.setReward(1, card, rewards);

        // Выбираем в обратном порядке
        selectReward.selectReward(Resource.RED);
        selectReward.selectReward(Resource.GREEN);
        selectReward.selectReward(Resource.YELLOW);

        assertFalse(selectReward.canSelectReward(Resource.YELLOW));
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
        assertFalse(selectReward.canSelectReward(Resource.RED));
    }

    @Test
    public void testPartialSelection() {
        List<Resource> rewards = Arrays.asList(
                Resource.YELLOW, Resource.GREEN, Resource.RED, Resource.MONEY
        );
        selectReward.setReward(1, card, rewards);

        selectReward.selectReward(Resource.GREEN);
        selectReward.selectReward(Resource.MONEY);

        assertTrue("YELLOW should still be available",
                selectReward.canSelectReward(Resource.YELLOW));
        assertFalse("GREEN should be gone",
                selectReward.canSelectReward(Resource.GREEN));
        assertTrue("RED should still be available",
                selectReward.canSelectReward(Resource.RED));
        assertFalse("MONEY should be gone",
                selectReward.canSelectReward(Resource.MONEY));
    }

    // support classes

    /**
     * Fake Card implementation for testing purposes
     */
    private static class FakeCard implements Card {
        @Override
        public boolean canGetResources(List<Resource> resources) {
            return true;
        }

        @Override
        public boolean getResources(List<Resource> resources) {
            return true;
        }

        @Override
        public boolean canPutResources(List<Resource> resources) {
            return true;
        }

        @Override
        public void putResources(List<Resource> resources) {
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
            return "FakeCard";
        }
    }
}
