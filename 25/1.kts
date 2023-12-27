import java.io.File

data class Edge(val node1: String, val node2: String) {
    init {
        require(node1 < node2)
    }
}

fun createEdge(node1: String, node2: String): Edge {
    return if (node1 < node2) Edge(node1, node2) else Edge(node2, node1)
}

val nodes = mutableSetOf<String>()
val edges = mutableListOf<Edge>()
File("input").readLines().forEach { line ->
    val (from, toStr) = line.split(": ")
    nodes.add(from)
    toStr.split(" ").forEach { to ->
        nodes.add(to)
        edges.add(createEdge(from, to))
    }
}

fun createGraph(edges: List<Edge>): Map<String, List<String>> {
    val graph = mutableMapOf<String, MutableList<String>>()
    for (edge in edges) {
        graph.computeIfAbsent(edge.node1) { mutableListOf() }.add(edge.node2)
        graph.computeIfAbsent(edge.node2) { mutableListOf() }.add(edge.node1)
    }
    return graph
}

fun bfs(graph: Map<String, List<String>>, start: String, end: String): List<String> {
    val visited = mutableSetOf<String>()
    val queue = ArrayDeque<Pair<String, List<String>>>()
    queue.add(start to listOf(start))

    while (queue.isNotEmpty()) {
        val (current, path) = queue.removeFirst()
        if (current == end) {
            return path
        }

        if (!visited.contains(current)) {
            visited.add(current)
            val neighbors = graph[current] ?: emptyList()
            for (neighbor in neighbors) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor to path + neighbor)
                }
            }
        }
    }

    return emptyList() // Return an empty list if no path is found
}

fun subGraphNodes(graph: Map<String, List<String>>, start: String): Set<String> {
    val visited = mutableSetOf<String>()
    val queue = ArrayDeque<String>()
    queue.add(start)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (!visited.contains(current)) {
            visited.add(current)
            val neighbors = graph[current] ?: emptyList()
            for (neighbor in neighbors) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor)
                }
            }
        }
    }

    return visited
}


fun registerVisit(from: String, to: String) {
    val edge = createEdge(from, to)
    visits[edge] = (visits[edge] ?: 0) + 1
}

val graph = createGraph(edges)
val visits = mutableMapOf<Edge, Int>()

fun travelBetweenRandomNodes() {
    val from = nodes.random()
    val to = nodes.random()
    val path = bfs(graph, from, to)
    for (i in 0 until path.size - 1) {
        registerVisit(path[i], path[i + 1])
    }
}

repeat(1000) {
    travelBetweenRandomNodes()
}

val sortedVisits = visits.entries.sortedByDescending { it.value }
val top3 = sortedVisits.take(3)
val newEdges = edges - top3.map { createEdge(it.key.node1, it.key.node2) }.toSet()
val newGraph = createGraph(newEdges)
val subGraphLength = subGraphNodes(newGraph, top3.first().key.node1).size
val result = subGraphLength * (nodes.size - subGraphLength)
println("Result: $result")
