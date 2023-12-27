import java.io.File
import kotlin.math.abs

data class Location(val x: Int, val y: Int)

var lastCube = Location(0, 0)
val cubes = mutableListOf(lastCube)
File("input").readLines().forEach { line ->
    val (directionStr, amountStr) = line.split(" ").take(2)
    val amount = amountStr.toInt()
    lastCube = with(lastCube) {
        when (directionStr) {
            "U" -> Location(x, y + amount)
            "D" -> Location(x, y - amount)
            "L" -> Location(x - amount, y)
            "R" -> Location(x + amount, y)
            else -> throw Exception("Unknown direction: $directionStr")
        }
    }
    cubes.add(lastCube)
}

fun area(path: List<Location>): Int {
    var area = 0
    for (i in path.indices) {
        val j = (i + 1) % path.size
        area += path[i].x * path[j].y
        area -= path[j].x * path[i].y
    }
    return abs(area / 2)
}

fun length(path: List<Location>): Int {
    var length = 0
    for (i in path.indices) {
        val j = (i + 1) % path.size
        length += abs(path[i].x - path[j].x) + abs(path[i].y - path[j].y)
    }
    return length
}

println(area(cubes) + length(cubes) / 2 + 1)