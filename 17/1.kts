import java.io.File
import java.util.*

data class Location(val row: Int, val col: Int) {
    operator fun minus(other: Location) = Location(row - other.row, col - other.col)
    operator fun plus(other: Location) = Location(row + other.row, col + other.col)
    operator fun unaryMinus() = Location(-row, -col)
    val up get() = this + Location(-1, 0)
    val down get() = this + Location(1, 0)
    val left get() = this + Location(0, -1)
    val right get() = this + Location(0, 1)
    val neighbours get() = listOf(up, down, left, right)
}

data class Node(
    val location: Location,
    val direction: Location,
    val stepsInDirection: Int,
) {
    var cost = 0 // Exclude from equals and hashcode

    constructor(location: Location, direction: Location, stepsInDirection: Int, cost: Int) : this(
        location,
        direction,
        stepsInDirection
    ) {
        this.cost = cost
    }
}

fun neighbours(node: Node, map: Map<Location, Int>): List<Node> {
    val result = node.location.neighbours.filter { it in map }.map {
        val stepsInDirection = if (it == node.location + node.direction) node.stepsInDirection + 1 else 1
        Node(it, it - node.location, stepsInDirection, node.cost + map[it]!!)
    }
    return result.filter { it.stepsInDirection <= 3 && it.direction != -node.direction }
}

val map = File("input").readLines().mapIndexed { row, line ->
    line.mapIndexedNotNull { col, c ->
        Location(row, col) to c.toString().toInt()
    }
}.flatten().toMap()
val maxRow = map.keys.maxByOrNull { it.row }!!.row
val maxCol = map.keys.maxByOrNull { it.col }!!.col

val startingNodes =
    listOf(
        Node(Location(1, 0), Location(1, 0), 1, map[Location(1, 0)]!!),
        Node(Location(0, 1), Location(0, 1), 1, map[Location(0, 1)]!!)
    )
val openSet = PriorityQueue<Node>(compareBy { it.cost })
openSet.addAll(startingNodes)
val closedSet = mutableSetOf<Node>()
while (openSet.isNotEmpty()) {
    val node = openSet.poll()
    if (node.location == Location(maxRow, maxCol)) {
        println(node.cost)
        break
    } else {
        closedSet.add(node)
        val neighbours = neighbours(node, map)
        for (neighbour in neighbours) {
            if (neighbour in closedSet) continue
            val existingNode = openSet.find { it == neighbour }
            if (existingNode == null || existingNode.cost > neighbour.cost) {
                openSet.remove(existingNode)
                openSet.add(neighbour)
            }
        }
    }
}