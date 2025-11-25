package sk.uniba.fmph.dcs.terra_futura.actions;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SelectReward {

    private Optional<Integer> player;
    private List<Resource> selection;

    public SelectReward() {
        this.player = Optional.empty();
        this.selection = new ArrayList<>();
    }

    public boolean setReward(int player, Card card, List<Resource> reward) {
        if (reward == null) {
            return false;
        }
        this.player = Optional.of(player);
        this.selection = new ArrayList<>(reward);
        return true;
    }

    public boolean canSelectReward(Resource source) {
        return selection.contains(source);
    }

    public void selectReward(Resource resource) {
        if (canSelectReward(resource)) {
            selection.remove(resource);
        }
    }

    public String state() {
        if (player.isEmpty()) {
            return "Player: None, Selection: []";
        }
        return "Player: " + player.get() + ", Selection: " + selection.toString();
    }
}
