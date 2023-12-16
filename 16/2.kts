import java.io.File

data class Location(val row: Int, val col: Int) {
    operator fun minus(other: Location) = Location(row - other.row, col - other.col)
    operator fun plus(other: Location) = Location(row + other.row, col + other.col)
    val up get() = Location(row - 1, col)
    val down get() = Location(row + 1, col)
    val left get() = Location(row, col - 1)
    val right get() = Location(row, col + 1)
}

data class Visit(val from: MutableSet<Location> = mutableSetOf())

fun pass(from: Location, to: Location) = to + (to - from)

fun nextLocations(char: Char, from: Location, to: Location): List<Pair<Location, Location>> {
    val pass = pass(from, to)
    val next = when (char) {
        '.' -> listOf(pass)
        '-' -> if (from.row == to.row) listOf(pass) else listOf(to.left, to.right)
        '|' -> if (from.col == to.col) listOf(pass) else listOf(to.up, to.down)
        '/' -> when {
            from == to.left -> listOf(to.up)
            from == to.right -> listOf(to.down)
            from == to.up -> listOf(to.left)
            from == to.down -> listOf(to.right)
            else -> throw IllegalArgumentException("Unknown direction: $char")
        }

        '\\' -> when {
            from == to.left -> listOf(to.down)
            from == to.right -> listOf(to.up)
            from == to.up -> listOf(to.right)
            from == to.down -> listOf(to.left)
            else -> throw IllegalArgumentException("Unknown direction: $char")
        }

        else -> throw IllegalArgumentException("Unknown direction: $char")
    }
    return next.map { to to it }
}

fun move(
    from: Location,
    to: Location,
    tiles: Map<Location, Char>,
    visited: MutableMap<Location, Visit>
): List<Pair<Location, Location>> {
    val next =
        nextLocations(tiles[to]!!, from, to).filter { it.second in tiles && it.first !in visited[it.second]!!.from }
    next.forEach { visited[it.second]!!.from.add(it.first) }
    return next
}

val tiles = File("input").readLines().mapIndexed { row, line ->
    line.mapIndexedNotNull { col, c ->
        Location(row, col) to c
    }
}.flatten().toMap()

fun calculate(from: Location, to: Location): Int {
    val visited = tiles.mapValues { Visit() }.toMutableMap()
    visited[to]!!.from.add(from)
    var nextLocations = listOf(from to to)
    while (nextLocations.isNotEmpty()) {
        nextLocations = nextLocations.flatMap { (from, to) ->
            move(from, to, tiles, visited)
        }
    }
    return visited.values.count { it.from.isNotEmpty() }
}

val maxRow = tiles.keys.maxOf { it.row }
val maxCol = tiles.keys.maxOf { it.col }

val leftToRightMax = (0..maxRow).maxOf { row ->
    calculate(Location(row, -1), Location(row, 0))
}
val topToBottomMax = (0..maxCol).maxOf { col ->
    calculate(Location(-1, col), Location(0, col))
}
val rightToLeftMax = (0..maxRow).maxOf { row ->
    calculate(Location(row, maxCol + 1), Location(row, maxCol))
}
val bottomToTopMax = (0..maxCol).maxOf { col ->
    calculate(Location(maxRow + 1, col), Location(maxRow, col))
}
println(listOf(leftToRightMax, topToBottomMax, rightToLeftMax, bottomToTopMax).max())