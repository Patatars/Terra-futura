package sk.uniba.fmph.dcs.terra_futura.grid;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Default implementation of the game grid.
 * Manages card placement and activation within a 5x5 logical coordinate system (x, y ∈ [-2, 2]).
 */
public final class GridImpl implements Grid, InterfaceActivateGrid { // ← final class

    private static final int MIN_COORD = -2;
    private static final int MAX_COORD = 2;

    // Stores cards at their positions
    private final Map<GridPosition, Card> cards = new HashMap<>();

    // Tracks activated positions this turn
    private final Set<GridPosition> activatedThisTurn = new HashSet<>();

    // Current activation pattern for this turn
    private List<GridPosition> activationPattern = new ArrayList<>();

    /**
     * Checks if a position is within the valid grid bounds.
     *
     * @param pos the grid position to validate
     * @return true if the position is within [-2, 2] for both x and y, false otherwise
     */
    private boolean isValidPosition(final GridPosition pos) {
        int x = pos.x();
        int y = pos.y();
        return x >= MIN_COORD && x <= MAX_COORD && y >= MIN_COORD && y <= MAX_COORD;
    }

    @Override
    public Optional<Card> getCard(final GridPosition position) {
        return Optional.ofNullable(cards.get(position));
    }

    @Override
    public boolean canPutCard(final GridPosition position) {
        if (!isValidPosition(position)) {
            return false;
        }
        return !cards.containsKey(position);
    }

    @Override
    public void putCard(final GridPosition position, final Card card) {
        if (!canPutCard(position)) {
            throw new IllegalArgumentException("Cannot place card at " + position);
        }
        cards.put(position, card);
    }

    @Override
    public boolean canBeActivated(final GridPosition position) {
        if (getCard(position).isEmpty()) {
            return false;
        }
        return activationPattern != null && activationPattern.contains(position);
    }

    @Override
    public void setActivated(final GridPosition position) {
        if (!canBeActivated(position)) {
            throw new IllegalArgumentException("Cannot activate card at " + position);
        }
        activatedThisTurn.add(position);
    }

    @Override
    public void setActivationPattern(final List<GridPosition> pattern) {
        this.activationPattern = new ArrayList<>(pattern);
    }

    @Override
    public void endTurn() {
        activatedThisTurn.clear();
        activationPattern.clear();
    }

    @Override
    public String state() {
        StringBuilder sb = new StringBuilder("Grid{\n");
        for (Map.Entry<GridPosition, Card> entry : cards.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" -> ").append(entry.getValue().state()).append("\n");
        }
        sb.append("  activatedThisTurn=").append(activatedThisTurn).append("\n");
        sb.append("  activationPattern=").append(activationPattern).append("\n");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void setActivationPattern(final Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern) {
        List<GridPosition> positions = new ArrayList<>();
        for (AbstractMap.SimpleEntry<Integer, Integer> coord : pattern) {
            positions.add(new GridPosition(coord.getKey(), coord.getValue()));
        }
        setActivationPattern(positions);
    }
}
