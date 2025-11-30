package sk.uniba.fmph.dcs.terra_futura.moveCard;

import sk.uniba.fmph.dcs.terra_futura.pile.Pile;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;
import sk.uniba.fmph.dcs.terra_futura.grid.Grid;

public interface MoveCard {
    /**
     * Executes the move of a card from the pile to the specified grid position.
     *
     * @param pile           The pile from which the card is taken.
     * @param gridCoordinate The position on the grid where the card should be placed.
     * @param grid           The grid where the card is placed.
     * @return true if the move was successful, false otherwise (e.g., pile empty, grid blocked).
     */
    boolean moveCard(final Pile pile, final GridPosition gridCoordinate, final Grid grid);
}

