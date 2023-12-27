import java.io.File

data class Location(val row: Int, val column: Int) {
    val north get() = Location(row - 1, column)
    val south get() = Location(row + 1, column)
    val east get() = Location(row, column + 1)
    val west get() = Location(row, column - 1)
    val neighbours get() = listOf(west, south, east, north)
}

val lines = File("input").readLines()
val grid = lines.mapIndexed { row, line ->
    line.mapIndexed { column, char -> Location(row, column) to char }
}.flatten().toMap()
val maxCol = grid.map { it.key.column }.max()
val maxRow = grid.map { it.key.row }.max()
assert(maxCol == maxRow)
val size = maxRow + 1
val startingLocation = grid.filterValues { it == 'S' }.keys.single()

fun get(from: Location): Char {
    val row = (from.row % size + size) % size
    val col = (from.column % size + size) % size
    return grid[Location(row, col)] ?: throw IllegalStateException("No value at $from, $row, $col")
}

fun neighbours(from: Location) = from.neighbours.filter { get(it) != '#' }.toSet()

fun step(from: Set<Location>, remainingSteps: Long): Set<Location> {
    if (remainingSteps == 0L) return from
    val next = from.flatMap { neighbours(it) }.toSet()
    return step(next, remainingSteps - 1)
}

val edge = (size / 2).toLong()
val y0 = step(setOf(startingLocation), edge).size
val y1 = step(setOf(startingLocation), edge + size).size
val y2 = step(setOf(startingLocation), edge + 2 * size).size

fun result(n: Long): Long {
    val a = ((y2 - (2 * y1) + y0) / 2)
    val b = (y1 - y0 - a)
    val c = y0
    return a * n * n + b * n + c
}

println(result((26501365L - edge) / size))