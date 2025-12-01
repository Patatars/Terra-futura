package sk.uniba.fmph.dcs.terra_futura.actions;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;

/**
 * Interface for managing the reward selection process.
 * Provides functionality to set up rewards for a player and handle the selection logic.
 */
public interface SelectReward {

    /**
     * Initializes a new reward selection session for a specific player.
     *
     * @param player  The ID of the player receiving the reward.
     * @param card The card generating the reward (context only).
     * @param reward    The list of resources offered as a reward.
     * @return true if the reward was successfully set, false otherwise (e.g., if rewards list is empty).
     */
    boolean setReward(int player, Card card, List<Resource> reward);

    /**
     * Checks if a specific resource is available for selection by the active player.
     *
     * @param resource The resource to check.
     * @return true if the resource can be selected, false otherwise.
     */
    boolean canSelectReward(Resource resource);

    /**
     * Processes the selection of a resource.
     * If valid, the resource is removed from the available pool.
     *
     * @param resource The resource the player wants to take.
     */
    void selectReward(Resource resource);

    /**
     * Resets the component to its initial empty state.
     * Clears the active player and the list of available rewards.
     */
    void clear();

    /**
     * Returns a string representation of the current state.
     *
     * @return Formatted state string showing the active player and remaining rewards.
     */
    String state();
}
