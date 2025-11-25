package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;

/**
 * Represents a card effect that can be checked for applicability
 * given input resources, desired output, and available pollution.
 */
public interface Effect {

    /**
     * Checks whether this effect can produce the given output resources
     * from the provided input resources using the available pollution.
     *
     * @param input     the list of input resources.
     * @param output    the desired list of output resources.
     * @param pollution the amount of pollution available for use.
     * @return true if the effect can be applied, false otherwise.
     */
    boolean check(List<Resource> input, List<Resource> output, int pollution);

    /**
     * Indicates whether this effect provides an Assistance reward.
     */
    boolean hasAssistance();

    /**
     * Returns a string representation of the effect's current state.
     *
     * @return a descriptive string of the effect.
     */
    String state();
}
