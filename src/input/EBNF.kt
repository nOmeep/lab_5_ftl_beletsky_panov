package input

class EBNF(
        val name: String,
        val rules: MutableList<EBNF>,
        val nonParsedRules: String,
        val ready: Boolean,
        val type: String
)