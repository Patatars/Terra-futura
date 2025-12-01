package sk.uniba.fmph.dcs.terra_futura.game;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardSource;
import sk.uniba.fmph.dcs.terra_futura.enums.Deck;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

/**
 * The primary contract for the Terra Futura game logic.
 * This interface defines the essential actions a player can perform during their turn,
 * such as acquiring cards, activating them, managing resources, and progressing the game state.
 */
public interface TerraFuturaInterface {

    /**
     * Attempts to move a card from the specified source to a target position on the player's grid.
     * The action is valid only if it is the requesting player's turn and the current game phase
     * allows for card acquisition.
     *
     * @param playerId    The ID of the player performing the action.
     * @param source      The source from which the card is drawn (deck type and index).
     * @param destination The coordinates on the grid where the card should be placed.
     * @return true if the card was successfully taken and placed, false otherwise.
     */
    boolean takeCard(final int playerId, final CardSource source, final GridPosition destination);

    /**
     * Performs the action of removing the bottom-most card from a specified deck.
     * This action is restricted to once per turn and must be performed before taking a new card.
     *
     * @param playerId The ID of the player requesting the discard.
     * @param deck     The target deck from which to discard.
     * @return true if the discard operation was successful, false otherwise.
     */
    boolean discardLastCardInDeck(final int playerId, final Deck deck);

    /**
     * Initiates the activation process for a card located at the given coordinates.
     * This method validates the inputs, outputs, and pollution allocation against the card's definition.
     *
     * @param playerId      The ID of the player activating the card.
     * @param card          The grid coordinates of the card to be activated.
     * @param inputs        A list of resources consumed during activation, mapped to their source positions.
     * @param outputs       A list of resources produced, mapped to their destination positions.
     * @param pollution     A list of positions where pollution tokens will be placed.
     * @param otherPlayerId Optional ID of another player involved (e.g., for assistance effects).
     * @param otherCard     Optional reference to another card involved in the interaction.
     * @throws IllegalStateException if the game is not in the activation phase or the player is invalid.
     */
    void activateCard(final int playerId, final GridPosition card,
                      final List<Pair<Resource, GridPosition>> inputs,
                      final List<Pair<Resource, GridPosition>> outputs,
                      final List<GridPosition> pollution,
                      final Optional<Integer> otherPlayerId,
                      final Optional<Card> otherCard);

    /**
     * Processes the selection of a bonus resource following a successful assistance action.
     *
     * @param playerId The ID of the player selecting the reward.
     * @param resource The specific resource chosen as the reward.
     */
    void selectReward(final int playerId, final Resource resource);

    /**
     * Concludes the active player's turn and advances the game state to the next player.
     * Also handles logic for triggering the end-game phase when specific conditions are met (e.g., turn limit).
     *
     * @param playerId The ID of the player finishing their turn.
     * @return true if the turn was successfully ended, false otherwise.
     */
    boolean turnFinished(final int playerId);

    /**
     * Sets the activation pattern to be used during the final rounds of the game.
     *
     * @param playerId The ID of the player making the selection.
     * @param card     The index representing the chosen pattern card.
     * @return true if the pattern was successfully selected, false otherwise.
     */
    boolean selectActivationPattern(final int playerId, final int card);

    /**
     * Chooses the method by which points will be calculated in the final scoring phase.
     *
     * @param playerId The ID of the player making the selection.
     * @param card     The index representing the chosen scoring card.
     * @return true if the scoring method was successfully applied, false otherwise.
     */
    boolean selectScoring(final int playerId, final int card);
}