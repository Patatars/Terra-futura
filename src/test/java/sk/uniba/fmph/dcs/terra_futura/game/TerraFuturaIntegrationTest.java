package sk.uniba.fmph.dcs.terra_futura.game;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.card.CardSource;
import sk.uniba.fmph.dcs.terra_futura.deck.Pile;
import sk.uniba.fmph.dcs.terra_futura.deck.PileImpl;
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

    private Card createRealCard() {
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
        initialResources.add(Resource.GREEN);
        return new CardImpl(initialResources, 3, upperEffect, lowerEffect);
    }

    @Before
    public void setUp() {
        grid1 = new GridImpl();
        grid2 = new GridImpl();

        Map<Integer, Grid> grids = new HashMap<>();
        grids.put(1, grid1);
        grids.put(2, grid2);

        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            cards.add(createRealCard());
        }

        Pile pile = new PileImpl(cards);
        game = new Game(new int[]{1, 2}, grids, pile, 1);
    }


    @Test
    public void testPlayer1TakesCardSuccessfully() {
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(grid1.getCard(new GridPosition(0, 0)).isPresent());
    }

    @Test
    public void testWrongPlayerCannotTakeCard() {
        assertFalse(game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
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

        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(2);

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
            assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), pos));
            assertTrue(game.turnFinished(1));
            assertTrue(game.takeCard(2, new CardSource(Deck.I, 0), pos));
            assertTrue(game.turnFinished(2));
        }

        for (GridPosition pos : positions) {
            assertTrue(grid1.getCard(pos).isPresent());
            assertTrue(grid2.getCard(pos).isPresent());
        }
    }


    @Test
    public void testFullGameToSelectActivationPattern() {
        GridPosition[] positions = getFullGridPositions();
        fillGridForBothPlayers(positions);
        assertTrue(game.selectActivationPattern(1, 0));
    }

    @Test
    public void testSelectActivationPatternWrongPlayer() {
        GridPosition[] positions = getFullGridPositions();
        fillGridForBothPlayers(positions);

        assertFalse(game.selectActivationPattern(2, 0));
    }

    @Test
    public void testCardActuallyPlacedOnCorrectGrid() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 1));
        assertTrue(grid1.getCard(new GridPosition(1, 1)).isPresent());
        assertFalse(grid2.getCard(new GridPosition(1, 1)).isPresent());
    }

    @Test
    public void testEachPlayerHasOwnGrid() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        game.turnFinished(1);
        game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0));
        assertTrue(grid1.getCard(new GridPosition(0, 0)).isPresent());
        assertTrue(grid2.getCard(new GridPosition(0, 0)).isPresent());
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
            assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), pos));
            assertTrue(game.turnFinished(1));
            assertTrue(game.takeCard(2, new CardSource(Deck.I, 0), pos));
            assertTrue(game.turnFinished(2));
        }

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                GridPosition pos = new GridPosition(x, y);
                assertTrue(grid1.getCard(pos).isPresent());
                assertTrue(grid2.getCard(pos).isPresent());
            }
        }

        assertTrue(game.selectActivationPattern(1, 0));
    }


    @Test
    public void testDiscardAndTakeCardScenario() {
        assertTrue(game.discardLastCardInDeck(1, Deck.I));
        assertFalse(game.discardLastCardInDeck(1, Deck.I));
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 1)));
        assertTrue(grid1.getCard(new GridPosition(1, 1)).isPresent());
        assertTrue(game.turnFinished(1));

        assertTrue(game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(1, 1)));
    }


    @Test
    public void testCardResourcesIntegration() {
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));

        Optional<Card> optionalCard = grid1.getCard(new GridPosition(0, 0));
        assertTrue(optionalCard.isPresent());
        Card placedCard = optionalCard.get();
        assertTrue(placedCard.canGetResources(List.of(Resource.GREEN)));
    }

    @Test
    public void testResourceManipulationOnPlacedCard() {
        game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0));

        Card card = grid1.getCard(new GridPosition(0, 0)).get();
        card.getResources(List.of(Resource.GREEN));
        assertFalse(card.canGetResources(List.of(Resource.GREEN)));
        card.putResources(List.of(Resource.RED));
        assertTrue(card.canGetResources(List.of(Resource.RED)));
    }


    @Test
    public void testGridAdjacencyRule() {
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
        for (int i = 0; i < 50; i++) {
            cards.add(createRealCard());
        }

        TerraFuturaInterface threePlayerGame = new Game(new int[]{1, 2, 3}, threePlayerGrids, new PileImpl(cards), 1);

        assertTrue(threePlayerGame.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(threePlayerGame.turnFinished(1));

        assertTrue(threePlayerGame.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(threePlayerGame.turnFinished(2));

        assertTrue(threePlayerGame.takeCard(3, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(threePlayerGame.turnFinished(3));

        assertTrue(threePlayerGame.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(1, 0)));
    }

    @Test
    public void testDiscardMultipleTurns() {
        assertTrue(game.discardLastCardInDeck(1, Deck.I));
        assertTrue(game.takeCard(1, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(game.turnFinished(1));

        assertTrue(game.takeCard(2, new CardSource(Deck.I, 0), new GridPosition(0, 0)));
        assertTrue(game.turnFinished(2));

        assertTrue(game.discardLastCardInDeck(1, Deck.I));
    }

    @Test
    public void testFullGameToScoringPhase() {
        GridPosition[] positions = getFullGridPositions();
        fillGridForBothPlayers(positions);

        assertTrue(game.selectActivationPattern(1, 0));
        assertTrue(game.turnFinished(1));
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
}
