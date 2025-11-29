package sk.uniba.fmph.dcs.terra_futura.grid;

import sk.uniba.fmph.dcs.terra_futura.card.Card;

import java.util.*;

public final class GridImpl implements Grid, InterfaceActivateGrid {
    // Stores cards on coordinates
    private final Map<GridPosition, Card> cards = new HashMap<>();

    // Stores which coordinates were activated this turn
    private final Set<GridPosition> cardsActivatedThisTurn = new HashSet<>();

    // Activation pattern for this turn (List of grid positions)
    private List<GridPosition> activationPattern = new ArrayList<>();

    /**
     * Returns the card at the given grid coordinate if one is present.
     *
     * @param coordinate the grid coordinate to query
     * @return an {@link Optional} containing the card at the coordinate,
     *         or an empty optional if no card is present
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
    public boolean canBeActivated(final GridPosition coordinate) {
        return getCard(coordinate).isPresent()
                && activationPattern.contains(coordinate);
    }

    /**
     * Marks the card at the given coordinate as activated for the current turn.
     * The coordinate must be activatable according to {@link #canBeActivated(GridPosition)}.
     *
     * @param coordinate the coordinate of the card to mark as activated
     * @throws IllegalStateException if the card at the coordinate cannot be
     *                               activated
     */
    @Override
    public void setActivated(final GridPosition coordinate) {
        if (!canBeActivated(coordinate)) {
            throw new IllegalStateException("Card cannot be activated at " + coordinate);
        }
        cardsActivatedThisTurn.add(coordinate);
    }

    /**
     * Sets the activation pattern for this grid using logical grid positions.
     * Only positions included in this pattern can be activated during the
     * current turn.
     *
     * @param pattern list of grid positions that define the activation pattern
     */
    @Override
    public void setActivationPattern(final List<GridPosition> pattern) {
        this.activationPattern = new ArrayList<>(pattern);
    }

    /**
     * Ends the current turn by clearing per-turn activation memory.
     * Clears the set of activated positions and the activation pattern.
     */
    @Override
    public void endTurn() {
        cardsActivatedThisTurn.clear();
        activationPattern = Collections.emptyList();
    }

    /**
     * Returns a string representation of the grid state.
     * Includes information about placed cards, activated positions, and the
     * current activation pattern.
     *
     * @return a string representation of the grid
     */
    @Override
    public String state() {
        StringBuilder sb = new StringBuilder("Grid{\n");
        cards.forEach((pos, card) ->
                sb.append("  ").append(pos).append(" -> ").append(card.state()).append("\n"));
        sb.append("cardsActivatedThisTurn=").append(cardsActivatedThisTurn).append("\n");
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
