package input

class ContextFreeResolver(
    private val settings: SettingsModel,
    private val listEBNF: MutableList<EBNF>
) {
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
