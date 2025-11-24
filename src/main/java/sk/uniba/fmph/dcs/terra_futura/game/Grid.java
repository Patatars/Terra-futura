package sk.uniba.fmph.dcs.terra_futura.game;

import java.util.List;
import java.util.Optional;

public interface Grid {
    Optional<Card> getCard(GridPosition position);

    boolean canPutCard(GridPosition position);

    void putCard(GridPosition position, Card card);

    boolean canBeActivated(GridPosition position);

    void setActivated(GridPosition position);

    void setActivationPattern(List<GridPosition> pattern);

    void endTurn();

    String state();
}
