package sk.uniba.fmph.dcs.terra_futura.score;

public interface ScoringMethod {

    /**
     * The player selects this scoring method and the scoring is calculated.
     */
    void selectThisMethodAndCalculate();

    /**
     * Returns the current state as a string.
     * Example: "Scoring method not yet selected."
     *          "Selected scoring method: total points = 25"
     * @return the current state of the scoring method
     */
    String state();
}

