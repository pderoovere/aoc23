import java.io.File

val cards = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')

enum class HandType {
    FIVE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, THREE_OF_A_KIND, TWO_PAIRS, ONE_PAIR, HIGH_CARD;

    companion object {

        fun of(cards: List<Char>): HandType {
            val jCount = cards.count { it == 'J' }
            val counts =
                cards.filter { it != 'J' }
                    .groupBy { it }.values.map { it.size }
                    .sortedDescending()
                    .toMutableList()
            if (counts.isEmpty()) {
                counts.add(jCount)
            } else {
                counts[0] += jCount
            }
            return when {
                counts.contains(5) -> FIVE_OF_A_KIND
                counts.contains(4) -> FOUR_OF_A_KIND
                counts.contains(3) && counts.contains(2) -> FULL_HOUSE
                counts.contains(3) -> THREE_OF_A_KIND
                counts.count { it == 2 } == 2 -> TWO_PAIRS
                counts.contains(2) -> ONE_PAIR
                else -> HIGH_CARD
            }
        }
    }
}

fun compare(hand1: List<Char>, hand2: List<Char>): Int {
    val type1 = HandType.of(hand1)
    val type2 = HandType.of(hand2)
    return type1.compareTo(type2).takeIf { it != 0 } ?: hand1.zip(hand2)
        .map { (c1, c2) -> cards.indexOf(c1).compareTo(cards.indexOf(c2)) }.first { it != 0 }
}

val lines = File("input").readLines()
val entries = lines.map {
    val cards = it.split(" ")[0].toList()
    val bid = it.split(" ")[1].toInt()
    cards to bid
}
val scores = entries.sortedWith { (cards1, _), (cards2, _) ->
    compare(cards1, cards2)
}.reversed().mapIndexed { index, (_, bid) -> (index + 1) * bid }

//247961593
println(scores.sum())