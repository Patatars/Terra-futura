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

public boolean canPutResources(final List<Resource> resources) {
    if (countOfResource(this.resources, Resource.POLLUTION) >= this.pollutionSpaceL) {
        return false;
    }
    return true;
}

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

public void putResources(final List<Resource> resources) {
    if (!canPutResources(resources)) {
        throw new IllegalArgumentException("Cannot put resources on this card.");
    }
    this.resources.addAll(resources);
}

    @Override
    public boolean check(final List<Resource> input, final List<Resource> output, final int pollution) {
    if (upperEffect == null) {
        return false;
    }
    int pollutionCount = countOfResource(this.resources, Resource.POLLUTION);
    int availablePollution = pollutionSpaceL - pollutionCount;

    return upperEffect.check(input, output, availablePollution);
    }

    @Override
    public boolean checkLower(final List<Resource> input, final List<Resource> output,
                              final int pollution) {
    if (lowerEffect == null) {
        return false;
    }
        int pollutionCount = countOfResource(this.resources, Resource.POLLUTION);
        int availablePollution = pollutionSpaceL - pollutionCount;

    return lowerEffect.check(input, output, availablePollution);
    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        String upperState = (upperEffect == null) ? "null" : upperEffect.state();
        String lowerState = (lowerEffect == null) ? "null" : lowerEffect.state();

        return String.format("CardImpl{upperEffect=%s, lowerEffect=%s, resources=%s, pollutionSpaceL=%d}",
                upperState, lowerState, resources.toString(), pollutionSpaceL);
    }

}
