import java.io.File

data class Number(private val firstDigit: Int, val colStart: Int, val row: Int) {
    var value = firstDigit
    var colEnd = colStart
    fun appendDigit(digit: Int) {
        value = value * 10 + digit
        colEnd++
    }
}

data class Symbol(val col: Int, val row: Int)

val lines = File("input").readLines()
val numbers = mutableListOf<Number>()
val symbols = mutableListOf<Symbol>()
var number: Number? = null

fun handleDigit(char: Char, col: Int, row: Int) {
    if (number == null) {
        number = Number(char.digitToInt(), col, row)
    } else {
        number!!.appendDigit(char.digitToInt())
    }
}

fun handleNumber() {
    if (number != null) {
        numbers.add(number!!)
        number = null
    }
}

for (row in lines.indices) {
    handleNumber()
    val line = lines[row]
    for (col in line.indices) {
        val char = line[col]
        if (char.isDigit()) {
            handleDigit(char, col, row)
        } else {
            handleNumber()
            if (char != '.') {
                symbols.add(Symbol(col, row))
            }
        }
    }
}

fun adjacent(symbol: Symbol, number: Number): Boolean {
    return ((symbol.row in number.row - 1..number.row + 1) && (symbol.col in number.colStart - 1..number.colEnd + 1))
}

println(numbers.filter { number -> symbols.any { symbol -> adjacent(symbol, number) } }.sumOf { it.value })