package sk.uniba.fmph.dcs.terra_futura.game;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.card.CardSource;
import sk.uniba.fmph.dcs.terra_futura.deck.Pile;
import sk.uniba.fmph.dcs.terra_futura.deck.PileImpl;
import sk.uniba.fmph.dcs.terra_futura.deck.Shuffler;
import sk.uniba.fmph.dcs.terra_futura.effect.Effect;
import sk.uniba.fmph.dcs.terra_futura.effect.TransformationFixed;
import sk.uniba.fmph.dcs.terra_futura.enums.Deck;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.grid.Grid;
import sk.uniba.fmph.dcs.terra_futura.grid.GridImpl;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

import java.util.*;

import static org.junit.Assert.*;


public class TerraFuturaIntegrationTest {

    private TerraFuturaInterface game;
    private GridImpl grid1;
    private GridImpl grid2;
    private FakeObserver observer;

    private static class NamedCard extends CardImpl {
        private final String name;

        public NamedCard(String name, ArrayList<Resource> resources, int pollutionSpace,
                         Effect upperEffect, Effect lowerEffect) {
            super(resources, pollutionSpace, upperEffect, lowerEffect);
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private Card createTestCard(String name, Resource initialResource) {
        Effect upperEffect = new TransformationFixed(
                List.of(Resource.GREEN),
                List.of(Resource.RED),
                1
        );
        Effect lowerEffect = new TransformationFixed(
                List.of(Resource.RED),
                List.of(Resource.YELLOW),
                0
        );
        ArrayList<Resource> initialResources = new ArrayList<>();
        initialResources.add(initialResource);
        return new NamedCard(name, initialResources, 3, upperEffect, lowerEffect);
    }

    private Pile createControlledPile(List<Card> cards) {
        Shuffler noOpShuffler = list -> {};
        return new PileImpl(new ArrayList<>(cards), noOpShuffler);
    }

    @Before
    public void setUp() {
        grid1 = new GridImpl();
        grid2 = new GridImpl();
        observer = new FakeObserver();

        Map<Integer, Grid> grids = new HashMap<>();
        grids.put(1, grid1);
        grids.put(2, grid2);
        List<Card> testCards = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            testCards.add(createTestCard("Card" + i, Resource.GREEN));
        }

        Pile pile = createControlledPile(testCards);
        game = new Game(new int[]{1, 2}, grids, pile, 1);
    }


    @Test
    public void testPlayer1TakesCardSuccessfully() {
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        observer.assertCardAt(grid1, new GridPosition(0, 0), "Card0");
        Card placedCard = grid1.getCard(new GridPosition(0, 0)).get();
        assertTrue(placedCard.canGetResources(List.of(Resource.GREEN)));
    }

    @Test
    public void testWrongPlayerCannotTakeCard() {
        assertFalse(game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertFalse(grid1.getCard(new GridPosition(0, 0)).isPresent());
        assertFalse(grid2.getCard(new GridPosition(0, 0)).isPresent());
    }

    @Test
    public void testCannotPutCardOnOccupiedPosition() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(2);

        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(1, 0));
        game.turnFinished(2);

        assertFalse(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(grid1.getCard(new GridPosition(0, 0)).isPresent());
    }

    @Test
    public void testDiscardBeforeTakeCard() {
        assertTrue(game.discardLastCardInDeck(1, Deck.I));
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(grid1.getCard(new GridPosition(0, 0)).isPresent());
    }

    @Test
    public void testCannotDiscardTwice() {
        assertTrue(game.discardLastCardInDeck(1, Deck.I));
        assertFalse(game.discardLastCardInDeck(1, Deck.I));
    }

    @Test
    public void testWrongPlayerCannotDiscard() {
        assertFalse(game.discardLastCardInDeck(2, Deck.I));
    }

    @Test
    public void testTurnFinishedAfterTakingCard() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        assertTrue(game.turnFinished(1));
        assertTrue(game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
    }

    @Test
    public void testCannotFinishTurnBeforeTakingCard() {
        assertFalse(game.turnFinished(1));
    }

    @Test
    public void testWrongPlayerCannotFinishTurn() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        assertFalse(game.turnFinished(2));
    }

    @Test
    public void testFullTurnCycle() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(1);
        assertTrue(grid1.getCard(new GridPosition(0, 0)).isPresent());
        assertFalse(grid2.getCard(new GridPosition(0, 0)).isPresent());
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(2);
        assertTrue(grid2.getCard(new GridPosition(0, 0)).isPresent());
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0)));
    }


    @Test
    public void testMultipleTurnsWithAdjacentCards() {
        GridPosition[] positions = {
            new GridPosition(0, 0),
            new GridPosition(1, 0),
            new GridPosition(0, 1)
        };

        for (GridPosition pos : positions) {
            assertTrue("Failed at position " + pos,
                game.takeCard(1, new CardSource(Deck.I, 0), pos));
            assertTrue(game.turnFinished(1));
            assertTrue(game.takeCard(2, new CardSource(Deck.I, 0), pos));
            assertTrue(game.turnFinished(2));
        }

        for (GridPosition pos : positions) {
            assertTrue("Missing card at " + pos, grid1.getCard(pos).isPresent());
            assertTrue("Missing card at " + pos, grid2.getCard(pos).isPresent());
        }
    }

    @Test
    public void testCannotPlaceNonAdjacentCard() {
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 1)));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(1, 1));
        game.turnFinished(2);
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0)));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(1, 0));
        game.turnFinished(2);
        assertFalse(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 2)));
    }


    @Test
    public void testGridBoundingBoxConstraint() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(2);

        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(1, 0));
        game.turnFinished(2);

        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(2, 0));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(2, 0));
        game.turnFinished(2);

        assertFalse("Should not allow placement exceeding 3x3 bounding box",
            game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(3, 0)));
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(2, 1)));
    }

    @Test
    public void testFullGameToSelectActivationPattern() {
        GridPosition[] positions = getFullGridPositions();
        fillGridForBothPlayers(positions);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                assertTrue(grid1.getCard(new GridPosition(x, y)).isPresent());
                assertTrue(grid2.getCard(new GridPosition(x, y)).isPresent());
            }
        }
        assertTrue(game.selectActivationPattern(1, 0));
    }

    @Test
    public void testSelectActivationPatternWrongPlayer() {
        GridPosition[] positions = getFullGridPositions();
        fillGridForBothPlayers(positions);
        assertFalse(game.selectActivationPattern(2, 0));
    }

    @Test
    public void testEachPlayerHasOwnGrid() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        assertTrue(grid1.getCard(new GridPosition(0, 0)).isPresent());
        assertFalse(grid2.getCard(new GridPosition(0, 0)).isPresent());

        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        assertTrue(grid1.getCard(new GridPosition(0, 0)).isPresent());
        assertTrue(grid2.getCard(new GridPosition(0, 0)).isPresent());
    }


    @Test
    public void testCardResourcesIntegration() {
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));

        Optional<Card> optionalCard = grid1.getCard(new GridPosition(0, 0));
        assertTrue(optionalCard.isPresent());
        Card placedCard = optionalCard.get();
        assertTrue(placedCard.canGetResources(List.of(Resource.GREEN)));
        assertFalse(placedCard.canGetResources(List.of(Resource.RED)));
    }

    @Test
    public void testResourceManipulationOnPlacedCard() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));

        Card card = grid1.getCard(new GridPosition(0, 0)).get();
        assertTrue(card.canGetResources(List.of(Resource.GREEN)));
        card.getResources(List.of(Resource.GREEN));
        assertFalse(card.canGetResources(List.of(Resource.GREEN)));
        card.putResources(List.of(Resource.RED));
        assertTrue(card.canGetResources(List.of(Resource.RED)));
    }


    @Test
    public void testCardActivationSetup() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 1));
        assertTrue(grid1.getCard(new GridPosition(1, 1)).isPresent());
        grid1.setActivationPattern(Collections.singleton(
                new AbstractMap.SimpleEntry<>(1, 1)
        ));
        assertTrue(grid1.canBeActivated(new GridPosition(1, 1)));
    }

    @Test
    public void testMultipleCardsActivationPattern() {
        GridPosition[] positions = getFullGridPositions();
        fillGridForBothPlayers(positions);

        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern = Arrays.asList(
            new AbstractMap.SimpleEntry<>(0, 0),
            new AbstractMap.SimpleEntry<>(1, 1),
            new AbstractMap.SimpleEntry<>(2, 2)
        );
        grid1.setActivationPattern(pattern);

        assertTrue(grid1.canBeActivated(new GridPosition(0, 0)));
        assertTrue(grid1.canBeActivated(new GridPosition(1, 1)));
        assertTrue(grid1.canBeActivated(new GridPosition(2, 2)));
        assertFalse(grid1.canBeActivated(new GridPosition(0, 1)));
        assertFalse(grid1.canBeActivated(new GridPosition(1, 0)));
    }


    @Test
    public void testInvalidPlayerIdOperations() {
        assertFalse(game.takeCard(999, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertFalse(game.discardLastCardInDeck(999, Deck.I));
        assertFalse(game.turnFinished(999));
    }

    @Test
    public void testThreePlayerGame() {
        Map<Integer, Grid> threePlayerGrids = new HashMap<>();
        GridImpl g1 = new GridImpl();
        GridImpl g2 = new GridImpl();
        GridImpl g3 = new GridImpl();
        threePlayerGrids.put(1, g1);
        threePlayerGrids.put(2, g2);
        threePlayerGrids.put(3, g3);

        List<Card> cards = new ArrayList<>();
        Resource[] resources = {Resource.GREEN, Resource.RED, Resource.YELLOW, Resource.GEAR};
        for (int i = 0; i < 50; i++) {
            cards.add(createTestCard("Card" + i, resources[i % resources.length]));
        }

        TerraFuturaInterface threePlayerGame = new Game(
            new int[]{1, 2, 3}, threePlayerGrids, new PileImpl(cards), 1);

        assertTrue(threePlayerGame.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(threePlayerGame.turnFinished(1));

        assertTrue(threePlayerGame.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(threePlayerGame.turnFinished(2));

        assertTrue(threePlayerGame.takeCard(3, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(threePlayerGame.turnFinished(3));

        assertTrue(threePlayerGame.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0)));

        assertTrue(g1.getCard(new GridPosition(0, 0)).isPresent());
        assertTrue(g2.getCard(new GridPosition(0, 0)).isPresent());
        assertTrue(g3.getCard(new GridPosition(0, 0)).isPresent());
    }
    @Test
    public void testDiscardAvailableOnNewTurn() {
        assertTrue(game.discardLastCardInDeck(1, Deck.I));
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(game.turnFinished(1));

        assertTrue(game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(game.turnFinished(2));

        assertTrue(game.discardLastCardInDeck(1, Deck.I));
    }


    @Test
    public void testCompleteGameScenario() {
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(grid1.getCard(new GridPosition(0, 0)).isPresent());
        assertTrue(game.turnFinished(1));

        assertTrue(game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(grid2.getCard(new GridPosition(0, 0)).isPresent());
        assertTrue(game.turnFinished(2));

        GridPosition[] positions = {
                new GridPosition(1, 0),
                new GridPosition(0, 1),
                new GridPosition(1, 1),
                new GridPosition(2, 0),
                new GridPosition(2, 1),
                new GridPosition(0, 2),
                new GridPosition(1, 2),
                new GridPosition(2, 2)
        };

        for (GridPosition pos : positions) {
            assertTrue("Failed to take card at " + pos,
                game.takeCard(1, new CardSource(Deck.I, 0), pos));
            assertTrue(game.turnFinished(1));
            assertTrue("Failed to take card at " + pos,
                game.takeCard(2, new CardSource(Deck.I, 0), pos));
            assertTrue(game.turnFinished(2));
        }

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                GridPosition pos = new GridPosition(x, y);
                assertTrue("Grid1 missing card at " + pos, grid1.getCard(pos).isPresent());
                assertTrue("Grid2 missing card at " + pos, grid2.getCard(pos).isPresent());
            }
        }

        assertTrue(game.selectActivationPattern(1, 0));
    }


    @Test
    public void testNegativeCoordinatesAllowed() {
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(-1, -1)));
        assertTrue(grid1.getCard(new GridPosition(-1, -1)).isPresent());

        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(2);
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, -1)));
    }


    @Test
    public void testResourceManagementAcrossMultipleTurns() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        assertTrue(grid1.getCard(new GridPosition(0, 0)).isPresent());
        Card card1 = grid1.getCard(new GridPosition(0, 0)).get();
        assertTrue("Card1 should have GREEN", card1.canGetResources(List.of(Resource.GREEN)));

        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(2);

        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0));
        assertTrue(grid1.getCard(new GridPosition(1, 0)).isPresent());
        Card card2 = grid1.getCard(new GridPosition(1, 0)).get();

        assertTrue("Card1 still has GREEN", card1.canGetResources(List.of(Resource.GREEN)));
        assertTrue("Card2 has GREEN", card2.canGetResources(List.of(Resource.GREEN)));

        card1.getResources(List.of(Resource.GREEN));
        card2.putResources(List.of(Resource.RED));

        assertFalse("Card1 lost GREEN", card1.canGetResources(List.of(Resource.GREEN)));
        assertTrue("Card2 gained RED", card2.canGetResources(List.of(Resource.RED)));
    }

    @Test
    public void testGridStateSerialization() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(2);

        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0));

        String gridState = grid1.state();

        assertTrue("State should contain cards array", gridState.contains("cards"));
        assertTrue("State should be valid JSON-like format", gridState.startsWith("{"));
    }


    @Test
    public void testErrorRecoveryAfterInvalidMove() {
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(2);
        assertFalse(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(5, 5)));
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0)));
        assertTrue(grid1.getCard(new GridPosition(0, 0)).isPresent());
        assertTrue(grid1.getCard(new GridPosition(1, 0)).isPresent());
        assertFalse(grid1.getCard(new GridPosition(5, 5)).isPresent());
    }

    @Test
    public void testFullCycleWithDiscardAndResourceValidation() {
        assertTrue("P1 discard", game.discardLastCardInDeck(1, Deck.I));
        assertTrue("P1 take card", game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));

        Optional<Card> p1Card1Opt = grid1.getCard(new GridPosition(0, 0));
        assertTrue(p1Card1Opt.isPresent());
        assertTrue("P1 card has GREEN", p1Card1Opt.get().canGetResources(List.of(Resource.GREEN)));

        assertTrue(game.turnFinished(1));

        assertTrue("P2 take card", game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        Optional<Card> p2Card1Opt = grid2.getCard(new GridPosition(0, 0));
        assertTrue(p2Card1Opt.isPresent());
        assertTrue("P2 card has GREEN", p2Card1Opt.get().canGetResources(List.of(Resource.GREEN)));
        assertTrue(game.turnFinished(2));

        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0)));
        assertTrue(game.turnFinished(1));
        assertTrue(game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(1, 0)));
        assertTrue(game.turnFinished(2));

        assertEquals(2, countCardsOnGrid(grid1));
        assertEquals(2, countCardsOnGrid(grid2));
    }

    @Test
    public void testBoundingBoxEdgeCases() {
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(2);

        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0)));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(1, 0));
        game.turnFinished(2);

        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(2, 0)));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(2, 0));
        game.turnFinished(2);

        assertFalse("Should not allow X > 2 span",
                game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(3, 0)));

        assertTrue("Should allow Y expansion within bounds",
                game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(2, 1)));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(2, 1));
        game.turnFinished(2);

        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(2, 2)));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(2, 2));
        game.turnFinished(2);

        assertFalse("Should not allow Y > 2 span",
                game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(2, 3)));
    }

    @Test
    public void testActivationPatternAndTurnEnd() {
        GridPosition[] positions = getFullGridPositions();
        fillGridForBothPlayers(positions);

        Collection<AbstractMap.SimpleEntry<Integer, Integer>> pattern = Arrays.asList(
                new AbstractMap.SimpleEntry<>(0, 0),
                new AbstractMap.SimpleEntry<>(1, 0),
                new AbstractMap.SimpleEntry<>(2, 0)
        );
        grid1.setActivationPattern(pattern);

        assertTrue("(0,0) can be activated", grid1.canBeActivated(new GridPosition(0, 0)));
        assertTrue("(1,0) can be activated", grid1.canBeActivated(new GridPosition(1, 0)));
        assertTrue("(2,0) can be activated", grid1.canBeActivated(new GridPosition(2, 0)));
        assertFalse("(1,1) cannot be activated", grid1.canBeActivated(new GridPosition(1, 1)));

        grid1.setActivated(new GridPosition(0, 0));
        assertFalse("Cannot activate (0,0) again", grid1.canBeActivated(new GridPosition(0, 0)));

        grid1.endTurn();

        grid1.setActivationPattern(pattern);
        assertTrue("(0,0) can be activated after endTurn", grid1.canBeActivated(new GridPosition(0, 0)));
    }



    private GridPosition[] getFullGridPositions() {
        return new GridPosition[]{
            new GridPosition(0, 0),
            new GridPosition(1, 0),
            new GridPosition(2, 0),
            new GridPosition(2, 1),
            new GridPosition(1, 1),
            new GridPosition(0, 1),
            new GridPosition(0, 2),
            new GridPosition(1, 2),
            new GridPosition(2, 2)
        };
    }

    private void fillGridForBothPlayers(GridPosition[] positions) {
        for (GridPosition pos : positions) {
            assertTrue("Failed to take card for player 1 at " + pos,
                game.takeCard(1, new CardSource(Deck.I, 0), pos));
            assertTrue(game.turnFinished(1));
            assertTrue("Failed to take card for player 2 at " + pos,
                game.takeCard(2, new CardSource(Deck.I, 0), pos));
            assertTrue(game.turnFinished(2));
        }
    }

    private int countCardsOnGrid(Grid grid) {
        int count = 0;
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                if (grid.getCard(new GridPosition(x, y)).isPresent()) {
                    count++;
                }
            }
        }
        return count;
    }



}
