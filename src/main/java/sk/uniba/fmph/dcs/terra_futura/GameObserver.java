package sk.uniba.fmph.dcs.terra_futura;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GameObserver {

    // Map of player ID (int) to their corresponding observer interface.
    private final Map<Integer, ObserverInterface> observers = new ConcurrentHashMap<>();

    /**
     * Get the map of all registered observers.
     *
     * @return a map of player IDs to their corresponding observer interfaces
     */
    public Map<Integer, ObserverInterface> getObservers() {
        return observers;
    }

    /**
     * Register an observer for a specific player.
     *
     * If an observer already exists for the given player ID, it will be
     * replaced with the new observer.
     *
     * @param playerId the unique identifier for the player
     * @param observer the observer to notify when this player's state changes;
     *                 must not be null
     */
    public void addObserver(final int playerId, final ObserverInterface observer) {
        observers.put(playerId, observer);
    }

    /**
     * Unregister the observer for a specific player.
     *
     * @param playerId the unique identifier for the player whose observer
     *                 should be removed
     */
    public void removeObserver(final int playerId) {
        observers.remove(playerId);
    }

    /**
     * Notify all registered observers of their new game state.
     * @param newState state that should be notified
     * This method iterates through all registered observers and forwards the
     * appropriate state string to each one based on their player ID. This ensures
     * "everybody sees what he has to" - each observer only receives updates for
     * their specific player.
     */
    public void notifyAll(final Map<Integer, String> newState) {
        for (Map.Entry<Integer, ObserverInterface> entry : observers.entrySet()) {
            int playerId = entry.getKey();
            ObserverInterface observer = entry.getValue();

            // Look up the state for this specific player. Only notify if a
            // non-null state exists - this implements the "everybody sees what
            // he has to"
            String state = newState.get(playerId);

            if (state != null) {
                // Forward the state string to this player's observer
                observer.notify(state);
            }
        }
    }
}
