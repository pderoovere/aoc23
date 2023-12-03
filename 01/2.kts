import java.io.File

val digits = mapOf(
    "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9
) + (0..9).associateBy { it.toString() }

fun String.digitAt(index: Int): Int? {
    return digits.firstNotNullOfOrNull { (k, v) -> if (this.substring(index).startsWith(k)) v else null }
}

val lines = File("input").readLines()
val numbers = lines.map { line ->
    val firstDigit = line.indices.first { line.digitAt(it) != null }.let { line.digitAt(it)!! }
    val lastDigit = line.indices.last { line.digitAt(it) != null }.let { line.digitAt(it)!! }
    firstDigit * 10 + lastDigit
}
println(numbers.sum())