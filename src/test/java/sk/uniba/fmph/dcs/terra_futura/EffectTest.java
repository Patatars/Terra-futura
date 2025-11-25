package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.effect.ArbitraryBasic;
import sk.uniba.fmph.dcs.terra_futura.effect.Effect;
import sk.uniba.fmph.dcs.terra_futura.effect.EffectOr;
import sk.uniba.fmph.dcs.terra_futura.effect.TransformationFixed;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

public class EffectTest {

    //TransformationFixed
    @Test
    public void transformationFixed_validInput_shouldReturnTrue() {
        // Given
        List<Resource> from = Arrays.asList(Resource.GREEN, Resource.RED);
        List<Resource> to = Arrays.asList(Resource.BULB, Resource.CAR);
        Effect effect = new TransformationFixed(from, to, 1);

        List<Resource> input = Arrays.asList(Resource.GREEN, Resource.RED);
        List<Resource> output = Arrays.asList(Resource.BULB, Resource.CAR);
        int pollution = 1;

        // When & Then
        assertTrue(effect.check(input, output, pollution));
        assertFalse(effect.hasAssistance());
    }

    @Test
    public void transformationFixed_wrongInput_shouldReturnFalse() {
        List<Resource> from = Arrays.asList(Resource.GREEN);
        List<Resource> to = Arrays.asList(Resource.BULB);
        Effect effect = new TransformationFixed(from, to, 0);

        List<Resource> input = Arrays.asList(Resource.RED);
        List<Resource> output = Arrays.asList(Resource.BULB);
        int pollution = 0;

        assertFalse(effect.check(input, output, pollution));
    }

    @Test
    public void transformationFixed_insufficientPollution_shouldReturnFalse() {
        List<Resource> from = new ArrayList<>();
        List<Resource> to = Arrays.asList(Resource.MONEY);
        Effect effect = new TransformationFixed(from, to, 2);

        List<Resource> input = new ArrayList<>();
        List<Resource> output = Arrays.asList(Resource.MONEY);
        int pollution = 1;

        assertFalse(effect.check(input, output, pollution));
    }

    //ArbitraryBasic

    @Test
    public void arbitraryBasic_validOutput_shouldReturnTrue() {
        List<Resource> to = Arrays.asList(Resource.GREEN, Resource.GREEN);
        Effect effect = new ArbitraryBasic(to);

        List<Resource> input = new ArrayList<>();
        List<Resource> output = Arrays.asList(Resource.GREEN, Resource.GREEN);
        int pollution = 0;

        assertTrue(effect.check(input, output, pollution));
        assertFalse(effect.hasAssistance());
    }

    @Test
    public void arbitraryBasic_nonEmptyInput_shouldReturnFalse() {
        List<Resource> to = Arrays.asList(Resource.BULB);
        Effect effect = new ArbitraryBasic(to);

        List<Resource> input = Arrays.asList(Resource.GREEN);
        List<Resource> output = Arrays.asList(Resource.BULB);
        int pollution = 0;

        assertFalse(effect.check(input, output, pollution));
    }

    @Test
    public void arbitraryBasic_wrongOutput_shouldReturnFalse() {
        List<Resource> to = Arrays.asList(Resource.CAR);
        Effect effect = new ArbitraryBasic(to);

        List<Resource> input = new ArrayList<>();
        List<Resource> output = Arrays.asList(Resource.GEAR);
        int pollution = 0;

        assertFalse(effect.check(input, output, pollution));
    }

    //EffectOr

    @Test
    public void effectOr_oneValidEffect_shouldReturnTrue() {
        List<Resource> from1 = Arrays.asList(Resource.RED);
        List<Resource> to1 = Arrays.asList(Resource.BULB);
        Effect effect1 = new TransformationFixed(from1, to1, 1);
        Effect effect2 = new ArbitraryBasic(Arrays.asList(Resource.MONEY));
        Effect orEffect = new EffectOr(Arrays.asList(effect1, effect2));

        List<Resource> input = new ArrayList<>();
        List<Resource> output = Arrays.asList(Resource.MONEY);
        int pollution = 0;

        assertTrue(orEffect.check(input, output, pollution));
    }

    @Test
    public void effectOr_allInvalid_shouldReturnFalse() {
        List<Resource> from1 = Arrays.asList(Resource.GREEN);
        List<Resource> to1 = Arrays.asList(Resource.BULB);
        Effect effect1 = new TransformationFixed(from1, to1, 1);
        Effect effect2 = new ArbitraryBasic(Arrays.asList(Resource.CAR));
        Effect orEffect = new EffectOr(Arrays.asList(effect1, effect2));

        List<Resource> input = Arrays.asList(Resource.YELLOW);
        List<Resource> output = Arrays.asList(Resource.GEAR);
        int pollution = 0;

        assertFalse(orEffect.check(input, output, pollution));
    }

    @Test
    public void effectOr_hasAssistance_ifAnySubEffectHasIt() {
        // Створимо фейковий ефект з Assistance (для тесту)
        Effect effectWithAssistance = new Effect() {
            @Override
            public boolean check(List<Resource> input, List<Resource> output, int pollution) {
                return true;
            }
            @Override
            public boolean hasAssistance() {
                return true;
            }
            @Override
            public String state() {
                return "MockAssistanceEffect";
            }
        };

        Effect normalEffect = new ArbitraryBasic(Arrays.asList(Resource.GREEN));
        Effect orEffect = new EffectOr(Arrays.asList(normalEffect, effectWithAssistance));

        assertTrue(orEffect.hasAssistance());
    }

    // state

    @Test
    public void transformationFixed_state_returnsCorrectString() {
        List<Resource> from = Arrays.asList(Resource.GREEN);
        List<Resource> to = Arrays.asList(Resource.BULB);
        Effect effect = new TransformationFixed(from, to, 1);
        String state = effect.state();
        assertTrue(state.contains("TransformationFixed"));
        assertTrue(state.contains("GREEN"));
        assertTrue(state.contains("BULB"));
        assertTrue(state.contains("pollution=1"));
    }
}