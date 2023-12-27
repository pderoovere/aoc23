import java.io.File

enum class Operation {
    SMALLER, GREATER, SMALLER_OR_EQUAL_TO, GREATER_OR_EQUAL_TO;

    operator fun not() = when (this) {
        SMALLER -> GREATER_OR_EQUAL_TO
        GREATER -> SMALLER_OR_EQUAL_TO
        SMALLER_OR_EQUAL_TO -> GREATER
        GREATER_OR_EQUAL_TO -> SMALLER
    }

    companion object {

        fun fromChar(c: Char) = when (c) {
            '<' -> SMALLER
            '>' -> GREATER
            else -> throw IllegalArgumentException("Unknown operation: $c")
        }
    }
}

data class Rule(val category: Char, val operation: Operation, val than: Long) {
    val inverse get() = Rule(category, !operation, than)
}


val workflows = File("input").readText().trim().split("\n\n").first().lines().map { line ->
    val (name, rulesStr) = line.split(Regex("[{}]")).take(2).map { it.trim() }
    val rules = rulesStr.split(",").map { ruleStr ->
        if (!ruleStr.contains(":")) return@map null to ruleStr
        val category = ruleStr[0]
        val op = ruleStr[1]
        val (valueStr, result) = ruleStr.drop(2).split(":").take(2)
        val value = valueStr.toLong()
        val rule = Rule(category, Operation.fromChar(op), value)
        rule to result
    }
    name to rules
}.toMap()

data class RulePath(val result: String, val rules: List<Rule>)

fun execute(name: String, rules: List<Rule>): List<RulePath> {
    when (name) {
        "A", "R" -> return listOf(RulePath(name, rules))
        else -> {
            val results = mutableListOf<RulePath>()
            val workflow = workflows[name]!!
            val rules = rules.toMutableList()
            for ((rule, result) in workflow) {
                if (rule == null) {
                    results.addAll(execute(result, rules))
                } else {
                    results.addAll(execute(result, rules + rule))
                    rules.add(rule.inverse)
                }
            }
            return results
        }
    }
}


data class Range(val min: Long, val max: Long) {
    operator fun plus(other: Range) = Range(min + other.min, max + other.max)
    operator fun times(other: Range) = Range(min * other.min, max * other.max)
    val size get() = max - min + 1

    fun apply(rule: Rule) = when (rule.operation) {
        Operation.SMALLER -> Range(min, minOf(max, rule.than - 1))
        Operation.GREATER -> Range(maxOf(min, rule.than + 1), max)
        Operation.SMALLER_OR_EQUAL_TO -> Range(min, minOf(max, rule.than))
        Operation.GREATER_OR_EQUAL_TO -> Range(maxOf(min, rule.than), max)
    }
}

fun range(rules: List<Rule>?): Range {
    var result = Range(1, 4000L)
    if (rules == null) return result
    rules.forEach { rule ->
        result = result.apply(rule)
    }
    return result
}

data class Combination(val x: Range, val m: Range, val a: Range, val s: Range) {
    val size = x.size * m.size * a.size * s.size
}

val rulePaths = execute("in", emptyList())
val result = rulePaths.filter { it.result == "A" }.map { it.rules }.map { rules ->
    val rulesByCategory = rules.groupBy { it.category }
    Combination(
        range(rulesByCategory['x']),
        range(rulesByCategory['m']),
        range(rulesByCategory['a']),
        range(rulesByCategory['s'])
    )
}

println(result.size)

println(result.sumOf { it.size })

// 167459205617600
// goal: 167409079868000
