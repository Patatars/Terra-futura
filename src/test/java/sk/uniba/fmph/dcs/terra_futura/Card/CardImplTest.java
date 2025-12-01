package sk.uniba.fmph.dcs.terra_futura.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.effect.Effect;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardImplTest {

    private CardImpl card;
    private FakeEffect upperEffectStub;
    private FakeEffect lowerEffectStub;
    private ArrayList<Resource> resources;
    private final int POLLUTION_MAX = 3;

    private static class FakeEffect implements Effect {
        boolean checkResult = false;
        boolean hasAssistanceResult = false;
        int lastPollutionPassed = -1;

        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            this.lastPollutionPassed = pollution;
            return checkResult;
        }

        @Override
        public boolean hasAssistance() {
            return hasAssistanceResult;
        }

        @Override
        public String state() {
            return "FakeState";
        }
    }

    @Before
    public void setUp() {
        upperEffectStub = new FakeEffect();
        lowerEffectStub = new FakeEffect();
        resources = new ArrayList<>();
        card = new CardImpl(new ArrayList<>(), POLLUTION_MAX, upperEffectStub, lowerEffectStub);
    }


    @Test
    public void testCanPutResources_Possible() {

        Assert.assertTrue(card.canPutResources(Collections.singletonList(Resource.GREEN)));
    }

    @Test
    public void testCanPutResources_CardFullOfPollution() {

        card.putResources(Arrays.asList(Resource.POLLUTION, Resource.POLLUTION, Resource.POLLUTION));

        Assert.assertFalse(card.canPutResources(Collections.singletonList(Resource.GREEN)));
    }

    @Test
    public void testPutResources_Success() {
        List<Resource> toAdd = Arrays.asList(Resource.GREEN, Resource.RED);
        card.putResources(toAdd);

        Assert.assertTrue(card.canGetResources(toAdd));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutResources_ThrowsExceptionWhenFull() {

        card.putResources(Arrays.asList(Resource.POLLUTION, Resource.POLLUTION, Resource.POLLUTION));

        List<Resource> toAdd = Collections.singletonList(Resource.GREEN);
        card.putResources(toAdd);
    }


    @Test
    public void testCanGetResources_HappyPath() {

        card.putResources(Arrays.asList(Resource.GREEN, Resource.RED));

        Assert.assertTrue(card.canGetResources(Collections.singletonList(Resource.GREEN)));
        Assert.assertTrue(card.canGetResources(Arrays.asList(Resource.GREEN, Resource.RED)));
    }

    @Test
    public void testCanGetResources_NotEnoughResources() {
        resources.add(Resource.GREEN);

        Assert.assertFalse(card.canGetResources(Collections.singletonList(Resource.RED)));
    }

    @Test
    public void testCanGetResources_PollutionBlock() {
        resources.add(Resource.GREEN);
        resources.add(Resource.POLLUTION);
        resources.add(Resource.POLLUTION);
        resources.add(Resource.POLLUTION);

        Assert.assertFalse(card.canGetResources(Collections.singletonList(Resource.GREEN)));
    }

    @Test
    public void testCanGetResources_CleaningPollution_AllowedEvenIfFull() {
        card.putResources(Arrays.asList(Resource.POLLUTION, Resource.POLLUTION, Resource.POLLUTION));

        Assert.assertTrue(card.canGetResources(Collections.singletonList(Resource.POLLUTION)));
    }

    @Test
    public void testGetResources_Success() {

        card.putResources(Arrays.asList(Resource.GREEN, Resource.RED, Resource.GREEN));

        List<Resource> toGet = Arrays.asList(Resource.GREEN, Resource.RED);

        card.getResources(toGet);

        Assert.assertTrue("Should contain one remaining GREEN",
                card.canGetResources(Collections.singletonList(Resource.GREEN)));

        Assert.assertFalse("Should NOT contain RED anymore",
                card.canGetResources(Collections.singletonList(Resource.RED)));

        Assert.assertFalse("Should NOT contain two GREENs",
                card.canGetResources(Arrays.asList(Resource.GREEN, Resource.GREEN)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResources_ThrowsIfNotPossible() {
        resources.add(Resource.GREEN);
        List<Resource> toGet = Collections.singletonList(Resource.RED);
        card.getResources(toGet);
    }

    @Test
    public void testCheck_UpperEffectDelegation() {

        card.putResources(Collections.singletonList(Resource.POLLUTION));

        upperEffectStub.checkResult = true;

        boolean result = card.check(Collections.emptyList(), Collections.emptyList(), 0);

        Assert.assertTrue(result);

        Assert.assertEquals("Card passed wrong pollution count to effect", 2, upperEffectStub.lastPollutionPassed);
    }

    @Test
    public void testCheck_UpperEffectNull() {
        CardImpl nullEffectCard = new CardImpl(new ArrayList<>(), 3, null, null);
        Assert.assertFalse(nullEffectCard.check(Collections.emptyList(), Collections.emptyList(), 0));
    }



    @Test
    public void testCheckLower_Delegation() {

        lowerEffectStub.checkResult = true;

        boolean result = card.checkLower(Collections.emptyList(), Collections.emptyList(), 0);

        Assert.assertTrue(result);
        Assert.assertEquals(3, lowerEffectStub.lastPollutionPassed);
    }


    @Test
    public void testHasAssistance() {
        // По умолчанию false
        Assert.assertFalse(card.hasAssistance());

    }

    @Test
    public void testState() {

        card.putResources(Collections.singletonList(Resource.MONEY));

        String state = card.state();

        Assert.assertTrue(state.contains("FakeState"));

        Assert.assertTrue(state.contains("MONEY"));
    }
}