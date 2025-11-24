package sk.uniba.fmph.dcs.terra_futura;
import static org.junit.Assert.*;

import org.junit.Test;
import java.util.Map;
import java.util.HashMap;

// Simple mock observer for test purposes
class MockObserver implements ObserverInterface {
    String lastReceived = null;

    @Override
    public void notify(String state) {
        lastReceived = state;
    }
}

public class GameObserverTest {

    /**
     * Test 1: Each player receives the correct personal game state.
     */
    @Test
    public void testNotifyAll_SendsCorrectStateToEachPlayer() {
        GameObserver gameObserver = new GameObserver();

        MockObserver p1 = new MockObserver();
        MockObserver p2 = new MockObserver();

        gameObserver.addObserver(1, p1);
        gameObserver.addObserver(2, p2);

        Map<Integer, String> newState = new HashMap<>();
        newState.put(1, "Player1 sees farm and forest");
        newState.put(2, "Player2 sees mine and village");

        gameObserver.notifyAll(newState);

        assertEquals("Player1 sees farm and forest", p1.lastReceived);
        assertEquals("Player2 sees mine and village", p2.lastReceived);
    }

    /**
     * Test 2: Removing an observer prevents them from receiving updates.
     */
    @Test
    public void testRemoveObserver_PreventsReceivingNotifications() {
        GameObserver gameObserver = new GameObserver();

        MockObserver p1 = new MockObserver();
        gameObserver.addObserver(1, p1);

        gameObserver.removeObserver(1);

        Map<Integer, String> newState = new HashMap<>();
        newState.put(1, "Should not be seen");

        gameObserver.notifyAll(newState);

        assertNull(p1.lastReceived);
    }

    /**
     * Test 3: Observers without a provided new state do NOT get notified.
     * (e.g., a player not affected this turn)
     */
    @Test
    public void testNotifyAll_ObserverWithoutStateDoesNotGetNotified() {
        GameObserver gameObserver = new GameObserver();

        MockObserver p1 = new MockObserver();
        MockObserver p2 = new MockObserver();

        gameObserver.addObserver(1, p1);
        gameObserver.addObserver(2, p2);

        Map<Integer, String> newState = new HashMap<>();
        newState.put(1, "Only player 1 gets an update");

        gameObserver.notifyAll(newState);

        assertEquals("Only player 1 gets an update", p1.lastReceived);
        assertNull(p2.lastReceived);
    }

    /**
     * Test 4: Updating an observer (same playerId, new instance)
     * correctly replaces the old observer.
     */
    @Test
    public void testAddObserver_ReplacesExistingObserver() {
        GameObserver gameObserver = new GameObserver();

        MockObserver oldObserver = new MockObserver();
        MockObserver newObserver = new MockObserver();

        gameObserver.addObserver(1, oldObserver);
        gameObserver.addObserver(1, newObserver); // replaces old one

        Map<Integer, String> newState = new HashMap<>();
        newState.put(1, "Updated player view");

        gameObserver.notifyAll(newState);

        assertNull(oldObserver.lastReceived);
        assertEquals("Updated player view", newObserver.lastReceived);
    }

    /**
     * Test 5: Multiple notifications (simulating multiple game turns)
     * update observers repeatedly with new turn states.
     */
    @Test
    public void testNotifyAll_MultipleGameTurns() {
        GameObserver gameObserver = new GameObserver();

        MockObserver p1 = new MockObserver();
        gameObserver.addObserver(1, p1);

        Map<Integer, String> turn1 = new HashMap<>();
        turn1.put(1, "Turn 1: produced wood");

        gameObserver.notifyAll(turn1);
        assertEquals("Turn 1: produced wood", p1.lastReceived);

        Map<Integer, String> turn2 = new HashMap<>();
        turn2.put(1, "Turn 2: produced stone");

        gameObserver.notifyAll(turn2);
        assertEquals("Turn 2: produced stone", p1.lastReceived);
    }
}

