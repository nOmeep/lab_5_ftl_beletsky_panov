package input

data class SettingsModel(
    val nonTerminalStart: String,
    val nonTerminalEnd: String,
    val arrow: String,
    val epsilon: String,
    val iterStart: String,
    val iterEnd: String,
    val optionalEnterStart: String,
    val optionalEnterEnd: String,
    val necessarilyStart: String,
    val necessarilyEnd: String,
    val alternative: String
)
