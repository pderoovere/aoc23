import java.io.File
import kotlin.math.pow

data class Card(val winning: List<Int>, val numbers: List<Int>) {
    val score = 2.0.pow(numbers.count { winning.contains(it) } - 1).toInt()
}

val spacesRegex = "\\s+".toRegex()
val lines = File("input").readLines()
val cards = lines.map { line ->
    val (winning, numbers) = line.split(":")[1].split(" | ")
    Card(
        winning.trim().split(spacesRegex).map(String::toInt),
        numbers.trim().split(spacesRegex).map(String::toInt)
    )
}
println(cards.sumOf { it.score })
