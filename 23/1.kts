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

fun neighbours(path: List<Location>, map: Map<Location, Char>): List<Location> {
    val last = path.last()
    return when (val char = map[last]!!) {
        '>' -> listOf(last.east)
        '<' -> listOf(last.west)
        '^' -> listOf(last.north)
        'v' -> listOf(last.south)
        '.' -> last.neighbours
        else -> throw IllegalArgumentException("Unknown char: $char")
    }.filter { it !in path && it in map && map[it] != '#' }
}

val lines = File("input").readLines()
val grid = lines.mapIndexed { row, line ->
    line.mapIndexed { column, char -> Location(row, column) to char }
}.flatten().toMap()
val maxRow = grid.keys.maxBy { it.row }.row
val startingLocation = grid.keys.single { it.row == 0 && grid[it] == '.' }
val destination = grid.keys.single { it.row == maxRow && grid[it] == '.' }

fun travel(path: Path, destination: Location, map: Map<Location, Char>): List<Path> {
    if (path.last() == destination) return listOf(path)
    val next = neighbours(path, map)
    return next.flatMap { travel(path + it, destination, map) }
}

val routes = travel(listOf(startingLocation), destination, grid)
println(routes.maxOf { it.size } - 1)