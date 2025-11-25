package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.grid.ActivationPattern;
import sk.uniba.fmph.dcs.terra_futura.grid.InterfaceActivateGrid;

import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;

import static org.junit.Assert.*;

class ActivateGridFake implements InterfaceActivateGrid {
    ArrayList<SimpleEntry<Integer, Integer>> activations;

    public ActivateGridFake() {
        this.activations = new ArrayList<>();
    }

    @Override
    public void setActivationPattern(Collection<SimpleEntry<Integer, Integer>> pattern) {
        activations = new ArrayList<>(pattern);
    }
}

public class ActivationPatternTest {

    private ActivateGridFake grid;
    private ActivationPattern activationPattern;

    @Before
    public void setUp() {
        grid = new ActivateGridFake();
        ArrayList<SimpleEntry<Integer, Integer>> patternEntries = new ArrayList<>();
        patternEntries.add(new SimpleEntry<>(0, 0));
        patternEntries.add(new SimpleEntry<>(0, 0));
        patternEntries.add(new SimpleEntry<>(-1, 1));
        activationPattern = new ActivationPattern(grid, patternEntries);
    }

    private void checkStateString(boolean expectedActivated) {
        System.out.println("Nas JSON:\n");
        System.out.println(activationPattern.state());
        System.out.println("Nas JSON:\n");
        JSONObject obj = new JSONObject(activationPattern.state());
        JSONArray arr = obj.getJSONArray("activations");
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject pair = arr.getJSONObject(i);
            s.append(String.format("(%s,%s)", pair.getInt("x"), pair.getInt("y")));
        }

        assertEquals("(0,0)(0,0)(-1,1)", s.toString());
        assertEquals(expectedActivated, obj.getBoolean("selected"));
    }

    @Test
    public void testDataForwarding() {
        checkStateString(false);
        activationPattern.select();
        checkStateString(true);
        assertEquals(3, grid.activations.size());
        assertEquals(Integer.valueOf(0), grid.activations.get(0).getKey());
        assertEquals(Integer.valueOf(0), grid.activations.get(0).getValue());
        assertEquals(Integer.valueOf(0), grid.activations.get(1).getKey());
        assertEquals(Integer.valueOf(0), grid.activations.get(1).getValue());
        assertEquals(Integer.valueOf(-1), grid.activations.get(2).getKey());
        assertEquals(Integer.valueOf(1), grid.activations.get(2).getValue());
    }

    @Test
    public void testPatternCannotBeActivatedTwice() {
        activationPattern.select();
        // assertThrows(activationPattern.select());
    }
}
