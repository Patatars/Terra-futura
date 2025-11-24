package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;
import java.util.Objects;

/**
 * Реалізація ефекту, що виконує фіксоване перетворення ресурсів.
 * Наприклад: [Green, Red] -> [Bulb, Car] з витратою 1 забруднення.
 */
public final class TransformationFixed implements Effect {

    private final List<Resource> from;
    private final List<Resource> to;
    private final int pollution;

    public TransformationFixed(List<Resource> from, List<Resource> to, final int pollution) {
        this.from = Objects.requireNonNull(from, "Input resources cannot be null");
        this.to = Objects.requireNonNull(to, "Output resources cannot be null");
        this.pollution = pollution;
    }

    @Override
    public boolean check(final List<Resource> input, final List<Resource> output, int availablePollution) {
        // Перевіряємо, чи доступне забруднення достатнє
        if (availablePollution < pollution) {
            return false;
        }

        // Перевіряємо, чи вхідні ресурси точно відповідають очікуваним
        if (!input.equals(from)) {
            return false;
        }

        // Перевіряємо, чи вихідні ресурси точно відповідають очікуваним
        return output.equals(to);
    }

    @Override
    public boolean hasAssistance() {
        // За замовчуванням, фіксоване перетворення не має Assistance.
        // Можна додати поле `hasAssistance` у конструктор, якщо потрібно.
        return false;
    }

    @Override
    public String state() {
        return String.format("TransformationFixed{from=%s, to=%s, pollution=%d}", from, to, pollution);
    }
}
