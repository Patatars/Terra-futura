package sk.uniba.fmph.dcs.terra_futura.grid;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.*;

/**
 * Implementation of Grid
 */
public class GridImpl implements Grid{
    private final Map<GridPosition, Card> cards = new HashMap<>();
    private final Set<GridPosition> activatedThisTurn = new HashSet<>();
    private Collection<AbstractMap.SimpleEntry<Integer, Integer>> activationPattern = new ArrayList<>();


    /**
     * Return card at position if present.
     *
     * @param coordinate grid position
     * @return optional card
     */
    @Override
    public Optional<Card> getCard(GridPosition coordinate) {
        if (!cards.containsKey(coordinate)) {
            return Optional.empty();
        }
        return Optional.ofNullable(cards.get(coordinate));
    }

    /**
     * Check whether a card can be placed to the specified position.
     *
     * @param coordinate target position
     * @return true when position is free
     */
    @Override
    public boolean canPutCard(GridPosition coordinate) {
        return !cards.containsKey(coordinate);
    }

    /**
     * Place a card to the grid.
     *
     * @param coordinate target position
     * @param card card to place
     * @throws IllegalArgumentException when position is occupied
     */
    @Override
    public void putCard(GridPosition coordinate, Card card) {
        if (!canPutCard(coordinate)) throw new IllegalArgumentException("Invalid position");
        cards.put(coordinate, card);
    }

    /**
     * Check if the card at given position can be activated.
     *
     * @param coordinate position to check
     * @return true if activatable
     * @throws IllegalStateException when activation pattern is not defined
     */
    @Override
    public boolean canBeActivated(GridPosition coordinate) {
        if (!cards.containsKey(coordinate)) {
            return false;
        }
        if (activatedThisTurn.contains(coordinate)) {
            return false;
        }
        if (activationPattern == null || activationPattern.isEmpty()) {
            throw new IllegalStateException("Cannot activate grid when there is no activation pattern");
        }
        return activationPattern.stream().anyMatch(p -> p.equals(new AbstractMap.SimpleEntry<>(coordinate.x(), coordinate.y())));
    }

    /**
     * Mark card at given position as activated for this turn.
     *
     * @param coordinate position to activate
     * @throws IllegalArgumentException when activation is not allowed
     */
    @Override
    public void setActivated(GridPosition coordinate) {
        if (!canBeActivated(coordinate)) {
            throw new IllegalArgumentException("Cannot activate card at position: " + coordinate);
        }
        activatedThisTurn.add(coordinate);
    }


    /**
     * Clear activations for the end of turn.
     */
    @Override
    public void endTurn() {
        activatedThisTurn.clear();
    }

    /**
     * Make JSON state string.
     *
     * @return serialized state
     */
    @Override
    public String state() {
        JSONObject result = new JSONObject();
        JSONArray cardsArray = new JSONArray();
        for (Map.Entry<GridPosition, Card> entry : cards.entrySet()) {
            JSONObject cardObj = new JSONObject();
            cardObj.put("x", entry.getKey().x());
            cardObj.put("y", entry.getKey().y());
            cardObj.put("card", entry.getValue().state());
            cardsArray.put(cardObj);
        }
        result.put("cards", cardsArray);

        JSONArray activatedArray = new JSONArray();
        for (GridPosition pos : activatedThisTurn) {
            JSONObject posObj = new JSONObject();
            posObj.put("x", pos.x());
            posObj.put("y", pos.y());
            activatedArray.put(posObj);
        }
        result.put("activatedThisTurn", activatedArray);

        JSONArray patternArray = new JSONArray();
        for (var entry : activationPattern) {
            JSONObject patternObj = new JSONObject();
            patternObj.put("x", entry.getKey());
            patternObj.put("y", entry.getValue());
            patternArray.put(patternObj);
        }
        result.put("activationPattern", patternArray);

        return result.toString();
    }

    public void setActivationPattern(Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern) {
        this.activationPattern = pattern;
    }
}
