package sk.uniba.fmph.dcs.terra_futura.card;


import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;

public class CardImpl {
    List<Resource> resources;
    int pollutionSpaces;

    public CardImpl(List<Resource> resources, int pollutionSpaces) {
        this.resources = resources;
        this.pollutionSpaces = pollutionSpaces;
    }
    public String state(){
        return "CardImpl{cards=["+resources+"]}";
    }
}
