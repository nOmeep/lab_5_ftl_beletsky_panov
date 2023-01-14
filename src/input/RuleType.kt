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
        var index = 0
        while (index != nonParsedRule.length) {
            val currentSymbol = nonParsedRule[index++]

            when (currentSymbol.toString()) {
                settings.nonTerminalStart -> {
                    val sb = StringBuilder()

                    while (index != nonParsedRule.length && nonParsedRule[index].toString() != settings.nonTerminalEnd) {
                        val symbol = nonParsedRule[index++]
                        sb.append(symbol)
                    }

                    if (index == nonParsedRule.length) {
                        throw IllegalStateException("Закрывающий символ в отсутствии открывающего")
                    }

                    globalSb.append("[")
                    globalSb.append(sb)
                    globalSb.append("]")

                    index++
                }
                settings.nonTerminalEnd -> {
                    throw IllegalStateException("Закрывающий символ в отсутствии открывающего")
                }
                "\"" -> {
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
