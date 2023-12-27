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
val startingLocation = grid.filterValues { it == 'S' }.keys.single()

fun neighbours(from: Location) = from.neighbours.filter { it in grid && grid[it] != '#' }.toSet()

fun step(from: Set<Location>, remainingSteps: Int): Set<Location> {
    if (remainingSteps == 0) return from
    val next = from.flatMap { neighbours(it) }.toSet()
    return step(next, remainingSteps - 1)
}

println(step(setOf(startingLocation), 64).size)