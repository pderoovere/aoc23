import java.io.File
import kotlin.math.max

data class Location(val row: Int, val column: Int) {
    private val north get() = Location(row - 1, column)
    private val south get() = Location(row + 1, column)
    private val east get() = Location(row, column + 1)
    private val west get() = Location(row, column - 1)
    val neighbours get() = listOf(west, south, east, north)
}

data class Path(val locations: List<Location>, val cost: Int) {
    val from = locations.first()
    val prev = locations[locations.size - 2]
    val to = locations.last()
    val reversed get() = Path(locations.reversed(), cost)

    operator fun plus(other: Path): Path {
        require(to == other.from)
        return Path(locations + other.locations, cost + other.cost)
    }
}

val lines = File("input").readLines()
val map = lines.mapIndexed { row, line ->
    line.mapIndexed { column, char -> Location(row, column) to char }
}.flatten().toMap()
val maxRow = map.keys.maxBy { it.row }.row
val startingLocation = map.keys.single { it.row == 0 && map[it] == '.' }
val startingNeighbour = startingLocation.neighbours.single { it in map && map[it] != '#' }
val destination = map.keys.single { it.row == maxRow && map[it] == '.' }

val visited = mutableSetOf<Location>()
val paths = mutableListOf<Path>()

fun travel(path: Path): Path {
    val neighbours = path.to.neighbours.filter { it in map && map[it] != '#' && it != path.prev }
    return if (neighbours.size == 1) {
        travel(Path(path.locations + neighbours[0], path.cost + 1))
    } else {
        path
    }
}

fun fillPaths(location: Location) {
    if (location in visited) return
    visited.add(location)
    val neighbours = location.neighbours.filter { it in map && map[it] != '#' && it !in visited }
    val newPaths = neighbours.map { travel(Path(listOf(location, it), 1)) }
    paths.addAll(newPaths)
    newPaths.forEach { fillPaths(it.to) }
}

fillPaths(startingLocation)

fun findLongestPath(paths: List<Path>, start: Location, end: Location): Int {
    // Create graph
    val graph = mutableMapOf<Location, MutableList<Pair<Location, Int>>>()
    for (path in paths) {
        graph.computeIfAbsent(path.from) { mutableListOf() }.add(Pair(path.to, path.cost))
    }

    // DFS function
    fun dfs(current: Location, destination: Location, visited: MutableSet<Location>, currentDistance: Int): Int {
        if (current == destination) return currentDistance
        visited.add(current)

        var maxDistance = 0
        val neighbors = graph[current] ?: return 0
        for ((next, distance) in neighbors) {
            if (next !in visited) {
                maxDistance = max(maxDistance, dfs(next, destination, visited, currentDistance + distance))
            }
        }

        visited.remove(current)
        return maxDistance
    }

    return dfs(start, end, mutableSetOf(), 0)
}

findLongestPath(paths, startingLocation, destination).let(::println)