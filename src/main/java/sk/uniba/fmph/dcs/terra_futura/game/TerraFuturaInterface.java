package sk.uniba.fmph.dcs.terra_futura.game;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

import java.util.List;
import java.util.Optional;

/**
 * Defines the core set of actions available to players and the game logic
 * within the Terra Futura game.
 */
public interface TerraFuturaInterface {

    /**
     * Allows a player to take a card from a specified source and place it at a specific
     * destination on their game grid.
     *
     * @param playerId The ID of the player performing the action.
     * @param cardSource The source from which the card is taken (e.g., Draw Deck, Market).
     * @param destination The target coordinates on the player's grid where the card is placed.
     * @return {@code true} if the action was executed successfully (e.g., the destination is valid), otherwise {@code false}.
     */
    boolean takeCard(int playerId, String cardSource, GridPosition destination);

    /**
     * Forces the top card of the specified deck to be discarded. This action may be
     * triggered by a rule or a specific card effect.
     *
     * @param playerId The ID of the player initiating the discard action.
     * @param deckType The specific deck (e.g., main deck, waste deck) from which the top card is discarded.
     * @return {@code true} if the deck was not empty and the card was successfully discarded, otherwise {@code false}.
     */
    boolean discardLastCardFromDeck(int playerId, String deckType);

    /**
     * Activates the effect of a card previously placed on the board, detailing all
     * resource consumption, production, and environmental changes (pollution).
     *
     * @param playerId The ID of the player activating the card.
     * @param gridCoordinate The card object, usually identified by its location on the grid, that is being activated.
     * @param inputs A list of resource/position pairs indicating which resources are consumed and their location on the grid.
     * @param outputs A list of resource/position pairs indicating which resources are produced and their destination on the grid.
     * @param pollution A list of grid positions where pollution or negative tokens are placed.
     * @param otherPlayerId Optional target player ID for interactive effects (if the card affects another player).
     * @param otherCard Optional target grid position for interactive effects involving another card.
     */
    void activateCard(int playerId, Card gridCoordinate, List<Pair<Resource, GridPosition>> inputs,
                      List<Pair<Resource, GridPosition>> outputs, List<GridPosition> pollution,
                      Optional<Integer> otherPlayerId, Optional<GridPosition> otherCard);

    /**
     * Allows a player to choose a specific resource as a reward (e.g., after completing a phase or scoring a threshold).
     *
     * @param playerId The ID of the player selecting the reward.
     * @param resource The type of resource chosen by the player.
     */
    void selectReward(int playerId, Resource resource);

    /**
     * Signals that the specified player has finished their turn. This triggers end-of-turn
     * processing, such as scoring, income generation, or passing the turn to the next player.
     *
     * @param playerId The ID of the player who completed the turn.
     * @return {@code true} if the turn completion also triggers the end-game condition, otherwise {@code false}.
     */
    boolean turnFinished(int playerId);

    /**
     * Allows the player to select a specific activation pattern if the card provides multiple possible effects.
     *
     * @param playerId The ID of the player making the selection.
     * @param card Identifier (e.g., card ID or position index) for the card whose pattern is being selected.
     * @return {@code true} if the selected pattern is valid for the current game state.
     */
    boolean selectActivationPattern(int playerId, int card);

    /**
     * Allows a player to select a card for scoring points, usually during a dedicated scoring phase
     * or upon meeting specific game conditions.
     *
     * @param playerId The ID of the player selecting the card for scoring.
     * @param card Identifier (e.g., card ID or position index) for the card that is being scored.
     * @return {@code true} if the selection and scoring action was valid.
     */
    boolean selectScoring(int playerId, int card);
}
