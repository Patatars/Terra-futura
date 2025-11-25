package sk.uniba.fmph.dcs.terra_futura.effect;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;
import java.util.Objects;

/**
 * Fixed transformation effect: exact input → exact output, optionally consuming pollution.
 */
public final class TransformationFixed implements Effect {

    private final List<Resource> from;
    private final List<Resource> to;
    private final int pollution;

    public TransformationFixed(final List<Resource> from, final List<Resource> to, final int pollution) {
        this.from = Objects.requireNonNull(from, "Input resources cannot be null");
        this.to = Objects.requireNonNull(to, "Output resources cannot be null");
        this.pollution = pollution;
    }

    @Override
    public boolean check(final List<Resource> input, final List<Resource> output, final int availablePollution) {

        if (availablePollution < pollution) {
            return false;
        }


        if (!input.equals(from)) {
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
        return String.format("TransformationFixed{from=%s, to=%s, pollution=%d}", from, to, pollution);
    }
}
