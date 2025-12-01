package sk.uniba.fmph.dcs.terra_futura.game;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.card.CardSource;
import sk.uniba.fmph.dcs.terra_futura.enums.Deck;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

import java.util.List;
import java.util.Optional;

/**
 * The main game-control interface for Terra Futura.
 *
 * Defines all actions a player may perform during the game, including
 * taking cards, activating cards, selecting rewards, managing end-of-round
 * transitions, and choosing final scoring options.
 *
 * Implementations of this interface enforce game rules, turn order,
 * and state-machine transitions.
 */
public interface Interface {

    /**
     * Attempts to take a card from a specific source (deck + index) and
     * place it onto the player's grid.
     *
     * This action is only allowed:
     *  - if it is the player's turn,
     *  - if the current game state permits card acquisition,
     *  - if the destination coordinate is valid and empty.
     *
     * @param playerId       ID of the player performing the action
     * @param source         card source (deck identifier + positional index)
     * @param destination    grid coordinate where the card will be placed
     * @return true if the card was successfully taken and placed; false otherwise
     */
    boolean takeCard(int playerId, CardSource source, GridPosition destination);

    /**
     * Discards the oldest card from the specified deck belonging to the player.
     *
     * This action can be performed at most once per turn,
     * and only before the player takes a card.
     *
     * @param playerId   ID of the player discarding the card
     * @param deck       deck from which the oldest card is removed
     * @return true if the discard was allowed and executed; false otherwise
     */
    boolean discardLastCardFromDeck(int playerId, Deck deck);

    /**
     * Activates a card located on the player's grid.
     *
     * This method processes:
     *  - input resources and their source coordinates,
     *  - output resources and their destination coordinates,
     *  - pollution placement,
     *  - optional interaction with another player's card (used in specific effects).
     *
     * Activation may only occur in the ACTIVATE_CARD phase,
     * and only by the current active player.
     *
     * @param playerId        ID of the player performing the activation
     * @param card            coordinate of the card being activated
     * @param inputs          list of input resources with corresponding grid positions
     * @param outputs         list of output resources with corresponding grid positions
     * @param pollution       list of grid positions where pollution cubes are placed
     * @param otherPlayerId   ID of another player if interaction is required; empty otherwise
     * @param otherCard       grid coordinate of another player's card if applicable; empty otherwise
     */
    void activateCard(int playerId, GridPosition card,
                      List<Pair<Resource, GridPosition>> inputs,
                      List<Pair<Resource, GridPosition>> outputs,
                      List<GridPosition> pollution,
                      Optional<Integer> otherPlayerId,
                      Optional<GridPosition> otherCard);

    /**
     * Selects a resource reward obtained as the result of activating a card
     * with the "Assistance" ability.
     *
     * The game must currently be in the SELECT_REWARD state for this player.
     *
     * @param playerId   ID of the player selecting the reward
     * @param resource   resource chosen as the reward
     */
    void selectReward(int playerId, Resource resource);

    /**
     * Ends the current player's turn and advances the game state.
     *
     * This method:
     *  - validates that the player may end their turn,
     *  - finalizes the current turn,
     *  - advances to the next player or next round,
     *  - transitions into final phases once turn 9 is completed.
     *
     * @param playerId   ID of the player finishing their turn
     * @return true if the turn was successfully ended; false otherwise
     */
    boolean turnFinished(int playerId);

    /**
     * Chooses an activation pattern used in the final round.
     *
     * Each pattern defines the order and selection of cards
     * used during the final activation phase.
     *
     * @param playerId   ID of the player making the selection
     * @param card       pattern identifier (typically card index)
     * @return true if a valid pattern was selected; false otherwise
     */
    boolean selectActivationPattern(int playerId, int card);

    /**
     * Chooses a scoring card that will determine the player's scoring method.
     *
     * This selection is only available during the final scoring setup phase.
     *
     * @param playerId   ID of the player selecting the scoring method
     * @param card       index of the scoring card being selected
     * @return true if the scoring method was accepted; false otherwise
     */
    boolean selectScoring(int playerId, int card);
}

