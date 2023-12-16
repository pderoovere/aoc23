import java.io.File

fun reflectionIndices(group: List<List<Char>>): Set<Int> {
    val result = mutableSetOf<Int>()
    (1 until group.size).forEach { i ->
        val above = group.take(i)
        val below = group.drop(i)
        val size = minOf(above.size, below.size)
        val above2 = above.takeLast(size)
        val below2 = below.take(size)
        if (above2 == below2.reversed()) {
            result.add(i)
        }
    }
    return result
}

fun transpose(group: List<List<Char>>): List<List<Char>> {
    val result = mutableListOf<MutableList<Char>>()
    group.forEachIndexed { i, row ->
        row.forEachIndexed { j, char ->
            if (i == 0) {
                result.add(mutableListOf())
            }
            result[j].add(char)
        }
    }
    return result
}

fun smudgeFixes(group: List<List<Char>>): List<List<List<Char>>> {
    val result = mutableListOf<List<List<Char>>>()
    for (i in group.indices) {
        for (j in group[i].indices) {
            val newGroup = group.map { it.toMutableList() }
            newGroup[i][j] = if (newGroup[i][j] == '#') '.' else '#'
            result.add(newGroup)
        }
    }
    return result
}

data class ReflectionIndices(val horizontal: Set<Int>, val vertical: Set<Int>) {
    operator fun minus(other: ReflectionIndices): ReflectionIndices {
        return ReflectionIndices(horizontal - other.horizontal, vertical - other.vertical)

    }
}

fun calculateReflectionIndices(group: List<List<Char>>): ReflectionIndices {
    val hReflectionIndices = reflectionIndices(group)
    val vReflectionIndices = reflectionIndices(transpose(group))
    return ReflectionIndices(hReflectionIndices, vReflectionIndices)
}

val result = File("input").readText().trim().split("\n\n").mapIndexed { i, g ->
    val group = g.split("\n").map { it.toList() }
    val originalReflectionIndices = calculateReflectionIndices(group)
    smudgeFixes(group).map {
        calculateReflectionIndices(it) - originalReflectionIndices
    }.toSet().single { it != ReflectionIndices(emptySet(), emptySet()) }.let {
        it.horizontal.singleOrNull()?.let { it * 100 } ?: it.vertical.single()
    }
}

println(result.sum())