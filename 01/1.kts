import java.io.File

val lines = File("input1").readLines()
val numbers = lines.map { line ->
    ("${line.first { it.isDigit() }}${line.last { it.isDigit() }}").toInt()
}
println(numbers.sum())