package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import java.util.Optional;
import java.util.List;

public interface Pile {

    public Optional<Card> getCard(int index);

    public void takeCard(int index);

    public void removeLastCard();

    String state();
}
