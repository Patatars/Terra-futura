package sk.uniba.fmph.dcs.terra_futura.game;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardSource;
import sk.uniba.fmph.dcs.terra_futura.enums.Deck;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

import java.util.List;
import java.util.Optional;

/**
 * Core game interface for Terra Futura.
 * Coordinates player actions and manages state progression throughout the game.
 */
public interface TeraFuturaInterface {

    /**
     * Allows a player to draw a visible card and place it onto their territory grid.
     * This action is only permitted during the player's turn and when the game is in a card-drawing state.
     *
     * @param playerId the unique ID of the acting player
     * @param source specifies which deck and card index to take from
     * @param destination the target grid coordinate for placement
     * @return true if the card was successfully placed; false otherwise
     */
    boolean takeCard(int playerId, CardSource source, GridPosition destination);

    /**
     * Removes the oldest card from the specified deck.
     * This action may be performed at most once per turn, and only before taking a new card.
     *
     * @param playerId the unique ID of the acting player
     * @param deck the deck from which to discard the last card
     * @return true if the discard was executed; false otherwise
     */
    boolean discardLastCardInDeck(int playerId, Deck deck);

    /**
     * Executes the activation of a card on the player's grid.
     * Requires the game to be in the ACTIVATE_CARD phase and the correct player to be active.
     * May involve resource payments, pollution placement, or assistance from another player.
     *
     * @param playerId the unique ID of the activating player
     * @param card position of the card to activate
     * @param inputs list of resources (with their source positions) used as input
     * @param outputs list of resources (with target positions) to be produced
     * @param pollution list of positions where pollution cubes should be placed
     * @param otherPlayerId optional ID of another player (used in Assistance effects)
     * @param otherCard optional reference to another player's card (for copying effects)
     */
    void activateCard(int playerId, GridPosition card,
                      List<Pair<Resource, GridPosition>> inputs,
                      List<Pair<Resource, GridPosition>> outputs,
                      List<GridPosition> pollution,
                      Optional<Integer> otherPlayerId,
                      Optional<Card> otherCard);

    /**
     * Lets a player choose a reward resource after successfully using an Assistance effect.
     *
     * @param playerId the unique ID of the player receiving the reward
     * @param resource the resource type selected as a reward
     */
    void selectReward(int playerId, Resource resource);

    /**
     * Finalizes the current player’s turn and advances the game to the next player.
     * After the 9th turn, this triggers transitions to end-game phases.
     *
     * @param playerId the unique ID of the player ending their turn
     * @return true if the turn was properly concluded; false if the action was invalid
     */
    boolean turnFinished(int playerId);

    /**
     * Chooses an activation pattern from the player’s counting cards for the final activation round.
     *
     * @param playerId the unique ID of the selecting player
     * @param card index (0 or 1) of the counting card containing the desired pattern
     * @return true if the pattern was successfully selected; false otherwise
     */
    boolean selectActivationPattern(int playerId, int card);

    /**
     * Picks a scoring configuration from the player’s counting cards for final point calculation.
     *
     * @param playerId the unique ID of the selecting player
     * @param card index (0 or 1) of the counting card containing the scoring rule
     * @return true if the scoring method was accepted; false otherwise
     */
    boolean selectScoring(int playerId, int card);
}