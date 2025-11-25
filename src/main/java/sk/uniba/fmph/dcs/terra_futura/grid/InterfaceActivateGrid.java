package sk.uniba.fmph.dcs.terra_futura.grid;

import java.util.Collection;
import java.util.AbstractMap.SimpleEntry;
public interface InterfaceActivateGrid {
    void setActivationPattern(Collection<SimpleEntry<Integer, Integer>> pattern);
}
