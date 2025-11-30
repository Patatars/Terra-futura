package sk.uniba.fmph.dcs.terra_futura.actions;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;

import static org.junit.Assert.*;

public class SelectRewardTest {

    private SelectReward selectReward;

    @Before
    public void setUp() {
        selectReward = new SelectReward();
    }

    @Test
    public void testInitialState() {
        assertEquals("Player: None, Selection: []", selectReward.state());
    }

    @Test
    public void testSetReward() {
        boolean result = selectReward.setReward(1, null, List.of(Resource.GREEN, Resource.RED));
        assertTrue("setReward should return true", result);
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
        assertTrue(selectReward.canSelectReward(Resource.RED));
        assertFalse(selectReward.canSelectReward(Resource.YELLOW));
        assertEquals("Player: 1, Selection: [GREEN, RED]", selectReward.state());
    }

    @Test
    public void testSetRewardWithNullReward() {
        boolean result = selectReward.setReward(1, null, null);
        assertFalse("setReward should return false for null reward", result);
    }

    @Test
    public void testSelectReward() {
        selectReward.setReward(1, null, List.of(Resource.GREEN, Resource.RED));

        selectReward.selectReward(Resource.GREEN);
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
        assertTrue(selectReward.canSelectReward(Resource.RED));
        assertEquals("Player: 1, Selection: [RED]", selectReward.state());

        selectReward.selectReward(Resource.RED);
        assertFalse(selectReward.canSelectReward(Resource.RED));
        assertEquals("Player: 1, Selection: []", selectReward.state());
    }

    @Test
    public void testSelectInvalidReward() {
        selectReward.setReward(1, null, List.of(Resource.GREEN));

        selectReward.selectReward(Resource.RED);
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
        assertEquals("Player: 1, Selection: [GREEN]", selectReward.state());
    }
}
