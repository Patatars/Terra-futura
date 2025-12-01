package sk.uniba.fmph.dcs.terra_futura.actions;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles the reward-selection phase for a player.
 * Only one player may select rewards at a time. The class stores the ID of
 * that player and the list of resources still available to choose from.
 */
public class SelectRewardImpl implements SelectReward {

    /**
     * ID of the player currently allowed to pick a reward.
     * Empty when no reward selection is active.
     */
    private Optional<Integer> player;

    /**
     * Resources still available for the active player to select.
     */
    private List<Resource> selection;

    /**
     * Initializes an empty reward-selection state
     * (no active player and no selectable resources).
     */
    public SelectRewardImpl() {
        this.player = Optional.empty();
        this.selection = new ArrayList<>();
    }

    /**
     * Starts a reward-selection phase for a specific player.
     * The provided resources become the available options.
     *
     * @param player the player allowed to select a reward
     * @param card   the card granting the reward (not used)
     * @param reward resources available to be selected
     */
    @Override
    public void setReward(final int player, final Card card, final List<Resource> reward) {
        if (reward.isEmpty()) {
            return;
        }
        this.player = Optional.of(player);
        this.selection = new ArrayList<>(reward);
    }

    /**
     * Checks whether the given resource can currently
     * be selected by the active player.
     *
     * @param resource resource to test
     * @return true if selection is active and the resource is available
     */
    @Override
    public boolean canSelectReward(final Resource resource) {
        return player.isPresent() && selection.contains(resource);
    }

    /**
     * Selects a resource and removes it from the remaining options.
     *
     * @param resource the resource chosen by the player
     */
    @Override
    public void selectReward(final Resource resource) {
        if (canSelectReward(resource)) {
            selection.remove(resource);
        }
    }
    /**
     * Returns the current status of the reward-selection phase.
     *
     * @return textual representation of active player and remaining options
     */
    @Override
    public String state() {
        if (player.isEmpty()) {
            return "Player: Undefined";
        }
        return "Player: " + player.get() + ", Selection: " + selection.toString();
    }

}