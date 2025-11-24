package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;
import java.util.Objects;

/**
 * Реалізація ефекту, що дає базову продукцію без вхідних витрат і без використання забруднення.
 * Це "довільна" базова активність картки.
 * Наприклад: [] -> [Green, Green] з витратою 0 забруднення.
 */
public final class ArbitraryBasic implements Effect {

    private final List<Resource> to;

    /**
     * Створює базовий ефект, який продукує задані ресурси.
     *
     * @param to Список ресурсів, які ефект продукує.
     */
    public ArbitraryBasic(final List<Resource> to) {
        this.to = Objects.requireNonNull(to, "Output resources cannot be null");
    }

    @Override
    public boolean check(final List<Resource> input, final List<Resource> output,final int availablePollution) {
        // 1. Цей ефект не вимагає вхідних ресурсів.
        // Тому input має бути порожнім (або ми просто ігноруємо його).
        // Для консистентності перевіримо, що очікувані вхідні ресурси - порожні.
        if (!input.isEmpty()) {
            return false;
        }

        // 2. Ефект не використовує забруднення, тому перевірка на pollution не потрібна.
        // availablePollution може бути будь-яким (навіть 0).

        // 3. Перевіряємо, чи вихідні ресурси точно відповідають очікуваним.
        return output.equals(to);
    }

    @Override
    public boolean hasAssistance() {
        // За замовчуванням, базовий ефект не має Assistance.
        return false;
    }

    @Override
    public String state() {
        return String.format("ArbitraryBasic{to=%s}", to);
    }
}
