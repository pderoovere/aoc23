import java.io.File
import java.util.*

data class Location(val row: Int, val col: Int) {
    operator fun minus(other: Location) = Location(row - other.row, col - other.col)
    operator fun plus(other: Location) = Location(row + other.row, col + other.col)
    operator fun times(other: Int) = Location(row * other, col * other)
    operator fun div(other: Int) = Location(row / other, col / other)
    operator fun unaryMinus() = Location(-row, -col)
    val size get() = row + col
    val norm get() = this / size
}

val directions = listOf(
    Location(-1, 0),
    Location(1, 0),
    Location(0, -1),
    Location(0, 1),
)

data class Node(
    val location: Location,
    val direction: Location,
    val stepsInDirection: Int,
    val cost: Int,
    val parent: Node?
) {
    val horizontalLastMove = direction.row == 0
}

fun neighbours(node: Node, map: Map<Location, Int>): List<Node> {
    return directions.filter { it != node.direction && it != -node.direction }.flatMap { direction ->
        (4..10).mapNotNull { steps ->
            val location = node.location + direction * steps
            if (location !in map) {
                null
            } else {
                val cost = node.cost + (1..steps).sumOf { s -> map[node.location + direction * s]!! }
                Node(location, direction, steps, cost, node)
            }
        }
    }
}

val map = File("input").readLines().mapIndexed { row, line ->
    line.mapIndexedNotNull { col, c ->
        Location(row, col) to c.toString().toInt()
    }
}.flatten().toMap()
val maxRow = map.keys.maxByOrNull { it.row }!!.row
val maxCol = map.keys.maxByOrNull { it.col }!!.col

val startingNodes = neighbours(Node(Location(0, 0), Location(0, 0), 0, 0, null), map)
val openSet = PriorityQueue<Node>(compareBy { it.cost })
openSet.addAll(startingNodes)
val closedSet = mutableSetOf<Location>()
while (openSet.isNotEmpty()) {
    val node = openSet.remove()
    if (node.location == Location(maxRow, maxCol)) {
        println(node.cost)
        break
    } else {
        closedSet.add(node.location)
        val neighbours = neighbours(node, map)
        for (neighbour in neighbours) {
            if (neighbour.location in closedSet) continue
            val existingNode =
                openSet.find { it.location == neighbour.location && it.direction == neighbour.direction }
            if (existingNode == null || existingNode.cost > neighbour.cost) {
                openSet.remove(existingNode)
                openSet.add(neighbour)
            }
        }
    }
}
