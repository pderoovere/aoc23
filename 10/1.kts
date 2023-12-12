import _1.Path
import java.io.File

data class Location(val row: Int, val column: Int) {
    val north get() = Location(row - 1, column)
    val south get() = Location(row + 1, column)
    val east get() = Location(row, column + 1)
    val west get() = Location(row, column - 1)
    val neighbours get() = listOf(west, south, east, north)
}

typealias Path = List<Location>

fun next(from: Location, option1: Location, option2: Location) =
    when (from) {
        option1 -> option2
        option2 -> option1
        else -> null
    }

fun neighbour(move: Pair<Location, Location>, char: Char) = with(move.second) {
    when (char) {
        '|' -> next(move.first, north, south)
        '-' -> next(move.first, east, west)
        'L' -> next(move.first, north, east)
        'J' -> next(move.first, north, west)
        '7' -> next(move.first, south, west)
        'F' -> next(move.first, south, east)
        else -> null
    }
}

val lines = File("input").readLines()
val grid = lines.mapIndexed { row, line ->
    line.mapIndexed { column, char -> Location(row, column) to char }
}.flatten().toMap()
val startingLocation = grid.filterValues { it == 'S' }.keys.single()

tailrec fun travel(
    from: Location,
    to: Location,
    path: Path
): Path? {
    val char = grid[to] ?: return null
    if (char == 'S') return path
    val next = neighbour(from to to, char) ?: return null
    val newPath = path + to
    return travel(to, next, newPath)
}

val solution = startingLocation.neighbours.firstNotNullOf { travel(startingLocation, it, listOf(startingLocation)) }
println(solution)
println(solution.size / 2)