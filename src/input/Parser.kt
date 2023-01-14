package input

class Parser(private val model: SettingsModel) {
    var listEBNF = mutableListOf<EBNF>()
    var count = 0
    var change = ""
    fun readInput(): List<String> {
        val list = mutableListOf<String>()
        while (true) {
            val line = readLine() ?: break
            if (line.isBlank()) break
            list.add(line)
        }
        return list
    }

    fun parse(list: List<String>): MutableList<EBNF> {
        list.forEach { str ->
            val newStr = str.filter { !it.isWhitespace() }
            val right = newStr.split(model.arrow)[1]
            val left = newStr.split(model.arrow)[0]
            val name = left.substring(model.nonTerminalStart.length, left.length - model.nonTerminalEnd.length)
            bracket(right, name)
        }
        return listEBNF
    }

    private fun findNonterm(rule: String): Int{
        listEBNF.forEachIndexed{index, ebnf->
            if(ebnf.nonParsedRules == rule){
                return index
            }
        }
        return -1
    }

    private fun bracket(oldLine: String, name: String){
        var line = oldLine
        var ended = false
        while (!ended){
            var changed = false
            var start = 0
            var end = 0
            if(model.necessarilyStart !in line && model.iterStart !in line && model.optionalEnterStart !in line) ended = true
            else{
                if(model.optionalEnterStart in line) {
                    for (i in start until line.length) {
                        if (end == 0 && line[i] in model.optionalEnterStart) {
                            if (line.substring(i, i + model.optionalEnterStart.length) == model.optionalEnterStart) {
                                start = i
                            }
                        } else if (end == 0 && line[i] in model.optionalEnterEnd) {
                            if (line.substring(i, i + model.optionalEnterEnd.length) == model.optionalEnterEnd) {
                                end = i
                            }
                        }
                    }
                    if(model.necessarilyStart !in line.substring(start + model.optionalEnterStart.length, end + model.optionalEnterEnd.length - 1) &&
                            model.iterStart !in line.substring(start + model.optionalEnterStart.length, end + model.optionalEnterEnd.length - 1)) {
                        change = line.substring(start + model.optionalEnterStart.length, end + model.optionalEnterEnd.length - 1)
                        if(findNonterm(change) == -1){
                            listEBNF.add(EBNF(name = "Nonterm${count}", nonParsedRules = change, type = "Opt"))
                            line = line.substring(0, start) + model.nonTerminalStart + "Nonterm${count}" + model.nonTerminalEnd +
                                    line.substring(end + model.optionalEnterEnd.length)
                            changed = true
                            count++
                        }
                        else{
                            line = line.substring(0, start) + model.nonTerminalStart + "Nonterm${findNonterm(change)}" + model.nonTerminalEnd +
                                    line.substring(end + model.optionalEnterEnd.length)
                            changed = true
                        }
                    }
                    else{
                        start = 0
                        end = 0
                    }
                }
                if(!changed && model.necessarilyStart in line) {
                    for (i in start until line.length) {
                        if (end == 0 && line[i] in model.necessarilyStart) {
                            if (line.substring(i, i + model.necessarilyStart.length) == model.necessarilyStart) {
                                start = i
                            }
                        } else if (end == 0 && line[i] in model.necessarilyEnd) {
                            if (line.substring(i, i + model.necessarilyEnd.length) == model.necessarilyEnd) {
                                end = i
                            }
                        }
                    }
                    if(model.optionalEnterStart !in line.substring(start + model.necessarilyStart.length, end + model.necessarilyEnd.length - 1) &&
                            model.iterStart !in line.substring(start + model.necessarilyStart.length, end + model.necessarilyEnd.length - 1)) {
                        change = line.substring(start + model.necessarilyStart.length, end + model.necessarilyEnd.length - 1)
                        if(findNonterm(change) == -1) {
                            listEBNF.add(EBNF(name = "Nonterm${count}", nonParsedRules =  change, type = "Nes"))
                            line = line.substring(0, start) + model.nonTerminalStart + "Nonterm${count}" + model.nonTerminalEnd +
                                    line.substring(end + model.necessarilyEnd.length)
                            changed = true
                            count++
                        }
                        else{
                            line = line.substring(0, start) + model.nonTerminalStart + "Nonterm${findNonterm(change)}" + model.nonTerminalEnd +
                                    line.substring(end + model.optionalEnterEnd.length)
                            changed = true
                        }
                    }
                    else{
                        start = 0
                        end = 0
                    }
                }
                if(!changed && model.iterStart in line) {
                    for (i in start until line.length) {
                        if (end == 0 && line[i] in model.iterStart) {
                            if (line.substring(i, i + model.iterStart.length) == model.iterStart) {
                                start = i
                            }
                        } else if (end == 0 && line[i] in model.iterEnd) {
                            if (line.substring(i, i + model.iterEnd.length) == model.iterEnd) {
                                end = i
                            }
                        }
                    }
                    if(model.optionalEnterStart !in line.substring(start + model.iterStart.length, end + model.iterEnd.length - 1) &&
                            model.necessarilyStart !in line.substring(start + model.iterStart.length, end + model.iterEnd.length - 1)) {
                        change = line.substring(start + model.iterStart.length, end + model.iterEnd.length - 1)
                        if(findNonterm(change) == -1) {
                            listEBNF.add(EBNF(name = "Nonterm${count}", nonParsedRules =  change, type =  "Iter"))
                            line = line.substring(0, start) + model.nonTerminalStart + "Nonterm${count}" + model.nonTerminalEnd +
                                    line.substring(end + model.iterEnd.length)
                            count++
                        }
                        else{
                            line = line.substring(0, start) + model.nonTerminalStart + "Nonterm${findNonterm(change)}" + model.nonTerminalEnd +
                                    line.substring(end + model.optionalEnterEnd.length)
                            changed = true
                        }
                    }
                }
            }
        }
        if(findNonterm(line) == -1) listEBNF.add(EBNF(name = name, nonParsedRules = line, type = "Nes"))

        listEBNF.forEach {
            print(it.name + ' ')
            print(it.type + " ")
            print(it.nonParsedRules + "\n")
        }
    }
}