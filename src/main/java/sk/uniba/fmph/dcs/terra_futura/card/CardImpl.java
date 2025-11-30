package sk.uniba.fmph.dcs.terra_futura.card;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.effect.Effect;

import java.util.ArrayList;
import java.util.List;

public class CardImpl implements Card {

private final List<Resource> resources;
private final int pollutionSpaceL;
private final Effect upperEffect;
private final Effect lowerEffect;

public CardImpl(final ArrayList<Resource> resources, final int pollutionSpaceL,
                final Effect upperEffect, final Effect lowerEffect) {
        this.upperEffect = upperEffect;
        this.lowerEffect = lowerEffect;
        this.resources = resources;
        this.pollutionSpaceL = pollutionSpaceL;
    }
private int countOfResource(final List<Resource>  resources, final Resource resourceType) {
    int count = 0;
    for (Resource res : resources) {
        if (res == resourceType) {
            count++;
        }
    }
    return count;
}

private List<Resource> kindsOfResources(final List<Resource> resources) {
    ArrayList<Resource> kinds = new ArrayList<>();
    for (Resource res : resources) {
        if (!kinds.contains(res)) {
            kinds.add(res);
        }
    }
    return kinds;
}

    /**
     * Checks if the specified resources can be removed from the card.
     * <p>
     * Special rules:
     * 1. If the request is to remove POLLUTION (clean up), it is allowed even if the card is full/blocked.
     * 2. If the card is full of pollution (>= pollutionSpaceL), no other resources can be taken.
     * 3. Otherwise, the card must contain sufficient quantity of the requested resources.
     *
     * @param resources The list of resources the player wants to take.
     * @return true if the operation is allowed, false otherwise.
     */

public boolean canGetResources(final List<Resource> resources) {
    if (resources.isEmpty()) {
        return false;
    }
    if (kindsOfResources(resources).size() == 1 && resources.contains(Resource.POLLUTION) && countOfResource(this.resources, Resource.POLLUTION) >= resources.size()) {
        return true;
    }
    if (countOfResource(this.resources, Resource.POLLUTION) >= pollutionSpaceL) {
        return false;
    }
    for (Resource res : kindsOfResources(resources)) {
        if (countOfResource(this.resources, res) < countOfResource(resources, res)) {
            return false;
        }
    }
    return true;
}

    /**
     * Checks if resources can be placed onto the card.
     * The card accepts resources only if it is not currently blocked by pollution.
     *
     * @param resources The list of resources to add (content is not checked, only card state).
     * @return true if the card is not full of pollution, false otherwise.
     */

public boolean canPutResources(final List<Resource> resources) {
    if (countOfResource(this.resources, Resource.POLLUTION) >= this.pollutionSpaceL) {
        return false;
    }
    return true;
}

    /**
     * Removes the specified resources from the card.
     *
     * @param resources The list of resources to remove.
     * @throws IllegalArgumentException if the resources cannot be taken (e.g., card is blocked or resources are missing).
     */

public void getResources(final List<Resource> resources) {
    if (!canPutResources(resources)) {
        throw new IllegalArgumentException("Cannot get resources from this card.");
    }
    ArrayList<Resource> willBeRemoved = new ArrayList<>();
    for (Resource res : this.resources) {
        if (resources.contains(res) && countOfResource(willBeRemoved, res) < countOfResource(resources, res)) {
            willBeRemoved.add(this.resources.remove(this.resources.indexOf(res)));
        }
    }
}

    /**
     * Adds the specified resources to the card.
     *
     * @param resources The list of resources to add.
     * @throws IllegalArgumentException if the card is blocked by pollution.
     */

public void putResources(final List<Resource> resources) {
    if (!canPutResources(resources)) {
        throw new IllegalArgumentException("Cannot put resources on this card.");
    }
    this.resources.addAll(resources);
}

    /**
     * Verifies if the Upper Effect of the card can be activated.
     * Calculates the available space for new pollution and delegates the check to the effect implementation.
     *
     * @param input     The input resources required for the action.
     * @param output    The output resources produced by the action.
     * @param pollution The pollution context (not used directly, calculated from internal state).
     * @return true if the upper effect exists and the action is valid given the card's current state.
     */

    @Override
    public boolean check(final List<Resource> input, final List<Resource> output, final int pollution) {
    if (upperEffect == null) {
        return false;
    }
    int pollutionCount = countOfResource(this.resources, Resource.POLLUTION);
    int availablePollution = pollutionSpaceL - pollutionCount;

    return upperEffect.check(input, output, availablePollution);
    }

    /**
     * Verifies if the Lower Effect of the card can be activated.
     * Calculates the available space for new pollution and delegates the check to the effect implementation.
     *
     * @param input     The input resources required for the action.
     * @param output    The output resources produced by the action.
     * @param pollution The pollution context (not used directly, calculated from internal state).
     * @return true if the lower effect exists and the action is valid given the card's current state.
     */

    @Override
    public boolean checkLower(final List<Resource> input, final List<Resource> output, final int pollution) {
    if (lowerEffect == null) {
        return false;
    }
        int pollutionCount = countOfResource(this.resources, Resource.POLLUTION);
        int availablePollution = pollutionSpaceL - pollutionCount;

    return lowerEffect.check(input, output, availablePollution);
    }

    /**
     * Always returns false because of simplified rules.
     *
     * @return false anyway.
     */

    @Override
    public boolean hasAssistance() {
        return false;
    }

    /**
     * Returns a string representation of the card's current state, including its effects,
     * held resources, and pollution limit.
     *
     * @return The state string.
     */

    @Override
    public String state() {
        String upperState = (upperEffect == null) ? "null" : upperEffect.state();
        String lowerState = (lowerEffect == null) ? "null" : lowerEffect.state();

        return String.format("CardImpl{upperEffect=%s, lowerEffect=%s, resources=%s, pollutionSpaceL=%d}",
                upperState, lowerState, resources.toString(), pollutionSpaceL);
    }

}
