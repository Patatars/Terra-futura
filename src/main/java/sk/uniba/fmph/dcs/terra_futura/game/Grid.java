package sk.uniba.fmph.dcs.terra_futura.game;

import java.util.List;
import java.util.Optional;

/**
 * Represents a grid structure for managing cards in the game.
 * Provides methods for querying, placing, and activating cards at specific positions,
 * as well as managing activation patterns and turn-based state.
 */
public interface Grid {
    /**
     * Returns the card at the specified grid position, if any.
     *
     * @param position the position in the grid to query
     * @return an Optional containing the card at the position, or empty if none exists
     */
    Optional<Card> getCard(GridPosition position);

    /**
     * Checks if a card can be placed at the specified grid position.
     *
     * @param position the position to check
     * @return true if a card can be placed at the position, false otherwise
     */
    boolean canPutCard(GridPosition position);

    /**
     * Places a card at the specified grid position.
     *
     * @param position the position to place the card
     * @param card the card to place
     * @throws IllegalArgumentException if the card cannot be placed at the position
     */
    void putCard(GridPosition position, Card card);

    /**
     * Checks if the card at the specified grid position can be activated.
     * A position is activatable if it contains a card and meets the activation pattern or game rules.
     *
     * @param position the position to check
     * @return true if the card at the position can be activated, false otherwise
     */
    boolean canBeActivated(GridPosition position);

    /**
     * Sets the card at the specified grid position as activated.
     * This may trigger effects associated with activation, such as scoring or card abilities.
     *
     * @param position the position of the card to activate
     * @throws IllegalArgumentException if the position cannot be activated
     */
    void setActivated(GridPosition position);

    /**
     * Sets the activation pattern for the grid.
     * The activation pattern determines which positions are eligible for activation.
     *
     * @param pattern a list of grid positions that define the activation pattern
     */
    void setActivationPattern(List<GridPosition> pattern);

    /**
     * Ends the current turn, performing any necessary state updates.
     */
    void endTurn();

    /**
     * Returns a string representation of the grid's current state.
     *
     * @return a string describing the grid state
     */
    String state();
}
