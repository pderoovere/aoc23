import java.io.File

val lines = File("input").readLines()
val instructions = lines[0]
val nodes = lines.drop(2).associate { line ->
    val (node, l, r) = "[A-Z0-9]+".toRegex().findAll(line).map { it.value }.toList()
    node to (l to r)
}

var count = 0L
var curNodes = nodes.keys.filter { it.endsWith("A") }
var cycles: MutableList<Long?> = curNodes.map { null }.toMutableList()
loop@ while (!cycles.all { it != null }) {
    for ((index, node) in curNodes.withIndex()) {
        if (node.endsWith("Z") && cycles[index] == null) {
            cycles[index] = count
        }
    }
    curNodes = curNodes.map { node ->
        val (l, r) = nodes[node]!!
        if (instructions[(count % instructions.length).toInt()] == 'L') l else r
    }
    count += 1
}

fun gcd(a: Long, b: Long): Long {
    if (b == 0L) return a
    return gcd(b, a % b)
}

fun lcm(a: Long, b: Long): Long {
    return a / gcd(a, b) * b
}

println(cycles.reduce { acc, cycle -> lcm(acc!!, cycle!!) })