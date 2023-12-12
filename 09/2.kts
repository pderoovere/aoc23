import java.io.File

val lines = File("input").readLines()
val histories = lines.map { it.split(" ").map(String::toInt) }

fun process(values: List<Int>): Int {
    if (values.all { it == 0 }) {
        return 0
    }
    val nextStepValues = values.dropLast(1).zip(values.drop(1)).map { (a, b) -> b - a }
    return values.first - process(nextStepValues)
}

println(histories.sumOf { process(it) })