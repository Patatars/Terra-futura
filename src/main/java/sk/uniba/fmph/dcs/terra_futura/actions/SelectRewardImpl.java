package sk.uniba.fmph.dcs.terra_futura.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

/**
 * Component responsible for managing the state of reward selection.
 * Holds information about the active player and the pool of available resources
 * they can choose from.
 */
public class SelectRewardImpl implements SelectReward {

    private Optional<Integer> activePlayer;
    private List<Resource> availableRewards;

    /**
     * Initializes the reward selector with an empty state.
     */
    public SelectRewardImpl() {
        this.activePlayer = Optional.empty();
        this.availableRewards = new ArrayList<>();
    }

    /**
     * Initializes a new reward selection session for a specific player.
     *
     * @param player   The ID of the player receiving the reward.
     * @param card The card generating the reward (context only, not stored).
     * @param reward    The list of resources offered as a reward.
     * @return true if the reward was successfully set; false if the input list is null or empty.
     */
    public boolean setReward(final int player, final Card card, final List<Resource> reward) {
        if (reward == null || reward.isEmpty()) {
            clear();
            return false;
        }

        this.activePlayer = Optional.of(player);
        // Defensive copy to prevent external modification
        this.availableRewards = new ArrayList<>(reward);
        return true;
    }

    /**
     * Verifies if a specific resource can be selected from the current pool.
     *
     * @param resource The resource to check.
     * @return true if there is an active player and the resource is available.
     */
    public boolean canSelectReward(final Resource resource) {
        if (activePlayer.isEmpty()) {
            return false;
        }
        return availableRewards.contains(resource);
    }

    /**
     * Processes the selection of a resource.
     * Removes the resource from the available pool if it is a valid choice.
     *
     * @param resource The resource the player wants to take.
     */
    public void selectReward(final Resource resource) {
        if (!canSelectReward(resource)) {
            return;
        }
        availableRewards.remove(resource);
    }

    /**
     * Resets the component to its initial empty state.
     * Clears the active player and the list of rewards.
     */
    public void clear() {
        this.activePlayer = Optional.empty();
        this.availableRewards.clear();
    }

    /**
     * Provides a string representation of the current internal state.
     *
     * @return Formatted state string.
     */
    public String state() {
        if (activePlayer.isEmpty()) {
            return "SelectReward{player=None, rewards=[]}";
        }
        return String.format("SelectReward{player=%d, rewards=%s}",
                activePlayer.get(), availableRewards.toString());
    }
}
