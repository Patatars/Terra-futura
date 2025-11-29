package sk.uniba.fmph.dcs.terra_futura.actions;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;

public interface SelectReward {
    void setReward(int player,Card card, List<Resource> reward);
    boolean canSelectReward(Resource reward);
    void selectReward(Resource reward);
    String state();

}
