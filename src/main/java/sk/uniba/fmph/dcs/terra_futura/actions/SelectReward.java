package sk.uniba.fmph.dcs.terra_futura.actions;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;

public interface SelectReward {
    /**
     * Assigns a reward to the specified player based on a card effect.
     * The reward may consist of one or more resources granted to the player.
     *
     * @param player the ID of the player receiving the reward
     * @param card   the card that triggered the reward assignment
     * @param reward the list of resources granted as the reward
     */
    void setReward(int player, Card card, List<Resource> reward);

    /**
     * Checks whether the specified resource can be selected as a reward
     * by the current player. Implementations may restrict selection based
     * on available rewards, game state, or card effects.
     *
     * @param reward the resource the player wishes to select
     * @return {@code true} if the resource is available and selection is allowed;
     *         {@code false} otherwise
     */
    boolean canSelectReward(Resource reward);

    /**
     * Selects the provided resource as the player's reward.
     * This method should be invoked only after {@link #canSelectReward(Resource)}
     * returns {@code true}. Implementations apply the reward to the player's state
     * and update any internal reward-selection logic.
     *
     * @param reward the resource chosen by the player
     */
    void selectReward(Resource reward);

    /**
     * Returns a text representation of the internal reward state,
     * including information about pending rewards, selected rewards,
     * and any card interactions that influence reward selection.
     *
     * @return a human-readable string describing the reward system state
     */
    String state();
    
}
