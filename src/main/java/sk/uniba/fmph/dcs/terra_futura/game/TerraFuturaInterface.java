package sk.uniba.fmph.dcs.terra_futura.game;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.card.CardSource;
import sk.uniba.fmph.dcs.terra_futura.enums.Deck;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

import java.util.List;

/**
 * Main interface for Terra Futura game.
 * Handles player actions and game state transitions.
 */
public interface TerraFuturaInterface {

    /**
     * Player takes a card from the pile and places it on their grid.
     * Only works if it's the player's turn and state allows taking cards.
     */
    boolean takeCard(int playerId, CardSource source, GridPosition destination);

    /**
     * Discards the oldest card from deck.
     * Can be done once per turn before taking a card.
     */
    boolean discardLastCardInDeck(int playerId, Deck deck);

    /**
     * Activates a card on player's grid.
     * Throws IllegalStateException if not in ACTIVATE_CARD state or wrong player.
     */
    void activateCard(int playerId, GridPosition card,
                      List<Pair<Resource, GridPosition>> inputs,
                      List<Pair<Resource, GridPosition>> outputs,
                      List<GridPosition> pollution);

    /**
     * Selects reward after card with Assistance was activated.
     */
    void selectReward(int playerId, Resource resource);

    /**
     * Ends current player's turn and moves to next player.
     * Handles transitions to final phases after turn 9.
     */
    boolean turnFinished(int playerId);

    /**
     * Selects activation pattern for final round.
     */
    boolean selectActivationPatter(int playerId, int card);

    /**
     * Selects scoring method for final scoring phase.
     */
    boolean selectScoring(int playerId, int card);

}
