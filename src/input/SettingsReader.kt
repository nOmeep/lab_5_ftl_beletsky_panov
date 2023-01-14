package input

import java.io.File
import java.io.FileNotFoundException

/**
 * Класс для чтения пользвовательских параметров из файла syntax.txt
 */
class SettingsReader {
    private val nameToValueMap = mutableMapOf(
        "NStart" to DEFAULT_NON_TERMINAL_START,
        "NEnd" to DEFAULT_NON_TERMINAL_END,
        "Arrow" to DEFAULT_ARROW,
        "Epsilon" to DEFAULT_EPSILON,
        "IterStart" to DEFAULT_ITER_START,
        "IterEnd" to DEFAULT_ITER_END,
        "OptionalEnterStart" to DEFAULT_OPTIONAL_ENTER_START,
        "OptionalEnterEnd" to DEFAULT_OPTIONAL_ENTER_END,
        "NecessarilyStart" to DEFAULT_NECESSARILY_START,
        "NecessarilyEnd" to DEFAULT_NECESSARILY_END,
        "Alternative" to DEFAULT_ALTERNATIVE
    )

    /**
     * Возвращает параметры, прочитанные из файла syntax.txt;
     * если что-то пошло не так, то отдает дефолтные параметры
     */
    fun readSyntax(): SettingsModel {
        safeReadSyntax()

        return SettingsModel(
            nonTerminalStart = nameToValueMap[NON_TERMINAL_START_KEY]!!,
            nonTerminalEnd = nameToValueMap[NON_TERMINAL_END_KEY]!!,
            arrow = nameToValueMap[ARROW_KEY]!!,
            epsilon = nameToValueMap[EPSILON_KEY]!!,
            iterStart = nameToValueMap[ITER_START_KEY]!!,
            iterEnd = nameToValueMap[ITER_END_KEY]!!,
            optionalEnterStart = nameToValueMap[OPTIONAL_ENTER_START_KEY]!!,
            optionalEnterEnd = nameToValueMap[OPTIONAL_ENTER_END_KEY]!!,
            necessarilyStart = nameToValueMap[NECESSARILY_START_KEY]!!,
            necessarilyEnd = nameToValueMap[NECESSARILY_END_KEY]!!,
            alternative = nameToValueMap[ALTERNATIVE_KEY]!!
        )
    }

    private fun safeReadSyntax() {
        try {
            File(DEFAULT_SETTINGS_FILE_NAME)
                .forEachLine { line -> readSetting(line) }

            nameToValueMap.forEach {
                println(it)
            }
        } catch (e: FileNotFoundException) {
            println("Не удалось найти файл syntax.txt")
        } catch (e: Exception) {
            println("Ошибка при чтении файла: ${e.localizedMessage}")
        }
    }

    private fun readSetting(line: String) {
        val noWhitespaceLine = line.filter { character -> !character.isWhitespace() }

        val (name, value) = try {
            noWhitespaceLine.split(DEFAULT_SEPARATOR, limit = 2)
        } catch (e: Exception) {
            println("Не удалось распарсить строку:\n\t$line")
            return
        }

        nameToValueMap.putIfPresent(name, value)
    }

    private fun <K, V> MutableMap<K, V>.putIfPresent(key: K, value: V) {
        if (containsKey(key)) {
            put(key, value)
        }
    }

    companion object {
        private const val DEFAULT_SETTINGS_FILE_NAME = "src/syntax.txt"
        private const val DEFAULT_SEPARATOR = "="

        private const val DEFAULT_NON_TERMINAL_START = "_"
        private const val DEFAULT_NON_TERMINAL_END = "_"
        private const val DEFAULT_ARROW = "::="
        private const val DEFAULT_EPSILON = "e"
        private const val DEFAULT_ITER_START = "{"
        private const val DEFAULT_ITER_END = "}"
        private const val DEFAULT_OPTIONAL_ENTER_START = "["
        private const val DEFAULT_OPTIONAL_ENTER_END = "]"
        private const val DEFAULT_NECESSARILY_START = "("
        private const val DEFAULT_NECESSARILY_END = ")"
        private const val DEFAULT_ALTERNATIVE = "|"

        private const val NON_TERMINAL_START_KEY = "NStart"
        private const val NON_TERMINAL_END_KEY = "NEnd"
        private const val ARROW_KEY = "Arrow"
        private const val EPSILON_KEY = "Epsilon"
        private const val ITER_START_KEY = "IterStart"
        private const val ITER_END_KEY = "IterEnd"
        private const val OPTIONAL_ENTER_START_KEY = "OptionalEnterStart"
        private const val OPTIONAL_ENTER_END_KEY = "OptionalEnterEnd"
        private const val NECESSARILY_START_KEY = "NecessarilyStart"
        private const val NECESSARILY_END_KEY = "NecessarilyEnd"
        private const val ALTERNATIVE_KEY = "Alternative"
    }
}