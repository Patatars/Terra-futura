package sk.uniba.fmph.dcs.terra_futura.moveCard;

import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;
import sk.uniba.fmph.dcs.terra_futura.pile.Pile;
import sk.uniba.fmph.dcs.terra_futura.grid.Grid;
import sk.uniba.fmph.dcs.terra_futura.card.Card;


public class MoveCardImpl implements MoveCard {

    private final int index;

    public MoveCardImpl(int index){
        this.index = index;
    }

    public boolean moveCard(Pile pile, GridPosition gridCoordinate, Grid grid) {

        if(pile.getCard(this.index).isEmpty()){
            return false;
        }

        if(!grid.canPutCard(gridCoordinate)){
            return false;
        }

        Card movedCard = pile.getCard(this.index).get();
        pile.takeCard(this.index);
        grid.putCard(gridCoordinate, movedCard);

        return true;
    }
}
