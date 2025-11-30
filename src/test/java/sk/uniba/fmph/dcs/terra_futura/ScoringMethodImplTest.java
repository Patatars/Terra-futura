package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.score.Points;
import sk.uniba.fmph.dcs.terra_futura.score.ScoringMethodImpl;

import java.util.List;

import static org.junit.Assert.*;

public class ScoringMethodImplTest {

    /**
     * Situation 1: Player collects a simple set of basic resources.
     * GREEN, RED, YELLOW → suppose: 1 + 1 + 1 = 3 points
     * Bonus: +5
     * Expected total = 8
     */
    @Test
    public void testBasicResourcesScoring() {
        List<Resource> resources = List.of(
                Resource.GREEN,
                Resource.RED,
                Resource.YELLOW
        );
        ScoringMethodImpl method =
                new ScoringMethodImpl(resources, new Points(5));

        method.selectThisMethodAndCalculate();

        assertTrue(method.getCalculatedTotal().isPresent());
        assertEquals(8, method.getCalculatedTotal().get().value());
    }

    /**
     * Situation 2: Player has produced advanced resources (e.g., CAR, GEAR).
     * CAR + GEAR = 6 + 5 = 11 (example values)
     * Bonus: +3
     * Expected total = 14
     */
    @Test
    public void testAdvancedResourcesScoring() {
        List<Resource> resources = List.of(
                Resource.CAR,
                Resource.GEAR
        );
        ScoringMethodImpl method =
                new ScoringMethodImpl(resources, new Points(3));

        method.selectThisMethodAndCalculate();

        assertTrue(method.getCalculatedTotal().isPresent());
        assertEquals(14, method.getCalculatedTotal().get().value());
    }

    /**
     * Situation 3: Player polluted heavily.
     * POLLUTION has negative points (example: -1).
     * 3×POLLUTION = -3
     * Bonus = +2
     * Expected total = -3 + 2 = -1
     */
    @Test
    public void testPollutionReducesPoints() {
        List<Resource> resources = List.of(
                Resource.POLLUTION,
                Resource.POLLUTION,
                Resource.POLLUTION
        );
        ScoringMethodImpl method =
                new ScoringMethodImpl(resources, new Points(2));

        method.selectThisMethodAndCalculate();

        assertTrue(method.getCalculatedTotal().isPresent());
        assertEquals(-1, method.getCalculatedTotal().get().value());
    }

    /**
     * Situation 4: Player ends the game with no resources.
     * Sum of resources = 0
     * Bonus = +4
     * Expected = 4
     */
    @Test
    public void testEmptyResourcesStillGetsBonus() {
        List<Resource> resources = List.of();
        ScoringMethodImpl method =
                new ScoringMethodImpl(resources, new Points(4));

        method.selectThisMethodAndCalculate();

        assertTrue(method.getCalculatedTotal().isPresent());
        assertEquals(4, method.getCalculatedTotal().get().value());
    }

    /**
     * Situation 5: Calling state() before and after calculation.
     * Before → "not yet selected"
     * After  → "Selected scoring method: total points = X"
     */
    @Test
    public void testStateMessages() {
        ScoringMethodImpl method =
                new ScoringMethodImpl(List.of(Resource.GREEN), new Points(1));

        // Before calculation
        assertEquals("Scoring method not yet selected.", method.state());

        // After calculation
        method.selectThisMethodAndCalculate();
        assertEquals(
                "Selected scoring method: total points = " +
                        method.getCalculatedTotal().get().value(),
                method.state()
        );
    }

    /**
     * Situation 6: A realistic game situation:
     * Player activates cards and ends with:
     * GREEN, GREEN, CAR, POLLUTION
     * Example values: 1 + 1 + 6 - 1 = 7
     * Bonus: +5
     * Expected = 12
     */
    @Test
    public void testMixedRealGameScenario() {
        List<Resource> resources = List.of(
                Resource.GREEN,
                Resource.GREEN,
                Resource.CAR,
                Resource.POLLUTION
        );

        ScoringMethodImpl method =
                new ScoringMethodImpl(resources, new Points(5));

        method.selectThisMethodAndCalculate();

        assertEquals(12, method.getCalculatedTotal().get().value());
    }
}

