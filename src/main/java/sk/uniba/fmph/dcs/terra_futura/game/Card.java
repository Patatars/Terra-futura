package sk.uniba.fmph.dcs.terra_futura.game;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import java.util.List;

public interface Card {
    boolean canGetResources(List<Resource> resources);

    boolean getResources(List<Resource> resources);

    boolean canPutResources(List<Resource> resources);

    void putResources(List<Resource> resources);

    boolean check(List<Resource> input, List<Resource> output, int pollution);

    boolean checkLower(List<Resource> input, List<Resource> output, int pollution);

    boolean hasAssistance();

    String state();
}
