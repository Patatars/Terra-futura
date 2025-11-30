package sk.uniba.fmph.dcs.terra_futura.grid;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.Optional;

/**
 * Represents a grid structure for managing cards in the game.
 * Provides methods for querying, placing, and activating cards at specific positions,
 * as well as managing activation patterns and turn-based state.
 */
public interface Grid extends InterfaceActivateGrid {
    /**
     * Returns the card at the specified grid coordinate, if any.
     *
     * @param coordinate the coordinate in the grid to query
     * @return an Optional containing the card at the coordinate, or empty if none exists
     */
    Optional<Card> getCard(GridPosition coordinate);

    /**
     * Checks if a card can be placed at the specified grid coordinate.
     *
     * @param coordinate the coordinate to check
     * @return true if a card can be placed at the coordinate, false otherwise
     */
    boolean canPutCard(GridPosition coordinate);

    /**
     * Places a card at the specified grid coordinate.
     *
     * @param coordinate the coordinate to place the card
     * @param card the card to place
     * @throws IllegalArgumentException if the card cannot be placed at the coordinate
     */
    void putCard(GridPosition coordinate, Card card);

    /**
     * Checks if the card at the specified grid coordinate can be activated.
     * A coordinate is activatable if it contains a card and meets the activation pattern or game rules.
     *
     * @param coordinate the coordinate to check
     * @return true if the card at the coordinate can be activated, false otherwise
     */
    boolean canBeActivated(GridPosition coordinate);

    /**
     * Sets the card at the specified grid coordinate as activated.
     * This may trigger effects associated with activation, such as scoring or card abilities.
     *
     * @param coordinate the coordinate of the card to activate
     * @throws IllegalArgumentException if the coordinate cannot be activated
     */
    void setActivated(GridPosition coordinate);

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
