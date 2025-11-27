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


public class GridImpl implements Grid, InterfaceActivateGrid {

    // Stores cards on coordinates
    private final Map<GridPosition, Card> cards = new HashMap<>();

    // Stores which coordinates were activated this turn
    private final Set<GridPosition> activatedThisTurn = new HashSet<>();

    // Activation pattern for this turn (List of grid positions)
    private List<GridPosition> activationPattern = new ArrayList<>();


    public Optional<Card> getCard(final GridPosition coordinate) {
        return Optional.ofNullable(cards.get(coordinate));
    }


    // Can a card be placed at the given coordinate?
    public boolean canPutCard(final GridPosition coordinate) {
        return !cards.containsKey(coordinate);
    }



    // Places a card if the position is free.
    public void putCard(final GridPosition coordinate, final Card card) {
        if (!canPutCard(coordinate)) {
            throw new IllegalStateException("Cannot put card at " + coordinate + ": occupied.");
        }
        cards.put(coordinate, card);
    }


    /** A card can be activated only if:
     * 1) There is a card on the coordinate
     * 2) It was not activated before in this turn
     */
    public boolean canBeActivated(final GridPosition coordinate) {
        // Check if card exists at position
        if (getCard(coordinate).isEmpty()) {
            return false;
        }

        // Check if position is in the activation pattern
        return activationPattern != null && activationPattern.contains(coordinate);
    }


    // Marks a card as activated for this turn
    public void setActivated(final GridPosition coordinate) {
        if (!canBeActivated(coordinate)) {
            throw new IllegalStateException("Card cannot be activated at " + coordinate);
        }
        activatedThisTurn.add(coordinate);
    }


    public void setActivationPattern(final List<GridPosition> pattern) {
        this.activationPattern = new ArrayList<>(pattern);
    }


    // Clears per-turn activation memory
    public void endTurn() {
        activatedThisTurn.clear();
        activationPattern.clear();
    }



    /**
     * Returns a string representation of the grid state.
     * Includes information about placed cards, activated positions, and activation pattern.
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

    @Override
    public void setActivationPattern(final Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern) {

    }
}

