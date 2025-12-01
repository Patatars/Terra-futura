package sk.uniba.fmph.dcs.terra_futura.actions;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages the reward selection process triggered by an Assistance effect.
 * Only one player can be in the reward selection state at a time,
 * and they can choose from a predefined list of available resources.
 */
public class SelectReward {

    private Optional<Integer> activePlayer = Optional.empty();
    private List<Resource> availableRewards = new ArrayList<>();


    /**
     * Initializes the reward selection for a specific player.
     *
     * @param player the ID of the player who may select a reward
     * @param card the card that triggered the Assistance effect (currently unused)
     * @param reward the list of resources available for selection
     * @return {@code true} if the reward list is valid and non-empty; {@code false} otherwise
     */
    public boolean setReward(final int player, final Card card, final List<Resource> reward) {
        clear();
        if (reward == null || reward.isEmpty()) {
            return false;
        }
        this.activePlayer = Optional.of(player);
        this.availableRewards.addAll(reward);
        return true;
    }

    /**
     * Checks whether a given resource can be selected by the current player.
     *
     * @param source the resource to check for availability
     * @return {@code true} if a player is eligible and the resource is in the available list; {@code false} otherwise
     */
    public boolean canSelectReward(final Resource source) {
        return activePlayer.isPresent() && availableRewards.contains(source);
    }

    /**
     * Selects a resource from the available options, removing it from the list.
     * Has no effect if the resource is not available or no player is active.
     *
     * @param resource the resource to select
     */
    public void selectReward(final Resource resource) {
        if (canSelectReward(resource)) {
            availableRewards.remove(resource);
        }
    }

    /**
     * Resets the state: clears the active player and available rewards.
     */
    public void clear() {
        activePlayer = Optional.empty();
        availableRewards.clear();
    }

    /**
     * Returns a string representation of the current state for debugging purposes.
     *
     * @return a human-readable description of the active player and available rewards,
     *         or "Player: None, Selection: []" if no player is active
     */
    public String state() {
        if (activePlayer.isEmpty()) {
            return "Player: None, Selection: []";
        }
        return "Player: " + activePlayer.get() + ", Selection: " + availableRewards;
    }
}
