package sk.uniba.fmph.dcs.terra_futura.actions;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.game.GridPosition;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.game.Card;
import sk.uniba.fmph.dcs.terra_futura.game.Grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for transferring resources between cards and handling
 * pollution.
 * This service encapsulates the core transaction logic used by various actions.
 */
public class ResourceTransferService {

    /**
     * Executes a resource transaction.
     *
     * @param grid      The game grid.
     * @param inputs    List of resources to take from specific positions.
     * @param outputs   List of resources to put to specific positions.
     * @param pollution List of positions to place pollution on.
     * @return true if the transaction was successful, false otherwise (e.g.,
     *         insufficient resources or capacity).
     */
    public boolean executeTransaction(final Grid grid, final List<Pair<Resource, GridPosition>> inputs,
            final List<Pair<Resource, GridPosition>> outputs, final List<GridPosition> pollution) {
        if (grid == null || inputs == null || outputs == null || pollution == null) {
            return false;
        }

        // 1. Validation Phase

        // Validate Inputs
        Map<GridPosition, List<Resource>> inputsByCard = new HashMap<>();
        for (Pair<Resource, GridPosition> input : inputs) {
            inputsByCard.computeIfAbsent(input.getValue(), k -> new ArrayList<>()).add(input.getKey());
        }

        for (Map.Entry<GridPosition, List<Resource>> entry : inputsByCard.entrySet()) {
            GridPosition pos = entry.getKey();
            List<Resource> requiredResources = entry.getValue();

            if (!grid.getCard(pos).isPresent()) {
                return false;
            }
            Card sourceCard = grid.getCard(pos).get();

            if (!sourceCard.canGetResources(requiredResources)) {
                return false;
            }
        }

        // Validate Outputs
        // Group outputs by card to use canPutResources(List) efficiently
        Map<GridPosition, List<Resource>> outputsByCard = new HashMap<>();
        for (Pair<Resource, GridPosition> output : outputs) {
            outputsByCard.computeIfAbsent(output.getValue(), k -> new ArrayList<>()).add(output.getKey());
        }

        for (Map.Entry<GridPosition, List<Resource>> entry : outputsByCard.entrySet()) {
            GridPosition pos = entry.getKey();
            List<Resource> resourcesToAdd = entry.getValue();

            if (!grid.getCard(pos).isPresent()) {
                return false;
            }
            Card targetCard = grid.getCard(pos).get();
            if (!targetCard.canPutResources(resourcesToAdd)) {
                return false;
            }
        }

        // Validate Pollution
        // Pollution is treated as Resource.POLLUTION and validated via canPutResources
        Map<GridPosition, List<Resource>> pollutionByCard = new HashMap<>();
        for (GridPosition pos : pollution) {
            pollutionByCard.computeIfAbsent(pos, k -> new ArrayList<>()).add(Resource.POLLUTION);
        }

        for (Map.Entry<GridPosition, List<Resource>> entry : pollutionByCard.entrySet()) {
            GridPosition pos = entry.getKey();
            List<Resource> pollutionResources = entry.getValue();

            if (!grid.getCard(pos).isPresent()) {
                return false;
            }
            Card targetCard = grid.getCard(pos).get();

            // Check combined capacity for outputs and pollution on the same card
            List<Resource> combinedResources = new ArrayList<>();
            if (outputsByCard.containsKey(pos)) {
                combinedResources.addAll(outputsByCard.get(pos));
            }
            combinedResources.addAll(pollutionResources);

            if (!targetCard.canPutResources(combinedResources)) {
                return false;
            }
        }

        // 2. Execution Phase

        // Execute Inputs
        for (Map.Entry<GridPosition, List<Resource>> entry : inputsByCard.entrySet()) {
            Card sourceCard = grid.getCard(entry.getKey()).get();
            sourceCard.getResources(entry.getValue());
        }

        // Execute Outputs
        for (Map.Entry<GridPosition, List<Resource>> entry : outputsByCard.entrySet()) {
            Card targetCard = grid.getCard(entry.getKey()).get();
            targetCard.putResources(entry.getValue());
        }

        // Execute Pollution
        for (Map.Entry<GridPosition, List<Resource>> entry : pollutionByCard.entrySet()) {
            Card targetCard = grid.getCard(entry.getKey()).get();
            targetCard.putResources(entry.getValue());
        }

        return true;
    }
}
