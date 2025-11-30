package sk.uniba.fmph.dcs.terra_futura.actions;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;


/**
 * Manages the reward selection process when assistance is involved.
 * Tracks which player can select from available reward resources.
 */
public class SelectReward {

    private Optional<Integer> player;
    private List<Resource> selection;

    /**
     * Constructs a new SelectReward with no player and empty selection.
     */
    public SelectReward() {
        this.player = Optional.empty();
        this.selection = new ArrayList<>();
    }

    /**
     * Sets the reward context for a player.
     *
     * @param player The player ID who can select rewards.
     * @param card   The card associated with the reward (may be unused).
     * @param reward The list of available reward resources.
     * @return true if the reward was set successfully, false if reward is null.
     */
    public boolean setReward(final int player, final Card card, final List<Resource> reward) {
        if (reward == null || reward.isEmpty()) {
            clear();
            return false;
        }
        this.player = Optional.of(player);
        this.selection = new ArrayList<>(reward);
        return true;
    }

    /**
     * Checks if a resource is available for selection.
     *
     * @param source The resource to check.
     * @return true if the resource is available, false otherwise.
     */
    public boolean canSelectReward(final Resource source) {
        return player.isPresent() && selection.contains(source);
    }

    /**
     * Selects and removes a resource from the available options.
     *
     * @param resource The resource to select.
     */
    public void selectReward(final Resource resource) {
        if (canSelectReward(resource)) {
            selection.remove(resource);
        }
    }

    /**
     * Clear SelectReward State
     */
    public void clear() {
        this.player = Optional.empty();
        this.selection.clear();
    }

    /**
     * Returns the current state as a string for debugging.
     *
     * @return String representation of player and selection.
     */
    public String state() {
        return player.map(integer -> "Player: " + integer + ", Selection: " + selection.toString()).orElse("Player: None, Selection: []");
    }
}
