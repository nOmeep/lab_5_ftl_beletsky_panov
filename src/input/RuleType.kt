package input

sealed class RuleType(
    protected val settings: SettingsModel
) {
    protected val ruleSet = mutableSetOf<String>()

    protected abstract fun convertNonParsedToParsed()

    fun defaultRuleParse(nonParsedRules: String): MutableSet<String> {
        val ruleList = nonParsedRules.split(settings.alternative)

        ruleList.map { nonParsedRule ->
            val rule = splitNonTerminals(nonParsedRule)
            ruleSet.add(rule)
        }

        convertNonParsedToParsed()

        return ruleSet
    }

    private fun splitNonTerminals(nonParsedRule: String): String {
        val globalSb = StringBuilder()

        println(nonParsedRule.replace(oldValue = settings.nonTerminalEnd, " "))

        var index = 0
        while (index != nonParsedRule.length) {

            when (val currentSymbol = nonParsedRule[index]) {
                settings.nonTerminalStart[0] -> {
                    if (nonParsedRule.substring(index, index + settings.nonTerminalStart.length) == settings.nonTerminalStart) {
                        index += settings.nonTerminalStart.length

                        globalSb.append("[")
                        val after = nonParsedRule.substring(index, nonParsedRule.length)

                        val before = after.substringBefore(settings.nonTerminalEnd).ifBlank {
                            after[0] + after.substring(1, after.length).substringBefore(settings.nonTerminalEnd)
                        }
                        index += before.length + settings.nonTerminalEnd.length
                        globalSb.append(before)

                        globalSb.append("]")
                    }
                }
                '"' -> {
                    val sb = StringBuilder()

                    while (index != nonParsedRule.length && nonParsedRule[index].toString() != "\"") {
                        val symbol = nonParsedRule[index++]
                        sb.append(symbol)
                    }

                    if (index == nonParsedRule.length) {
                        throw IllegalStateException("Закрывающий символ в отсутствии открывающего")
                    }

                    globalSb.append(sb)
                    index++
                }
                else -> {
                    globalSb.append(currentSymbol)
                    index++
                }
            }
        }

        return globalSb.toString()
    }

    class Optional(settings: SettingsModel) : RuleType(settings) {
        override fun convertNonParsedToParsed() {
            ruleSet.add(settings.epsilon)
        }
    }

    class Iteration(settings: SettingsModel, private val ebnf: EBNF) : RuleType(settings) {
        override fun convertNonParsedToParsed() {
            val allRules = ruleSet.map { rule -> "$rule[${ebnf.name}]" }

            ruleSet.clear()
            ruleSet.addAll(allRules)
            ruleSet.add(settings.epsilon)
        }
    }

    class Necessary(settings: SettingsModel) : RuleType(settings) {
        override fun convertNonParsedToParsed() {

        }
    }

    companion object {
        @JvmStatic
        fun valueOfString(settings: SettingsModel, ebnf: EBNF): RuleType {
            return when (ebnf.type) {
                "Iter" -> {
                    Iteration(settings, ebnf)
                }
                "Nes" -> {
                    Necessary(settings)
                }
                "Opt" -> {
                    Optional(settings)
                }
                else -> {
                    throw IllegalArgumentException("ВЛАД ЧЕ ТЫ МНЕ ТУТ ПЕРЕДАЛ ВООБЩЕ ТАКОЕ ИЗВИНИСЬ")
                }
            }
        }
    }
}
