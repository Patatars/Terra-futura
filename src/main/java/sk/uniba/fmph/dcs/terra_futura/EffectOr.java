package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
/**
 * Composite effect that succeeds if at least one of its sub-effects succeeds.
 * Used to model cards with multiple activation options.
 */
public final class EffectOr implements Effect {

    private final List<Effect> effects;

    public EffectOr(final List<Effect> effects) {
        this.effects = Objects.requireNonNull(effects, "Effects list cannot be null");
        if (effects.isEmpty()) {
            throw new IllegalArgumentException("EffectOr must contain at least one effect");
        }
    }

    @Override
    public boolean check(final List<Resource> input, final List<Resource> output, final int pollution) {
        return effects.stream().anyMatch(effect -> effect.check(input, output, pollution));
    }

    @Override
    public boolean hasAssistance() {
        return effects.stream().anyMatch(Effect::hasAssistance);
    }

    @Override
    public String state() {
        String effectsState = effects.stream()
                .map(Effect::state)
                .collect(Collectors.joining(", "));
        return String.format("EffectOr{%s}", effectsState);
    }
}
