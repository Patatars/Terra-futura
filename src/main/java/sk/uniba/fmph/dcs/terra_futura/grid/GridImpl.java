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

public class GridImpl implements Grid, InterfaceActivateGrid{

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
     * @param pos position
     */
    private boolean isValidPosition(GridPosition pos) {
        int x = pos.x();
        int y = pos.y();
        return x >= MIN_COORD && x <= MAX_COORD && y >= MIN_COORD && y <= MAX_COORD;
    }

    @Override
    public Optional<Card> getCard(GridPosition position) {
        return Optional.ofNullable(cards.get(position));
    }

    @Override
    public boolean canPutCard(GridPosition position) {
        if (!isValidPosition(position)) {
            return false;
        }
        return !cards.containsKey(position);
    }

    /**
     * @param position the position to place the card
     * @param card     the card to place
     */
    @Override
    public void putCard(GridPosition position, Card card) {
        if (!canPutCard(position)) {
            throw new IllegalArgumentException("Cannot place card at " + position);
        }
        cards.put(position, card);
    }


    @Override
    public boolean canBeActivated(GridPosition position) {
        if (getCard(position).isEmpty()) {
            return false;
        }
        return activationPattern != null && activationPattern.contains(position);
    }

    /**
     * @param position the position of the card to activate
     */
    @Override
    public void setActivated(GridPosition position) {
        if (!canBeActivated(position)) {
            throw new IllegalArgumentException("Cannot activate card at " + position);
        }
        activatedThisTurn.add(position);
    }

    /**
     * @param pattern a list of grid positions that define the activation pattern
     */
    @Override
    public void setActivationPattern(List<GridPosition> pattern) {
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

    /**
     * Set activation pattern.
     * @param pattern current pattern
     */
    @Override
    public void setActivationPattern(Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern) {
        List<GridPosition> positions = new ArrayList<>();
        for (AbstractMap.SimpleEntry<Integer, Integer> coord : pattern) {
            positions.add(new GridPosition(coord.getKey(), coord.getValue()));
        }
        setActivationPattern(positions);
    }
}
