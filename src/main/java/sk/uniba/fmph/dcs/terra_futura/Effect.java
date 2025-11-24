package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;

/**
 * Інтерфейс для ефектів карток.
 * Використовується для перевірки можливості отримання ресурсів з заданим забрудненням.
 */
public interface Effect {

    /**
     * Перевіряє, чи можна досягти вихідних ресурсів з вхідних ресурсів та заданого рівня забруднення.
     *
     * @param input   Список вхідних ресурсів.
     * @param output  Список вихідних ресурсів, які потрібно отримати.
     * @param pollution Рівень забруднення, доступний для використання.
     * @return true, якщо ефект може бути застосований; false — ні.
     */
    boolean check(List<Resource> input, List<Resource> output, int pollution);

    /**
     * Перевіряє, чи цей ефект має функцію "Assistance" (допомога).
     *
     * @return true, якщо ефект має Assistance; false — ні.
     */
    boolean hasAssistance();

    /**
     * Повертає стан ефекту у вигляді рядка (для логування/відображення).
     *
     * @return Рядок, що описує стан ефекту.
     */
    String state();
}
