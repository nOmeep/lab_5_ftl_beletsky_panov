import input.EBNF
import input.Parser
import input.SettingsModel
import input.SettingsReader

fun main() {
    val solver = TaskSolver()
    solver.solve()
}

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
                    throw IllegalStateException("Неожиданный поворот событий")
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

class ContextFreeResolver(
    private val settings: SettingsModel,
    private val listEBNF: MutableList<EBNF>
) {
    // Храним мапу "имя -> список правил"
    private val nonTerminalMap = mutableMapOf<String, MutableSet<String>>()
    fun convert(): List<NonTerminal> {
        return listEBNF.map { ebnf ->
            val nonTerminalName = ebnf.name
            val nonTerminalRules = RuleType.valueOfString(settings, ebnf)
                .defaultRuleParse(ebnf.nonParsedRules)

            NonTerminal(nonTerminalName, nonTerminalRules.toList())
        }
    }

    data class NonTerminal(
        private val name: String,
        private val rules: List<String>
    ) {
        fun getNonTerminalString(): String {
            val stringRules = rules.joinToString(separator = " | ")
            return "[$name] -> $stringRules"
        }
    }
}

class TaskSolver {
    fun solve() {
        val settings = SettingsReader().readSyntax()
        val parser = Parser(settings)

        val strings = parser.readInput()
        val listEBNF = parser.parse(strings)

        println("EBNF list")
        listEBNF.forEach {
            println(it)
        }

        val contextFreeResolver = ContextFreeResolver(settings, listEBNF)
        val nonTerminals = contextFreeResolver.convert()

        println("КС вид:")
        nonTerminals
            .map { nonTerminal ->
                nonTerminal.getNonTerminalString()
            }.forEach { nonTerminalString ->
                println(nonTerminalString)
            }
    }
}