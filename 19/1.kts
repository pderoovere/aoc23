import java.io.File

data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
    fun get(c: Char) = when (c) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> throw IllegalArgumentException("Unknown part: $c")
    }
}

val (workflowLines, partLines) = File("input").readText().trim().split("\n\n").map { it.lines() }
val workflows = workflowLines.map { line ->
    val (name, rulesStr) = line.split(Regex("\\{|\\}")).take(2).map { it.trim() }
    val rules = rulesStr.split(",").map { ruleStr ->
        if (!ruleStr.contains(":")) return@map { _: Part -> ruleStr }
        val category = ruleStr[0]
        val op = ruleStr[1]
        val (valueStr, result) = ruleStr.drop(2).split(":").take(2)
        val value = valueStr.toInt()
        val rule = { part: Part ->
            if (when (op) {
                    '>' -> part.get(category) > value
                    '<' -> part.get(category) < value
                    else -> throw IllegalArgumentException("Unknown op: $op")
                }
            ) result else null
        }
        rule
    }
    name to rules
}.toMap()
val parts = partLines.map { ratingLine ->
    val (x, m, a, s) = ratingLine.trim('{', '}').split(",").map { it.split("=").last().trim().toInt() }
    Part(x, m, a, s)
}

fun execute(part: Part, workflow: List<(Part) -> String?>): Boolean {
    for (w in workflow) {
        val result = w(part) ?: continue
        return when (result) {
            "A" -> true
            "R" -> false
            else -> execute(part, workflows[result]!!)
        }
    }
    throw IllegalStateException("No result for part: $part")
}

parts.filter {
    execute(it, workflows["in"]!!)
}.sumOf { it.x + it.a + it.m + it.s }.let { println(it) }