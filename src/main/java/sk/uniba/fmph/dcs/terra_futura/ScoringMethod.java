package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.score.Points;

import java.util.List;
import java.util.Optional;

public class ScoringMethod implements ScoringMethodInterface {

    // The resources to be scored. Each resource contributes its quantity
    private final List<Resource> resources;

    // Points awarded per combination/unit
    private final Points pointsPerCombination;

    // Cached calculation result; null until calculation runs
    private Points calculatedTotal;

    public ScoringMethod(final List<Resource> resources, final Points pointsPerCombination) {
        this.resources = resources;
        this.pointsPerCombination = pointsPerCombination;
    }

    /**
     * Calculate and store the total score for the current resources.
     * Algorithm: sum all resource point values from the list. Each resource
     * in the list has its own point value (e.g., GREEN=1, CAR=6, POLLUTION=-1).
     * The pointsPerCombination is added as bonus points for having the complete set.
     * Edge cases handled:
     * - If {@code resources} is empty, the sum is 0 plus the bonus points.
     * - The method does not modify the original resources list.
     */
    @Override
    public void selectThisMethodAndCalculate() {
        // Sum point values of all resources in the list
        int totalFromResources = resources.stream()
                .mapToInt(resource -> resource.getPoints().value())
                .sum();

        // Add the bonus points for having this combination and store the result
        calculatedTotal = new Points(totalFromResources + pointsPerCombination.value());
    }

    @Override
    public String state() {
        if (calculatedTotal != null) {
            return "Selected scoring method: total points = " + calculatedTotal.value();
        }
        return "Scoring method not yet selected.";
    }

    // Expose the calculated total (if present) to callers.
    public Optional<Points> getCalculatedTotal() {
        return Optional.ofNullable(calculatedTotal);
    }
}

