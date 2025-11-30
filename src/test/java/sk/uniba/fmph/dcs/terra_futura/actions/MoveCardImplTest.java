package sk.uniba.fmph.dcs.terra_futura.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.Grid;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;
import sk.uniba.fmph.dcs.terra_futura.pile.Pile;
import sk.uniba.fmph.dcs.terra_futura.moveCard.MoveCard;
import sk.uniba.fmph.dcs.terra_futura.moveCard.MoveCardImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoveCardImplTest {

    private FakePile pile;
    private FakeGrid grid;
    private MoveCard moveCardAction;
    private final int CARD_INDEX = 0;

    @Before
    public void setUp() {
        pile = new FakePile();
        grid = new FakeGrid();
        // Создаем реализацию
        moveCardAction = new MoveCardImpl(CARD_INDEX);
    }

    // --- 1. Сценарий успеха ---
    @Test
    public void testMoveCard_Success() {
        FakeCard card = new FakeCard();
        pile.cards.add(card);
        grid.canPut = true;

        boolean result = moveCardAction.moveCard(pile, new GridPosition(0, 0), grid);

        Assert.assertTrue("Move should be successful", result);
        Assert.assertTrue("Card should be removed from pile", pile.cards.isEmpty());
        Assert.assertEquals("Card should be placed on grid", card, grid.lastPlacedCard);
    }

    // --- 2. Сценарий: Карта отсутствует в стопке ---
    @Test
    public void testMoveCard_PileEmpty_ReturnsFalse() {
        grid.canPut = true;

        boolean result = moveCardAction.moveCard(pile, new GridPosition(0, 0), grid);

        Assert.assertFalse("Should return false if card not found", result);
        Assert.assertNull("No card should be placed on grid", grid.lastPlacedCard);
    }

    // --- 3. Сценарий: Поле не принимает карту ---
    @Test
    public void testMoveCard_GridRefuses_ReturnsFalseAndPreservesPile() {
        FakeCard card = new FakeCard();
        pile.cards.add(card);
        grid.canPut = false;

        boolean result = moveCardAction.moveCard(pile, new GridPosition(0, 0), grid);

        Assert.assertFalse("Should return false if grid refuses", result);
        Assert.assertFalse("Card should remain in pile", pile.cards.isEmpty());
        Assert.assertNull("Grid should not receive card", grid.lastPlacedCard);
    }

    // ==========================================
    // Вспомогательные фейковые классы (Stubs)
    // ==========================================

    private static class FakeCard implements Card {
        @Override public boolean canGetResources(List<Resource> resources) { return false; }
        @Override public boolean canPutResources(List<Resource> resources) { return false; }
        @Override public void getResources(List<Resource> resources) {}
        @Override public void putResources(List<Resource> resources) {}
        @Override public boolean check(List<Resource> input, List<Resource> output, int pollution) { return false; }
        @Override public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) { return false; }
        @Override public boolean hasAssistance() { return false; }
        @Override public String state() { return "FakeCard"; }
    }

    private static class FakePile implements Pile {
        List<Card> cards = new ArrayList<>();

        @Override
        public Optional<Card> getCard(int index) {
            if (index < 0 || index >= cards.size()) {
                return Optional.empty();
            }
            return Optional.of(cards.get(index));
        }

        @Override
        public void takeCard(int index) {
            if (index >= 0 && index < cards.size()) {
                cards.remove(index);
            }
        }

        @Override public void removeLastCard() {}
        @Override public String state() { return ""; }
    }

    // Исправленный FakeGrid: добавлены ВСЕ методы интерфейса Grid
    private static class FakeGrid implements Grid {
        boolean canPut = true;
        Card lastPlacedCard = null;

        @Override
        public Optional<Card> getCard(GridPosition coordinate) {
            return Optional.empty();
        }

        @Override
        public boolean canPutCard(GridPosition coordinate) {
            return canPut;
        }

        @Override
        public void putCard(GridPosition coordinate, Card card) {
            this.lastPlacedCard = card;
        }

        // --- Заглушки для методов, которые требуются интерфейсом, но не используются в MoveCard ---

        @Override
        public boolean canBeActivated(GridPosition coordinate) {
            return false;
        }

        @Override
        public void setActivated(GridPosition coordinate) {
            // Пусто, так как MoveCard это не использует
        }

        @Override
        public void setActivationPattern(List<GridPosition> pattern) {
            // Пусто
        }

        @Override
        public void endTurn() {
            // Пусто
        }

        @Override
        public String state() {
            return "FakeGrid";
        }
    }
}