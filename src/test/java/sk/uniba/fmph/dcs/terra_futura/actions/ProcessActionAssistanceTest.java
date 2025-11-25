package sk.uniba.fmph.dcs.terra_futura.actions;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.Grid;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;


import java.util.*;

import static org.junit.Assert.*;


//Unit tests for ProcessActionAssistance

public class ProcessActionAssistanceTest {

    private ProcessActionAssistance action;
    private FakeGrid grid;
    private FakeCard card;
    private FakeCard assistingCard;
    private FakeResourceTransferService transfer;
    private GridPosition cardPosition = new GridPosition(1, 1);

    @Before
    public void setup() {
        transfer = new FakeResourceTransferService();
        action = new ProcessActionAssistance(transfer);
        grid = new FakeGrid();

        card = new FakeCard();
        assistingCard = new FakeCard();

        // Place card at (1,1)
        grid.putCard(cardPosition, card);
    }


    @Test
    public void testCardNotFoundOnGrid() {
        FakeCard other = new FakeCard();

        boolean ok = action.activateCard(other, grid, 1, assistingCard,
                List.of(), List.of(), List.of());

        assertFalse(ok);
    }

    @Test
    public void testCardCannotBeActivated() {
        grid.forbidActivation(cardPosition);

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                List.of(), List.of(), List.of());

        assertFalse(ok);
    }

    @Test
    public void testAssistanceRequiredButMissingParameters() {
        card.setHasAssistance(true);

        boolean ok = action.activateCard(card, grid,
                0, null, // invalid
                List.of(), List.of(), List.of());

        assertFalse(ok);
    }



    @Test
    public void testCardPositionFoundByReference() {

        assertTrue(grid.getCard(cardPosition).isPresent());

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                List.of(), List.of(), List.of());

        assertTrue(ok);
        assertTrue(card.activated);
    }

    @Test
    public void testAssistanceRequiredButNoAssistingCard() {
        card.setHasAssistance(true);


        boolean ok = action.activateCard(card, grid,
                1, null,
                List.of(), List.of(), List.of());

        assertFalse(ok);
    }


    @Test
    public void testActivationWithInputResources() {
        transfer.shouldFail = false;
        List<Pair<Resource, GridPosition>> inputs = List.of(
                Pair.of(Resource.GREEN, cardPosition)
        );

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                inputs, List.of(), List.of());

        assertTrue(ok);
        assertTrue(card.activated);
    }

    @Test
    public void testActivationWithOutputResources() {
        transfer.shouldFail = false;
        List<Pair<Resource, GridPosition>> outputs = List.of(
                Pair.of(Resource.MONEY, new GridPosition(2, 2))
        );

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                List.of(), outputs, List.of());

        assertTrue(ok);
        assertTrue(card.activated);
    }
    @Test
    public void testActivationWithNegativeLevel() {
        transfer.shouldFail = false;

        boolean ok = action.activateCard(card, grid, -5, assistingCard,
                List.of(), List.of(), List.of());


        assertTrue(ok);
        assertTrue(card.activated);
    }


    @Test
    public void testActivationWithPollution() {
        transfer.shouldFail = false;
        List<GridPosition> pollution = List.of(
                new GridPosition(0, 0), new GridPosition(2, 0)
        );

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                List.of(), List.of(), pollution);

        assertTrue(ok);
        assertTrue(card.activated);
    }

    @Test
    public void testActivationFailsWithInvalidGridPositionInInputs() {
        transfer.shouldFail = false;

        List<Pair<Resource, GridPosition>> badInputs = List.of(
                Pair.of(Resource.GREEN, new GridPosition(999, 999)) // Нет такой позиции
        );

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                badInputs, List.of(), List.of());


        assertTrue(ok);
    }
    @Test
    public void testNeighborsActivatedOnlyOnce() {
        FakeCard neighbor1 = new FakeCard();

        grid.putCard(new GridPosition(1, 0), neighbor1);
        grid.putCard(new GridPosition(0, 1), neighbor1);


        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                List.of(), List.of(), List.of());

        assertTrue(ok);
        assertTrue(neighbor1.activated);
        assertTrue(card.activated);
    }

    @Test
    public void testNeighborsActivated() {
        FakeCard neighbor1 = new FakeCard();
        FakeCard neighbor2 = new FakeCard();

        // Same row and same column
        grid.putCard(new GridPosition(1, 0), neighbor1);
        grid.putCard(new GridPosition(0, 1), neighbor2);

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                List.of(), List.of(), List.of());

        assertTrue(ok);
        assertTrue(neighbor1.activated);
        assertTrue(neighbor2.activated);
        assertTrue(card.activated);
    }

    @Test
    public void testTransactionFails() {
        transfer.shouldFail = true;

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                List.of(), List.of(), List.of());

        assertFalse(ok);
        assertFalse(card.activated);
    }

    @Test
    public void testSuccessfulActivation() {
        transfer.shouldFail = false;

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                List.of(), List.of(), List.of());

        assertTrue(ok);
        assertTrue(card.activated);
    }

    @Test
    public void testSuccessfulActivation_NoAssistanceRequired() {
        card.setHasAssistance(false);
        transfer.shouldFail = false;

        boolean ok = action.activateCard(card, grid,
                0, null, List.of(), List.of(), List.of());

        assertTrue(ok);
        assertTrue(card.activated);
    }

    @Test
    public void testFindAndUseCorrectCardPosition() {
        FakeCard other = new FakeCard();
        grid.putCard(new GridPosition(0, 2), other);

        grid.allowActivation(cardPosition);
        transfer.shouldFail = false;

        boolean ok = action.activateCard(card, grid,
                1, assistingCard, List.of(), List.of(), List.of());

        assertTrue(ok);
    }

    @Test
    public void testNullInputLists() {
        transfer.shouldFail = false;

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                null, null, null);

        assertTrue(ok); // Should handle null lists gracefully
        assertTrue(card.activated);
    }

    @Test
    public void testActivationWhenManyNeighborsPresent() {
        FakeCard a = new FakeCard();
        FakeCard b = new FakeCard();
        FakeCard c = new FakeCard();
        FakeCard d = new FakeCard();

        grid.putCard(new GridPosition(1, 0), a);
        grid.putCard(new GridPosition(1, 2), b);
        grid.putCard(new GridPosition(0, 1), c);
        grid.putCard(new GridPosition(2, 1), d);

        transfer.shouldFail = false;

        boolean ok = action.activateCard(card, grid, 1, assistingCard,
                List.of(), List.of(), List.of());

        assertTrue(ok);
        assertTrue(a.activated);
        assertTrue(b.activated);
        assertTrue(c.activated);
        assertTrue(d.activated);
    }


    @Test
    public void testCardImplementsEqualsCorrectly() {
        FakeCard card1 = new FakeCard();
        FakeCard card2 = new FakeCard();

        grid.putCard(new GridPosition(2, 2), card1);

        boolean ok = action.activateCard(card2, grid, 1, assistingCard,
                List.of(), List.of(), List.of());

        assertFalse(ok);
        assertFalse(card1.activated);
    }

    // support classes

    private static class FakeCard implements Card {
        boolean hasAssistance = false;
        boolean activated = false;



        void setHasAssistance(boolean v) {
            hasAssistance = v;
        }

        @Override
        public boolean canGetResources(List<Resource> resources) {
            return true;
        }

        @Override
        public boolean getResources(List<Resource> resources) {
            return true;
        }

        @Override
        public boolean canPutResources(List<Resource> resources) {
            return true;
        }

        @Override
        public void putResources(List<Resource> resources) {
        }

        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return true;
        }

        @Override
        public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
            return true;
        }

        @Override
        public boolean hasAssistance() {
            return hasAssistance;
        }

        @Override
        public String state() {
            return "{}";
        }
    }

    private static class FakeGrid implements Grid {

        private final Map<GridPosition, Card> cards = new HashMap<>();
        private final Set<GridPosition> forbidden = new HashSet<>();
        private Card cardReference;
        private GridPosition cardRefPos;

        void forbidActivation(GridPosition p) {
            forbidden.add(p);
        }

        void allowActivation(GridPosition p) {
            forbidden.remove(p);
        }

        @Override
        public Optional<Card> getCard(GridPosition pos) {
            return Optional.ofNullable(cards.get(pos));
        }

        @Override
        public boolean canPutCard(GridPosition pos) {
            return !cards.containsKey(pos);
        }

        @Override
        public void putCard(GridPosition pos, Card card) {
            cards.put(pos, card);
            if (card instanceof FakeCard) {
                this.cardReference = card;
                this.cardRefPos = pos;
            }
        }

        @Override
        public boolean canBeActivated(GridPosition pos) {
            return cards.containsKey(pos) && !forbidden.contains(pos);
        }

        @Override
        public void setActivated(GridPosition pos) {
            Card c = cards.get(pos);
            if (c instanceof FakeCard f) {
                f.activated = true;
            }
        }

        @Override
        public void setActivationPattern(List<GridPosition> pattern) { }

        @Override
        public void endTurn() { }

        @Override
        public String state() {
            return "{}";
        }
    }

    private static class FakeResourceTransferService extends ResourceTransferService {
        boolean shouldFail = false;

        @Override
        public boolean executeTransaction(Grid grid,
                                          List<Pair<Resource, GridPosition>> inputs,
                                          List<Pair<Resource, GridPosition>> outputs,
                                          List<GridPosition> pollution) {
            return !shouldFail;
        }
    }
}