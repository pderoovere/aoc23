import java.io.File

data class Card(val id: Int, val winning: List<Int>, val numbers: List<Int>, var amount: Int = 1) {
    val score = numbers.count { winning.contains(it) }
}

val spacesRegex = "\\s+".toRegex()
val lines = File("input").readLines()
val cards = lines.map { line ->
    val id = line.split(":")[0].split(spacesRegex)[1].toInt()
    val (winning, numbers) = line.split(":")[1].split(" | ")
    Card(
        id,
        winning.trim().split(spacesRegex).map(String::toInt),
        numbers.trim().split(spacesRegex).map(String::toInt)
    )
}

cards.forEachIndexed { index, card ->
    for (i in 1..card.score) {
        cards[index + i].amount += card.amount
    }
}

println(cards.sumOf { it.amount })