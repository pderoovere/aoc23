import java.io.File

val lines = File("input").readLines()
val instructions = lines[0]
val nodes = lines.drop(2).associate { line ->
    val (node, l, r) = "[A-Z]+".toRegex().findAll(line).map { it.value }.toList()
    node to (l to r)
}

var count = 0
var node = "AAA"
while (node != "ZZZ") {
    val (l, r) = nodes[node]!!
    node = if (instructions[count++ % instructions.length] == 'L') l else r
}

println(count)