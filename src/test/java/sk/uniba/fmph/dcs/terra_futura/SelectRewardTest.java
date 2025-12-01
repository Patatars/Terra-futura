package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.actions.SelectReward;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.effect.ArbitraryBasic;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SelectRewardTest {

    private SelectReward selectReward;
    private Card dummyCard;
    private List<Resource> rewards;

    @Before
    public void setUp() {
        selectReward = new SelectReward();
        // Create a dummy card (not used in logic, but required by interface)
        dummyCard = new CardImpl(
                Collections.emptyList(),
                1,
                new ArbitraryBasic(Collections.singletonList(Resource.GREEN)),
                null
        );
        rewards = Arrays.asList(Resource.GREEN, Resource.RED, Resource.MONEY);
    }

    // Verifies initial state is empty
    @Test
    public void initialState_hasNoActivePlayerAndEmptySelection() {
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
        assertEquals("Player: None, Selection: []", selectReward.state());
    }

    // Checks that reward setup works with valid input
    @Test
    public void setReward_withValidData_activatesPlayerAndStoresRewards() {
        assertTrue(selectReward.setReward(42, dummyCard, rewards));
        assertTrue(selectReward.canSelectReward(Resource.GREEN));
        assertTrue(selectReward.canSelectReward(Resource.RED));
        assertTrue(selectReward.canSelectReward(Resource.MONEY));
        assertFalse(selectReward.canSelectReward(Resource.YELLOW));
        assertTrue(selectReward.state().contains("Player: 42"));
    }

    // Ensures null or empty reward list is rejected
    @Test
    public void setReward_withNullOrEmptyReward_returnsFalseAndClearsState() {
        assertFalse(selectReward.setReward(1, dummyCard, null));
        assertFalse(selectReward.setReward(2, dummyCard, Collections.emptyList()));
        assertEquals("Player: None, Selection: []", selectReward.state());
    }

    // Confirms that selecting a reward removes it from available options
    @Test
    public void selectReward_removesResourceFromAvailableList() {
        selectReward.setReward(1, dummyCard, rewards);
        selectReward.selectReward(Resource.GREEN);
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
        assertTrue(selectReward.canSelectReward(Resource.RED));
    }

    // Tests that selecting an invalid resource has no effect
    @Test
    public void selectReward_withInvalidResource_doesNothing() {
        selectReward.setReward(1, dummyCard, rewards);
        selectReward.selectReward(Resource.YELLOW);
        assertTrue(selectReward.canSelectReward(Resource.GREEN)); // still available
    }

    // Validates that clear() resets the entire state
    @Test
    public void clear_resetsPlayerAndRewards() {
        selectReward.setReward(1, dummyCard, rewards);
        selectReward.clear();
        assertEquals("Player: None, Selection: []", selectReward.state());
        assertFalse(selectReward.canSelectReward(Resource.GREEN));
    }
}
