package sk.uniba.fmph.dcs.terra_futura.grid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.uniba.fmph.dcs.terra_futura.card.Card;
import sk.uniba.fmph.dcs.terra_futura.card.CardImpl;
import sk.uniba.fmph.dcs.terra_futura.effect.Effect;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GridImplTest {

    private GridImpl grid;
    private CardImpl cardA;
    private CardImpl cardB;
    private GridPosition pos1;
    private GridPosition pos2;

    @BeforeEach
    void setUp() {
        grid = new GridImpl();
        List<Resource> res = List.of(Resource.MONEY, Resource.GREEN);
        Effect effect = new Effect() {
            @Override
            public boolean check(List<Resource> input, List<Resource> output, int pollution) {
                return false;
            }

            @Override
            public boolean hasAssistance() {
                return false;
            }

            @Override
            public String state() {
                return "";
            }
        };
        cardA = new CardImpl(res, 1, effect, effect );
        cardB = new CardImpl(res, 1, effect, effect);
        pos1 = new GridPosition(1, 1);
        pos2 = new GridPosition(2, 2);
    }

    // GetCard
    @Test
    void getCard_emptyGrid_returnsEmpty() {
        assertTrue(grid.getCard(pos1).isEmpty());
    }

    @Test
    void getCard_afterPut_returnsCard() {
        grid.putCard(pos1, cardA);
        Optional<Card> found = grid.getCard(pos1);

        assertTrue(found.isPresent());
        assertSame(cardA, found.get());
    }

    // PutCard
    @Test
    void canPutCard_emptyCell_returnsTrue() {
        assertTrue(grid.canPutCard(pos1));
    }

    @Test
    void canPutCard_occupiedCell_returnsFalse() {
        grid.putCard(pos1, cardA);
        assertFalse(grid.canPutCard(pos1));
    }

    // ---------- putCard ----------
    @Test
    void putCard_inEmptyCell_succeeds() {
        grid.putCard(pos1, cardA);
        assertSame(cardA, grid.getCard(pos1).get());
    }

    @Test
    void putCard_inOccupiedCell_throwsException() {
        grid.putCard(pos1, cardA);
        assertThrows(IllegalStateException.class, () -> grid.putCard(pos1, cardB));
    }

    // ---------- setActivationPattern (List version) ----------
    @Test
    void setActivationPattern_allowsActivationOfListedPositions() {
        grid.putCard(pos1, cardA);
        grid.setActivationPattern(List.of(pos1));

        assertTrue(grid.canBeActivated(pos1));
    }

    @Test
    void canBeActivated_returnsFalse_whenNoCard() {
        grid.setActivationPattern(List.of(pos1));
        assertFalse(grid.canBeActivated(pos1));
    }

    @Test
    void canBeActivated_returnsFalse_whenNotInPattern() {
        grid.putCard(pos1, cardA);
        grid.setActivationPattern(List.of(pos2));
        assertFalse(grid.canBeActivated(pos1));
    }

    // ---------- setActivated ----------
    @Test
    void setActivated_onValidPosition_succeeds() {
        grid.putCard(pos1, cardA);
        grid.setActivationPattern(List.of(pos1));

        assertDoesNotThrow(() -> grid.setActivated(pos1));
    }

    @Test
    void setActivated_onInvalidPosition_throwsException() {
        grid.putCard(pos1, cardA);
        grid.setActivationPattern(List.of(pos2));

        assertThrows(IllegalStateException.class, () -> grid.setActivated(pos1));
    }

    // ---------- setActivationPattern(Collection<SimpleEntry<Integer,Integer>>) ----------
    @Test
    void setActivationPattern_integerCoordinates_convertedToPositions() {
        grid.putCard(pos1, cardA);

        Collection<AbstractMap.SimpleEntry<Integer, Integer>> raw =
                List.of(new AbstractMap.SimpleEntry<>(1, 1));

        grid.setActivationPattern(raw);

        assertTrue(grid.canBeActivated(pos1));
    }

    // ---------- endTurn ----------
    @Test
    void endTurn_clearsActivationData() {
        grid.putCard(pos1, cardA);
        grid.setActivationPattern(List.of(pos1));
        grid.setActivated(pos1);

        grid.endTurn();

        assertFalse(grid.canBeActivated(pos1));
    }

    // ---------- state ----------
    @Test
    void state_containsCardStates() {
        grid.putCard(pos1, cardA);
        String s = grid.state();

        assertTrue(s.contains("Grid{"));
        assertTrue(s.contains(pos1.toString()));
        assertTrue(s.contains(cardA.state()));
    }
}
