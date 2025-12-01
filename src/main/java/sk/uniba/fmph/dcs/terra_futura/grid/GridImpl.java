package sk.uniba.fmph.dcs.terra_futura.grid;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GridImpl implements Grid {

    private final Map<GridPosition, Card> gridMap;
    private final Set<GridPosition> processedPositions;
    private List<GridPosition> currentPattern;
    private final int lowLimit = -2;
    private final int highLimit = 2;

    public GridImpl() {
        this.gridMap = new HashMap<>();
        this.processedPositions = new HashSet<>();
        this.currentPattern = new ArrayList<>();
    }

    private boolean isPositionValid(final GridPosition pos) {
        if (pos == null) {
            return false;
        }
        return pos.x() >= lowLimit && pos.x() <= highLimit
                && pos.y() >= lowLimit && pos.y() <= highLimit;
    }

    /**
     * Retrieves the card at the specified grid position.
     *
     * @param coordinate The position to query.
     * @return An Optional containing the card if present, otherwise empty.
     */

    @Override
    public Optional<Card> getCard(final GridPosition coordinate) {
        if (coordinate == null || !gridMap.containsKey(coordinate)) {
            return Optional.empty();
        }
        return Optional.of(gridMap.get(coordinate));
    }

    /**
     * Checks if a card can be placed at the specified position.
     * A card can be placed if the position is within bounds and not already occupied.
     *
     * @param coordinate The position to check.
     * @return true if placement is allowed, false otherwise.
     */

    @Override
    public boolean canPutCard(final GridPosition coordinate) {
        if (!isPositionValid(coordinate)) {
            return false;
        }
        return !gridMap.containsKey(coordinate);
    }

    /**
     * Places a card at the specified position.
     *
     * @param coordinate The target position.
     * @param card       The card to place.
     * @throws IllegalArgumentException if the card is null, the position is invalid, or occupied.
     */

    @Override
    public void putCard(final GridPosition coordinate, final Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        if (!canPutCard(coordinate)) {
            throw new IllegalArgumentException(String.format("Cannot place card at %s. Position occupied or invalid.", coordinate));
        }
        gridMap.put(coordinate, card);
    }

    /**
     * Checks if the card at the specified position can be activated.
     * Possible only if:
     * 1. A card exists at the position.
     * 2. The position is part of the current activation pattern.
     *
     * @param coordinate The position to check.
     * @return true if activation is allowed.
     */

    @Override
    public boolean canBeActivated(final GridPosition coordinate) {

        if (!gridMap.containsKey(coordinate)) {
            return false;
        }

        return currentPattern.contains(coordinate);
    }

    /**
     * Marks the position as activated for the current turn.
     *
     * @param coordinate The position to activate.
     * @throws IllegalStateException if the position cannot be activated.
     */

    @Override
    public void setActivated(final GridPosition coordinate) {
        if (!canBeActivated(coordinate)) {
            throw new IllegalStateException("Position cannot be activated: " + coordinate);
        }
        processedPositions.add(coordinate);
    }

    /**
     * Sets the activation pattern for the current turn.
     *
     * @param pattern List of positions that are eligible for activation.
     */

    @Override
    public void setActivationPattern(final List<GridPosition> pattern) {
        this.currentPattern = (pattern == null) ? new ArrayList<>() : new ArrayList<>(pattern);
    }

    /**
     * Ends the current turn, clearing the activation pattern and processed positions.
     */

    @Override
    public void endTurn() {
        processedPositions.clear();
        currentPattern.clear();
    }

    /**
     * Returns a string representation of the grid's state.
     *
     * @return String describing cards, activated positions, and pattern.
     */

    @Override
    public String state() {
        String mapState = gridMap.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue().state())
                .collect(Collectors.joining(", "));

        return String.format("GridImpl{cards=[%s], activated=%s, pattern=%s}",
                mapState, processedPositions, currentPattern);
    }
}
