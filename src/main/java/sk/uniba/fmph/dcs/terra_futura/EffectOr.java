package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Композитний ефект, який успішно виконується, якщо хоча б один з його під-ефектів успішно виконується.
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
        // Перевіряємо, чи хоча б один ефект може бути застосований
        return effects.stream().anyMatch(effect -> effect.check(input, output, pollution));
    }

    @Override
    public boolean hasAssistance() {
        // Якщо хоча б один ефект має Assistance, то і цей ефект має Assistance
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
