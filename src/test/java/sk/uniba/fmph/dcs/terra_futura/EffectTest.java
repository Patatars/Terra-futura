package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class EffectTest {

    //TransformationFixed


    @Test
    public void transformationFixed_validInput_shouldReturnTrue() {
        // Given
        var from = List.of(Resource.GREEN, Resource.RED);
        var to = List.of(Resource.BULB, Resource.CAR);
        var effect = new TransformationFixed(from, to, 1);

        var input = List.of(Resource.GREEN, Resource.RED);
        var output = List.of(Resource.BULB, Resource.CAR);
        int pollution = 1;

        // When & Then
        assertTrue(effect.check(input, output, pollution));
        assertFalse(effect.hasAssistance());
    }

    @Test
    public void transformationFixed_wrongInput_shouldReturnFalse() {
        var effect = new TransformationFixed(
                List.of(Resource.GREEN),
                List.of(Resource.BULB),
                0
        );

        assertFalse(effect.check(
                List.of(Resource.RED), // неправильний вхід
                List.of(Resource.BULB),
                0
        ));
    }

    @Test
    public void transformationFixed_insufficientPollution_shouldReturnFalse() {
        var effect = new TransformationFixed(
                List.of(),
                List.of(Resource.MONEY),
                2
        );

        assertFalse(effect.check(
                List.of(),
                List.of(Resource.MONEY),
                1 // недостатньо забруднення
        ));
    }

    //Тест для ArbitraryBasic

    @Test
    public void arbitraryBasic_validOutput_shouldReturnTrue() {
        var effect = new ArbitraryBasic(List.of(Resource.GREEN, Resource.GREEN));

        assertTrue(effect.check(
                List.of(),                     // немає вхідних ресурсів
                List.of(Resource.GREEN, Resource.GREEN),
                0                              // не використовує забруднення
        ));
        assertFalse(effect.hasAssistance());
    }

    @Test
    public void arbitraryBasic_nonEmptyInput_shouldReturnFalse() {
        var effect = new ArbitraryBasic(List.of(Resource.BULB));

        assertFalse(effect.check(
                List.of(Resource.GREEN), // не повинно бути входу
                List.of(Resource.BULB),
                0
        ));
    }

    @Test
    public void arbitraryBasic_wrongOutput_shouldReturnFalse() {
        var effect = new ArbitraryBasic(List.of(Resource.CAR));

        assertFalse(effect.check(
                List.of(),
                List.of(Resource.GEAR), // інший вихід
                0
        ));
    }

    //EffectOr

    @Test
    public void effectOr_oneValidEffect_shouldReturnTrue() {
        var effect1 = new TransformationFixed(List.of(Resource.RED), List.of(Resource.BULB), 1);
        var effect2 = new ArbitraryBasic(List.of(Resource.MONEY));
        var orEffect = new EffectOr(List.of(effect1, effect2));

        // Спробуємо активувати другий ефект (ArbitraryBasic)
        assertTrue(orEffect.check(
                List.of(),
                List.of(Resource.MONEY),
                0
        ));
    }

    @Test
    public void effectOr_allInvalid_shouldReturnFalse() {
        var effect1 = new TransformationFixed(List.of(Resource.GREEN), List.of(Resource.BULB), 1);
        var effect2 = new ArbitraryBasic(List.of(Resource.CAR));
        var orEffect = new EffectOr(List.of(effect1, effect2));

        // Ні один ефект не підходить
        assertFalse(orEffect.check(
                List.of(Resource.YELLOW), // ні до чого не підходить
                List.of(Resource.GEAR),
                0
        ));
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

        var normalEffect = new ArbitraryBasic(List.of(Resource.GREEN));
        var orEffect = new EffectOr(List.of(normalEffect, effectWithAssistance));

        assertTrue(orEffect.hasAssistance());
    }

    // Додатково: тест стану (state)

    @Test
    public void transformationFixed_state_returnsCorrectString() {
        var effect = new TransformationFixed(
                List.of(Resource.GREEN),
                List.of(Resource.BULB),
                1
        );
        String state = effect.state();
        assertTrue(state.contains("TransformationFixed"));
        assertTrue(state.contains("GREEN"));
        assertTrue(state.contains("BULB"));
        assertTrue(state.contains("pollution=1"));
    }
}
