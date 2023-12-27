import java.io.File
import kotlin.math.abs

data class Location(val x: Long, val y: Long)

fun convert(hexCode: String): Pair<Int, Long> {
    val amount = hexCode.take(5).toLong(16)
    val direction = hexCode[5].toString().toInt()
    return direction to amount
}

val regex = "\\(#(.*?)\\)".toRegex()
var lastCube = Location(0, 0)
val cubes = mutableListOf(lastCube)
File("input").readLines().forEach { line ->
    val (direction, amount) = convert(regex.find(line)!!.groupValues[1])
    lastCube = with(lastCube) {
        when (direction) {
            3 -> Location(x, y + amount)
            1 -> Location(x, y - amount)
            2 -> Location(x - amount, y)
            0 -> Location(x + amount, y)
            else -> throw Exception("Unknown direction: $direction")
        }
    }
    cubes.add(lastCube)
}

fun area(path: List<Location>): Long {
    var area = 0L
    for (i in path.indices) {
        val j = (i + 1) % path.size
        area += path[i].x * path[j].y
        area -= path[j].x * path[i].y
    }
    return abs(area / 2)
}

fun length(path: List<Location>): Long {
    var length = 0L
    for (i in path.indices) {
        val j = (i + 1) % path.size
        length += abs(path[i].x - path[j].x) + abs(path[i].y - path[j].y)
    }
    return length
}

println(area(cubes) + length(cubes) / 2 + 1)