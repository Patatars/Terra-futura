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
 * Main interface for Terra Futura game.
 * Handles player actions and game state transitions.
 */
public interface TerraFuturaInterface {

    /**
     * Player takes a card from the pile and places it on their grid.
     * Only works if it's the player's turn and state allows taking cards.
     *
     * @param playerId player identifier
     * @param source card source (deck and index)
     * @param destination grid position to place card
     * @return true if successful, false otherwise
     */
    boolean takeCard(int playerId, CardSource source, GridPosition destination);

    /**
     * Discards the oldest card from deck.
     * Can be done once per turn before taking a card.
     *
     * @param playerId player identifier
     * @param deck deck to discard from
     * @return true if successful, false otherwise
     */
    boolean discardLastCardInDeck(int playerId, Deck deck);

    /**
     * Activates a card on player's grid.
     * Throws IllegalStateException if not in ACTIVATE_CARD state or wrong player.
     *
     * @param playerId player identifier
     * @param card grid position of card to activate
     * @param inputs input resources with positions
     * @param outputs output resources with positions
     * @param otherPlayerId optional id of other player
     * @param otherCard optional position of other card
     * @param pollution pollution positions
     */
    void activateCard(int playerId, GridPosition card,
                      List<Pair<Resource, GridPosition>> inputs,
                      List<Pair<Resource, GridPosition>> outputs,
                      List<GridPosition> pollution,
                      Optional<Integer> otherPlayerId,
                      Optional<Card> otherCard);

    /**
     * Selects reward after card with Assistance was activated.
     *
     * @param playerId player identifier
     * @param resource selected resource as reward
     */
    void selectReward(int playerId, Resource resource);

    /**
     * Ends current player's turn and moves to next player.
     * Handles transitions to final phases after turn 9.
     *
     * @param playerId player identifier
     * @return true if successful, false otherwise
     */
    boolean turnFinished(int playerId);

    /**
     * Selects activation pattern for final round.
     *
     * @param playerId player identifier
     * @param card card index for pattern
     * @return true if successful, false otherwise
     */
    boolean selectActivationPattern(int playerId, int card);

    /**
     * Selects scoring method for final scoring phase.
     *
     * @param playerId player identifier
     * @param card card index for scoring
     * @return true if successful, false otherwise
     */
    boolean selectScoring(int playerId, int card);

}
