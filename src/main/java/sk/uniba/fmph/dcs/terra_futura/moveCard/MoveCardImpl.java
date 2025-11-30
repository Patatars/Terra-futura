package sk.uniba.fmph.dcs.terra_futura.moveCard;

import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;
import sk.uniba.fmph.dcs.terra_futura.pile.Pile;
import sk.uniba.fmph.dcs.terra_futura.grid.Grid;
import sk.uniba.fmph.dcs.terra_futura.card.Card;


public class MoveCardImpl implements MoveCard {

    private final int index;

    public MoveCardImpl(final int index){
        this.index = index;
    }

    /**
     * Attempts to move a card from the pile to the grid.
     * <p>
     * The move is successful only if:
     * 1. A card exists at the specified index in the pile.
     * 2. The target position on the grid is valid and empty.
     * </p>
     * If successful, the card is removed from the pile and placed on the grid.
     *
     * @param pile           The source pile from which the card is taken.
     * @param gridCoordinate The target coordinate on the grid.
     * @param grid           The grid where the card should be placed.
     * @return true if the move was successful, false otherwise.
     */

    public boolean moveCard(Pile pile, GridPosition gridCoordinate, Grid grid) {

        if (pile.getCard(this.index).isEmpty()) {
            return false;
        }

        if (!grid.canPutCard(gridCoordinate)) {
            return false;
        }

        Card movedCard = pile.getCard(this.index).get();
        pile.takeCard(this.index);
        grid.putCard(gridCoordinate, movedCard);

        return true;
    }
}
