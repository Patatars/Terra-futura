package sk.uniba.fmph.dcs.terra_futura;

public interface ScoringMethodInterface {

    /**
     * The player selects this scoring method and the scoring is calculated.
     */
    void selectThisMethodAndCalculate();

    /**
     * Returns the current state as a string.
     * Example: "Scoring method not yet selected."
     *          "Selected scoring method: total points = 25"
     */
    String state();
}

