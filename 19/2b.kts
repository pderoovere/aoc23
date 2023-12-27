import java.io.File

data class Rule(val category: Char, val larger: Boolean, val than: Long) {
    val inverse get() = Rule(category, !larger, than)
}

val workflows = File("test").readText().trim().split("\n\n").first().lines().map { line ->
    val (name, rulesStr) = line.split(Regex("[{}]")).take(2).map { it.trim() }
    val rules = rulesStr.split(",").map { ruleStr ->
        if (!ruleStr.contains(":")) return@map null to ruleStr
        val category = ruleStr[0]
        val op = ruleStr[1]
        val (valueStr, result) = ruleStr.drop(2).split(":").take(2)
        val value = valueStr.toLong()
        val rule = Rule(category, op == '>', value)
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

    fun largerThan(value: Long) = Range(maxOf(min, value + 1), max)
    fun smallerThanOrEqualTo(value: Long) = Range(min, minOf(max, value))
    fun overlaps(other: Range): Boolean {
        return this.min <= other.max && this.max >= other.min
    }

    fun split(other: Range): List<Range> {
        val result = mutableListOf<Range>()
        var current = this
        while (current.min <= other.max) {
            val min = current.min
            val max = minOf(current.max, other.max)
            result.add(Range(min, max))
            current = Range(max + 1, current.max)
        }
        return result
    }
}

fun range(rules: List<Rule>?): Range {
    var result = Range(1, 4000L)
    if (rules == null) return result
    rules.forEach { rule ->
        result = if (rule.larger) result.largerThan(rule.than) else result.smallerThanOrEqualTo(rule.than)
    }
    return result
}

data class Combination(val x: Range, val m: Range, val a: Range, val s: Range) {
    val size = x.size * m.size * a.size * s.size

    fun overlapsWith(other: Combination): Boolean {
        return this.x.overlaps(other.x) && this.m.overlaps(other.m) &&
                this.a.overlaps(other.a) && this.s.overlaps(other.s)
    }

    fun splitIfOverlaps(other: Combination): List<Combination> {
        // Check for overlap in all ranges
        if (this.x.overlaps(other.x) && this.m.overlaps(other.m) && this.a.overlaps(other.a) && this.s.overlaps(other.s)) {
            val splitX = this.x.split(other.x)
            val splitM = this.m.split(other.m)
            val splitA = this.a.split(other.a)
            val splitS = this.s.split(other.s)

            val combinations = mutableListOf<Combination>()

            // Create non-overlapping combinations
            for (xRange in splitX) {
                for (mRange in splitM) {
                    for (aRange in splitA) {
                        for (sRange in splitS) {
                            combinations.add(Combination(xRange, mRange, aRange, sRange))
                        }
                    }
                }
            }

            return combinations
        } else {
            // No overlap, return the original combination
            return listOf(this)
        }
    }
}

val rulePaths = execute("in", emptyList())
val result = rulePaths.filter { it.result == "A" }.map { it.rules }.map { rules ->
    val grouped = rules.groupBy { it.category }
    Combination(
        range(grouped['x'] ?: emptyList()),
        range(grouped['m'] ?: emptyList()),
        range(grouped['a'] ?: emptyList()),
        range(grouped['s'] ?: emptyList())
    )
}

fun removeOverlaps(combinations: List<Combination>): List<Combination> {
    val result = mutableListOf<Combination>()
    result.addAll(combinations)

    var index = 0
    while (index < result.size) {
        var hasOverlap = false

        // Compare the current combination with all other combinations
        for (otherIndex in result.indices) {
            if (index != otherIndex) {
                val current = result[index]
                val other = result[otherIndex]

                // Check and handle overlaps
                if (current.overlapsWith(other)) {
                    result.removeAt(index)
                    result.removeAt(otherIndex - if (otherIndex > index) 1 else 0)

                    val splitCombinations = current.splitIfOverlaps(other)
                    result.addAll(splitCombinations)

                    hasOverlap = true
                    break // Exit the inner loop to reprocess the updated list
                }
            }
        }

        if (!hasOverlap) {
            index++ // Move to the next combination if no overlap was found
        }
    }

    return result
}

println(result.size)

val newResults = removeOverlaps(result)

println(result.sumOf { it.size })
println(newResults.sumOf { it.size })

// 167459205617600
// goal: 167409079868000
