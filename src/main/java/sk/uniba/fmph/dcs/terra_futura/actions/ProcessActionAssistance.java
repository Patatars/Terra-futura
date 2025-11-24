package sk.uniba.fmph.dcs.terra_futura.actions;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.game.Card;
import sk.uniba.fmph.dcs.terra_futura.game.Grid;
import sk.uniba.fmph.dcs.terra_futura.game.GridPosition;

import java.util.*;

/**
 * Handles assistance action where a player uses a card effect from another player.
 */
public class ProcessActionAssistance {
    private final ResourceTransferService transferService;

    public ProcessActionAssistance(ResourceTransferService transferService) {
        this.transferService = transferService;
    }

    public ProcessActionAssistance() {
        this(new ResourceTransferService());
    }

    /**
     * Activates a card using assistance from another player.
     *
     * @param card            The card being activated
     * @param grid            The game grid
     * @param assistingPlayer ID of the assisting player
     * @param assistingCard   Card providing assistance
     * @param inputs          Resources required
     * @param outputs         Resources produced
     * @param pollution       Pollution generated
     * @return true if activation succeeded
     */
    public boolean activateCard(
            Card card,
            Grid grid,
            int assistingPlayer,
            Card assistingCard,
            List<Pair<Resource, GridPosition>> inputs,
            List<Pair<Resource, GridPosition>> outputs,
            List<GridPosition> pollution
    ) {
        if (card == null || grid == null) {
            return false;
        }

        GridPosition cardPosition = findCardPosition(grid, card);
        if (cardPosition == null) {
            return false;
        }

        if (!grid.canBeActivated(cardPosition)) {
            return false;
        }

        // Validate assistance parameters
        if (card.hasAssistance()) {
            if (assistingPlayer <= 0 || assistingCard == null) {
                return false;
            }
        }

        // Activate neighbors in the same row/column
        List<GridPosition> neighborPositions = getNeighborPositions(grid, cardPosition);
        Set<Card> alreadyActivatedCards = new HashSet<>();

        for (GridPosition pos : neighborPositions) {
            if (grid.canBeActivated(pos)) {
                Optional<Card> neighborCardOpt = grid.getCard(pos);
                if (neighborCardOpt.isPresent()) {
                    Card neighborCard = neighborCardOpt.get();
                    if (alreadyActivatedCards.add(neighborCard)) {
                        grid.setActivated(pos);

                    }
                }
            }
        }

        // Execute resource transfer using the service
        boolean success = transferService.executeTransaction(grid, inputs, outputs, pollution);

        if (success) {
            grid.setActivated(cardPosition);
        }

        return success;
    }

    /**
     * Finds the position of a card in the grid.
     *
     * @param grid The game grid
     * @param card The card to find
     * @return GridPosition or null if not found
     */
    private GridPosition findCardPosition(Grid grid, Card card) {
        // Since GridPosition is now a record, we need to iterate through all possible positions
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                GridPosition pos = new GridPosition(x, y);
                Optional<Card> foundCard = grid.getCard(pos);
                if (foundCard.isPresent() && foundCard.get().equals(card)) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Gets all neighbor positions in the same row or column.
     *
     * @param grid        The game grid
     * @param cardPos     The card position
     * @return List of neighbor positions
     */
    private List<GridPosition> getNeighborPositions(Grid grid, GridPosition cardPos) {
        List<GridPosition> neighbors = new ArrayList<>();
        int targetX = cardPos.x();
        int targetY = cardPos.y();

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                GridPosition pos = new GridPosition(x, y);

                // Skip the card itself
                if (pos.equals(cardPos)) {
                    continue;
                }

                // Check if in same row or column
                if (pos.x() == targetX || pos.y() == targetY) {
                    if (grid.getCard(pos).isPresent()) {
                        neighbors.add(pos);
                    }
                }
            }
        }

        return neighbors;
    }
}