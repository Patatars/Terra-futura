package sk.uniba.fmph.dcs.terra_futura.card;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import java.util.List;

public interface Card {
    /**
     * Checks if the specified resources can be obtained from this card.
     *
     * @param resources the list of resources to check
     * @return true if the resources can be obtained, false otherwise
     * @implNote This method does not modify the card's state.
     */
    boolean canGetResources(List<Resource> resources);

    /**
     * Attempts to obtain the specified resources from this card.
     *
     * @param resources the list of resources to obtain
     * @return true if the resources were successfully obtained, false otherwise
     * @implNote Implementations may remove the resources from the card if successful.
     */
    void getResources(List<Resource> resources);

    /**
     * Checks if the specified resources can be placed on this card.
     *
     * @param resources the list of resources to check
     * @return true if the resources can be placed, false otherwise
     * @implNote This method does not modify the card's state.
     */
    boolean canPutResources(List<Resource> resources);

    /**
     * Places the specified resources on this card.
     *
     * @param resources the list of resources to place
     * @implNote Implementations should update the card's state to reflect the added resources.
     */
    void putResources(List<Resource> resources);

    /**
     * Checks if the card can process the given input and output resources with the specified pollution value.
     *
     * @param input the list of input resources
     * @param output the list of output resources
     * @param pollution the pollution value to check
     * @return true if the card can process the resources and pollution, false otherwise
     */
    boolean check(List<Resource> input, List<Resource> output, int pollution);

    /**
     * Checks if the card can process the given input and output resources with a pollution value lower than specified.
     *
     * @param input the list of input resources
     * @param output the list of output resources
     * @param pollution the pollution threshold to check against
     * @return true if the card can process the resources with lower pollution, false otherwise
     */
    boolean checkLower(List<Resource> input, List<Resource> output, int pollution);

    /**
     * Checks if this card provides assistance.
     *
     * @return true if the card has assistance, false otherwise
     */
    boolean hasAssistance();

    /**
     * Returns a string representation of the card's current state.
     *
     * @return the state of the card as a string
     */
    String state();
}
