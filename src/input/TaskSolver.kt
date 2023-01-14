package input

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
