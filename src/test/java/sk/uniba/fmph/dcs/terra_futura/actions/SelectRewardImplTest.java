package sk.uniba.fmph.dcs.terra_futura.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SelectRewardImplTest {

    private SelectReward selectReward;

    @Before
    public void setUp() {
        selectReward = new SelectRewardImpl();
    }

    @Test
    public void testSetReward_ValidInput() {
        int playerId = 1;
        List<Resource> rewards = Arrays.asList(Resource.GREEN, Resource.RED);

        boolean result = selectReward.setReward(playerId, null, rewards);

        Assert.assertTrue("Should return true for valid input", result);
        Assert.assertTrue("Should be able to select GREEN", selectReward.canSelectReward(Resource.GREEN));
        Assert.assertTrue("Should be able to select RED", selectReward.canSelectReward(Resource.RED));
        Assert.assertTrue(selectReward.state().contains("player=1"));
    }

    @Test
    public void testSetReward_InvalidInput() {

        boolean resultEmpty = selectReward.setReward(1, null, new ArrayList<>());
        Assert.assertFalse(resultEmpty);
        Assert.assertTrue(selectReward.state().contains("player=None"));

        boolean resultNull = selectReward.setReward(1, null, null);
        Assert.assertFalse(resultNull);
        Assert.assertTrue(selectReward.state().contains("player=None"));
    }

    @Test
    public void testCanSelectReward() {

        selectReward.setReward(1, null, Collections.singletonList(Resource.MONEY));

        Assert.assertTrue(selectReward.canSelectReward(Resource.MONEY));

        Assert.assertFalse(selectReward.canSelectReward(Resource.GREEN));
    }

    @Test
    public void testSelectReward_RemovesResource() {
        List<Resource> rewards = new ArrayList<>();
        rewards.add(Resource.GREEN);
        rewards.add(Resource.GREEN);
        rewards.add(Resource.RED);

        selectReward.setReward(2, null, rewards);

        selectReward.selectReward(Resource.GREEN);

        String state = selectReward.state();

        Assert.assertTrue(selectReward.canSelectReward(Resource.GREEN));

        Assert.assertTrue(selectReward.canSelectReward(Resource.RED));

        selectReward.selectReward(Resource.GREEN);

        selectReward.selectReward(Resource.RED);

        Assert.assertFalse(selectReward.canSelectReward(Resource.RED));
    }

    @Test
    public void testSelectReward_InvalidSelection() {
        selectReward.setReward(1, null, Collections.singletonList(Resource.GREEN));

        selectReward.selectReward(Resource.RED);

        Assert.assertTrue(selectReward.canSelectReward(Resource.GREEN));
    }

    @Test
    public void testClear() {
        selectReward.setReward(1, null, Collections.singletonList(Resource.GREEN));

        selectReward.clear();

        Assert.assertFalse(selectReward.canSelectReward(Resource.GREEN));
        Assert.assertTrue(selectReward.state().contains("player=None"));
    }

    @Test
    public void testState_Empty() {
        Assert.assertEquals("SelectReward{player=None, rewards=[]}", selectReward.state());
    }
}
