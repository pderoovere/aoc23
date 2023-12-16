import java.io.File

fun reflectionIndex(group: List<List<Char>>): Int? {
    (1 until group.size).forEach { i ->
        val above = group.take(i)
        val below = group.drop(i)
        val size = minOf(above.size, below.size)
        val above2 = above.takeLast(size)
        val below2 = below.take(size)
        if (above2 == below2.reversed()) {
            return i
        }
    }
    return null
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

val result = File("input").readText().split("\n\n").map { g ->
    val group = g.split("\n").map { it.toList() }
    val hReflectionIndex = reflectionIndex(group)
    val vReflectionIndex = reflectionIndex(transpose(group))
    (hReflectionIndex?.let { it * 100 } ?: vReflectionIndex)!!
}
println(result.sum())