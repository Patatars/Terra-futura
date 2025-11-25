package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of an effect that produces base resources without any input cost
 * and without consuming pollution.
 * This represents the "arbitrary" basic activation of a card.
 */
public final class ArbitraryBasic implements Effect {

    private final List<Resource> to;
    private final int from;
    private final int pollution;

    public ArbitraryBasic(final List<Resource> to) {
        this(0, to, 0);
    }
    /**
     * Creates a basic effect that produces the given output resources.
     *
     * @param from     number of input resources (unused, kept for diagram compatibility)
     * @param to       list of resources this effect produces
     * @param pollution amount of pollution consumed (unused, kept for diagram compatibility)
     */
    public ArbitraryBasic(final int from, final List<Resource> to, final int pollution) {
        this.from = from;
        this.to = Objects.requireNonNull(to, "Output resources cannot be null");
        this.pollution = pollution;
    }

    @Override
    public boolean check(final List<Resource> input, final List<Resource> output, final int availablePollution) {
        if (!input.isEmpty()) {
            return false;
        }
        return output.equals(to);
    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        return String.format("ArbitraryBasic{from=%d, to=%s, polution=%d}", from, to, pollution);
    }
}
