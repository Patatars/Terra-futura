package sk.uniba.fmph.dcs.terra_futura.enums;

/**
 * Game states for state machine.
 */
public enum GameState {
    TAKE_CARD_NO_CARD_DISCARDED,
    TAKE_CARD_CARD_DISCARDED,
    ACTIVATE_CARD,
    SELECT_REWARD,
    SELECT_ACTIVATION_PATTERN,
    SELECT_SCORING_METHOD,
    FINISH
}
