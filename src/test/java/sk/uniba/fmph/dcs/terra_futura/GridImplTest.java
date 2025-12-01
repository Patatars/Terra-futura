package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.effect.ArbitraryBasic;
import sk.uniba.fmph.dcs.terra_futura.effect.Effect;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.grid.GridImpl;
import sk.uniba.fmph.dcs.terra_futura.grid.GridPosition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class GridImplTest {

    private GridImpl grid;
    private Card sampleCard;

    @Before
    public void setUp() {
        grid = new GridImpl();
        // Create an upper effect: produce GREEN
        Effect upper = new ArbitraryBasic(Arrays.asList(Resource.GREEN));
        // No lower effect
        Effect lower = null;
        // Initial resources: empty
        List<Resource> initialResources = Collections.emptyList();
        // Pollution space: 1 (standard for most cards)
        int pollutionSpace = 1;

        sampleCard = new CardImpl(initialResources, pollutionSpace, upper, lower);
    }


    //Card Placement Tests

    @Test
    public void putCard_validPosition_shouldStoreCard() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, sampleCard);
        assertTrue(grid.getCard(pos).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void putCard_occupiedPosition_shouldThrowException() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, sampleCard);
        grid.putCard(pos, sampleCard); // second placement → should fail
    }

    @Test
    public void canPutCard_positionOutsideBounds_shouldReturnFalse() {
        assertFalse(grid.canPutCard(new GridPosition(3, 0)));
        assertFalse(grid.canPutCard(new GridPosition(-3, 0)));
        assertFalse(grid.canPutCard(new GridPosition(0, 3)));
        assertFalse(grid.canPutCard(new GridPosition(0, -3)));
    }

    @Test
    public void canPutCard_validEmptyPosition_shouldReturnTrue() {
        assertTrue(grid.canPutCard(new GridPosition(0, 0)));
    }

    @Test
    public void canBeActivated_emptyPosition_shouldReturnFalse() {
        assertFalse(grid.canBeActivated(new GridPosition(0, 0)));
    }

    @Test
    public void canBeActivated_cardPresentButNotInPattern_shouldReturnFalse() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, sampleCard);
        assertFalse(grid.canBeActivated(pos));
    }

    @Test
    public void canBeActivated_cardInActivationPattern_shouldReturnTrue() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, sampleCard);
        grid.setActivationPattern(Collections.singletonList(pos));
        assertTrue(grid.canBeActivated(pos));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setActivated_invalidPosition_shouldThrowException() {
        grid.setActivated(new GridPosition(0, 0)); // no card → should fail
    }

    @Test
    public void setActivated_validCardInPattern_shouldMarkAsActivated() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, sampleCard);
        grid.setActivationPattern(Collections.singletonList(pos));
        grid.setActivated(pos);
        // Verify via state string (activated position is recorded)
        assertTrue(grid.state().contains("activatedThisTurn=[" + pos + "]"));
    }

    //Turn Management

    @Test
    public void endTurn_shouldClearActivationState() {
        GridPosition pos = new GridPosition(0, 0);
        grid.putCard(pos, sampleCard);
        grid.setActivationPattern(Collections.singletonList(pos));
        grid.setActivated(pos);

        grid.endTurn();

        // After endTurn, activation pattern and activated set are empty
        assertFalse(grid.canBeActivated(pos));
        assertTrue(grid.state().contains("activatedThisTurn=[]"));
        assertTrue(grid.state().contains("activationPattern=[]"));
    }

    //State Representation

    @Test
    public void state_shouldIncludePlacedCardsAndTheirState() {
        GridPosition pos = new GridPosition(-1, 1);
        grid.putCard(pos, sampleCard);
        String currentState = grid.state();
        assertTrue(currentState.contains(pos.toString()));
        assertTrue(currentState.contains("GREEN"));
    }
    //InterfaceActivateGrid Compatibility

    @Test
    public void setActivationPattern_fromIntegerPairs_shouldConvertCorrectly() {
        java.util.AbstractMap.SimpleEntry<Integer, Integer> coord =
                new java.util.AbstractMap.SimpleEntry<>(1, -1);
        grid.setActivationPattern(Collections.singletonList(coord));

        GridPosition expected = new GridPosition(1, -1);
        assertTrue(grid.state().contains("activationPattern=[" + expected + "]"));
    }
}