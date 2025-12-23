package sk.uniba.fmph.dcs.terra_futura.actions;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import java.util.List;

/**
 * Interface for objects that can receive reward resources.
 */
public interface RewardTarget {
    /**
     * Checks if cat put resources.
     *
     * @param resources the list of resources to check
     * @return true if the resources can be placed
     */
    boolean canPutResources(List<Resource> resources);

    /**
     * Places resources on target.
     *
     * @param resources the list of resources to place
     */
    void putResources(List<Resource> resources);
}

