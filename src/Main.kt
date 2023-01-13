import input.Parser
import input.SettingsReader

fun main() {
    val settings = SettingsReader().readSyntax()
    val parser = Parser(settings)
    val strings = parser.readInput()
    var listEBNF = parser.parser(strings)
}
