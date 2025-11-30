package sk.uniba.fmph.dcs.terra_futura.enums;

import sk.uniba.fmph.dcs.terra_futura.score.Points;

public enum Resource {
    GREEN(new Points(1)),
    RED(new Points(1)),
    YELLOW(new Points(1)),
    BULB(new Points(5)),
    GEAR(new Points(5)),
    CAR(new Points(6)),
    MONEY(new Points(0)),
    POLLUTION(new Points(-1));

    public Points getPoints() {
        return points;
    }

    private final Points points;

    Resource(final Points points) {
        this.points = points;
    }
}
