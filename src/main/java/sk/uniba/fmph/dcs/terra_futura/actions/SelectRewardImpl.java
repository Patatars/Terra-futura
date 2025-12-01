package sk.uniba.fmph.dcs.terra_futura.actions;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link SelectReward} interface.
 * <p>
 * This class manages the process of assigning and selecting rewards
 * for a player. It tracks which player is currently eligible to choose
 * from a set of available resources and updates the selection state
 * as resources are taken.
 * </p>
 */
public class SelectRewardImpl implements SelectReward {

    private Optional<Integer> player;
    private List<Resource> selection;
    /**
     * Creates a new {@code SelectRewardImpl} instance with no active player
     * and an empty list of selectable resources.
     */
    public SelectRewardImpl() {
        this.player = Optional.empty();
        this.selection = new ArrayList<>();
    }

    /**
     * Defines the reward context for a player, including the resources
     * that can be chosen.
     *
     * @param player the ID of the player who is allowed to select rewards
     * @param card   the card associated with the reward (not used in this implementation)
     * @param reward the list of resources available for selection
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
     * Determines whether the given resource can currently be selected
     * by the active player.
     *
     * @param resource the resource to check
     * @return {@code true} if the resource is available for selection,
     *         {@code false} otherwise
     */
    @Override
    public boolean canSelectReward(final Resource resource) {
        return player.isPresent() && selection.contains(resource);
    }

    /**
     * Selects a resource for the active player and removes it from
     * the list of available options.
     *
     * @param resource the resource to select
     */
    @Override
    public void selectReward(final Resource resource) {
        if (canSelectReward(resource)) {
            selection.remove(resource);
        }
    }

    /**
     * Returns a textual representation of the current reward state.
     * Useful for debugging and logging.
     *
     * @return a string containing the active player ID (or "Undefined")
     *         and the list of remaining selectable resources
     */
    @Override
    public String state() {
        if (player.isEmpty()) {
            return "Player: Undefined ";
        }
        return "Player: " + player.get() + ", Selection: " + selection.toString();
    }
}
