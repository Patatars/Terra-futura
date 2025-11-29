package sk.uniba.fmph.dcs.terra_futura.deck;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PileImpl implements Pile {
    public final List<CardImpl> cards;

    public PileImpl(List<CardImpl> cards) {
        this.cards = cards;
    }

    @Override
    public Optional<Card> getCard(int index) {
        if(index<0 || index> cards.size()){
            return Optional.empty();
        }
        return Optional.of((Card) cards.get(index));
    }

    @Override
    public void takeCardP(int cardIndex) {
     if(cardIndex>0 && cardIndex<=cards.size()){
          cards.remove(cardIndex);
     }
    }

    @Override
    public void removeLastCard() {
        if(cards.isEmpty()){
            return;
        }
        IntStream.range(0, cards.size())
                .max()
                .ifPresent(cards::remove);
    }

    @Override
    public String state() {
        if (cards.isEmpty()) {
            return "PileImpl{cards=[]}";
        }

        String cardsState = cards.stream()
                .map(CardImpl::state)
                .collect(Collectors.joining(", "));
        return String.format("PileImpl{cards=[%s]}", cardsState);

    }
}
