package sk.uniba.fmph.dcs.terra_futura.grid;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collection;
import java.util.AbstractMap;


/**
 * GridImpl is the default implementation of the Grid interface.
 * It represents a grid where cards can be placed and activated
 * according to a defined pattern.
 */
public final class GridImpl implements Grid, InterfaceActivateGrid {

    // Stores cards on coordinates
    private final Map<GridPosition, Card> cards = new HashMap<>();

    // Stores which coordinates were activated this turn
    private final Set<GridPosition> activatedThisTurn = new HashSet<>();

    // Activation pattern for this turn (List of grid positions)
    private List<GridPosition> activationPattern = new ArrayList<>();


    /**
     * Returns the card at the given grid coordinate if one is present.
     *
     * @param coordinate the grid coordinate to query
     * @return an {@link Optional} containing the card at the coordinate,
     *         or an empty optional if no card is present
     */
    public Optional<Card> getCard(final GridPosition coordinate) {
        return Optional.ofNullable(cards.get(coordinate));
    }


    /**
     * Checks whether a card can be placed at the given coordinate.
     * A card can be placed only if there is no other card already stored
     * at that coordinate.
     *
     * @param coordinate the grid coordinate to check
     * @return {@code true} if the coordinate is free, {@code false} otherwise
     */
    public boolean canPutCard(final GridPosition coordinate) {
        return !cards.containsKey(coordinate);
    }



    /**
     * Places a card at the given coordinate if the position is free.
     * Implementations must ensure that the coordinate is valid for this grid
     * and that no card is already present at that position.
     *
     * @param coordinate the grid coordinate where the card should be placed
     * @param card       the card to place
     * @throws IllegalStateException if a card is already present at the
     *                               specified coordinate
     */
    public void putCard(final GridPosition coordinate, final Card card) {
        if (!canPutCard(coordinate)) {
            throw new IllegalStateException("Cannot put card at " + coordinate + ": occupied.");
        }
        cards.put(coordinate, card);
    }


    /**
     * Determines whether the card at the given coordinate can be activated.
     * A card can be activated only if it exists at the coordinate and the
     * coordinate is included in the current activation pattern.
     *
     * @param coordinate the coordinate of the card to check
     * @return {@code true} if the card can be activated, {@code false} otherwise
     */
    public boolean canBeActivated(final GridPosition coordinate) {
        // Check if card exists at position
        if (getCard(coordinate).isEmpty()) {
            return false;
        }

        // Check if position is in the activation pattern
        return activationPattern != null && activationPattern.contains(coordinate);
    }


    /**
     * Marks the card at the given coordinate as activated for the current turn.
     * The coordinate must be activatable according to {@link #canBeActivated(GridPosition)}.
     *
     * @param coordinate the coordinate of the card to mark as activated
     * @throws IllegalStateException if the card at the coordinate cannot be
     *                               activated
     */
    public void setActivated(final GridPosition coordinate) {
        if (!canBeActivated(coordinate)) {
            throw new IllegalStateException("Card cannot be activated at " + coordinate);
        }
        activatedThisTurn.add(coordinate);
    }


    /**
     * Sets the activation pattern for this grid using logical grid positions.
     * Only positions included in this pattern can be activated during the
     * current turn.
     *
     * @param pattern list of grid positions that define the activation pattern
     */
    public void setActivationPattern(final List<GridPosition> pattern) {
        this.activationPattern = new ArrayList<>(pattern);
    }


    /**
     * Ends the current turn by clearing per-turn activation memory.
     * Clears the set of activated positions and the activation pattern.
     */
    public void endTurn() {
        activatedThisTurn.clear();
        activationPattern.clear();
    }



    /**
     * Returns a string representation of the grid state.
     * Includes information about placed cards, activated positions, and the
     * current activation pattern.
     *
     * @return a string representation of the grid
     */
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grid{cards=\n");

        for (Map.Entry<GridPosition, Card> entry : cards.entrySet()) {
            sb.append("  ").append(entry.getKey())
                    .append(" -> ").append(entry.getValue().state())
                    .append("\n");
        }

        sb.append("activatedThisTurn=").append(activatedThisTurn).append("\n");
        sb.append("activationPattern=").append(activationPattern).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Sets the activation pattern based on integer coordinate pairs.
     * This is an adapter method used by the activation interface to convert
     * raw integer coordinates into {@link GridPosition} instances.
     *
     * @param pattern a collection of (x, y) coordinate pairs defining the
     *                activation pattern
     */
    @Override
    public void setActivationPattern(final Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern) {
        List<GridPosition> logicalPattern = new ArrayList<>();
        for (AbstractMap.SimpleEntry<Integer, Integer> entry : pattern) {
            logicalPattern.add(new GridPosition(entry.getKey(), entry.getValue()));
        }
        setActivationPattern(logicalPattern);
    }
}
