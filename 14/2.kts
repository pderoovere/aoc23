import java.io.File

data class Location(val row: Int, val col: Int)

val cubeRocks = mutableSetOf<Location>()
val roundRocks = mutableSetOf<Location>()
val lines = File("input").readLines()
val nbRows = lines.size
val nbCols = lines[0].length
lines.forEachIndexed { row, line ->
    line.forEachIndexed { col, char ->
        when (char) {
            'O' -> roundRocks.add(Location(row, col))
            '#' -> cubeRocks.add(Location(row, col))
        }
    }
}

enum class Direction {
    NORTH, SOUTH, EAST, WEST
}

fun tiltNorth(roundRocks: Set<Location>, cubeRocks: Set<Location>): Set<Location> {
    val result = mutableSetOf<Location>()
    (0..<nbCols).forEach { col ->
        val prevColRoundRocks = roundRocks.filter { it.col == col }
        val colCubeRocks = cubeRocks.filter { it.col == col }
        val newColRoundRocks = mutableSetOf<Location>()
        (0..<nbRows).forEach { row ->
            val location = Location(row, col)
            val relevantRocks = colCubeRocks.filter { it.row < row } + newColRoundRocks
            if (location in prevColRoundRocks) {
                val newRow = relevantRocks.maxOfOrNull { it.row }?.let { it + 1 } ?: 0
                newColRoundRocks.add(Location(newRow, col))
            }
        }
        result.addAll(newColRoundRocks)
    }
    return result
}

fun tiltWest(roundRocks: Set<Location>, cubeRocks: Set<Location>): Set<Location> {
    val result = mutableSetOf<Location>()
    (0..<nbRows).forEach { row ->
        val prevRowRoundRocks = roundRocks.filter { it.row == row }
        val rowCubeRocks = cubeRocks.filter { it.row == row }
        val newRowRoundRocks = mutableSetOf<Location>()
        (0..<nbCols).forEach { col ->
            val location = Location(row, col)
            val relevantRocks = rowCubeRocks.filter { it.col < col } + newRowRoundRocks
            if (location in prevRowRoundRocks) {
                val newCol = relevantRocks.maxOfOrNull { it.col }?.let { it + 1 } ?: 0
                newRowRoundRocks.add(Location(row, newCol))
            }
        }
        result.addAll(newRowRoundRocks)
    }
    return result
}

fun tiltSouth(roundRocks: Set<Location>, cubeRocks: Set<Location>): Set<Location> {
    val result = mutableSetOf<Location>()
    (0..<nbCols).forEach { col ->
        val prevColRoundRocks = roundRocks.filter { it.col == col }
        val colCubeRocks = cubeRocks.filter { it.col == col }
        val newColRoundRocks = mutableSetOf<Location>()
        (nbRows - 1 downTo 0).forEach { row ->
            val location = Location(row, col)
            val relevantRocks = colCubeRocks.filter { it.row > row } + newColRoundRocks
            if (location in prevColRoundRocks) {
                val newRow = relevantRocks.minOfOrNull { it.row }?.let { it - 1 } ?: (nbRows - 1)
                newColRoundRocks.add(Location(newRow, col))
            }
        }
        result.addAll(newColRoundRocks)
    }
    return result
}

fun tiltEast(roundRocks: Set<Location>, cubeRocks: Set<Location>): Set<Location> {
    val result = mutableSetOf<Location>()
    (0..<nbRows).forEach { row ->
        val prevColRoundRocks = roundRocks.filter { it.row == row }
        val colCubeRocks = cubeRocks.filter { it.row == row }
        val newColRoundRocks = mutableSetOf<Location>()
        (nbCols - 1 downTo 0).forEach { col ->
            val location = Location(row, col)
            val relevantRocks = colCubeRocks.filter { it.col > col } + newColRoundRocks
            if (location in prevColRoundRocks) {
                val newCol = relevantRocks.minOfOrNull { it.col }?.let { it - 1 } ?: (nbCols - 1)
                newColRoundRocks.add(Location(row, newCol))
            }
        }
        result.addAll(newColRoundRocks)
    }
    return result
}

fun tilt2(roundRocks: Set<Location>, cubeRocks: Set<Location>): Set<Location> {
    var result = roundRocks
    result = tiltNorth(result, cubeRocks)
    result = tiltWest(result, cubeRocks)
    result = tiltSouth(result, cubeRocks)
    result = tiltEast(result, cubeRocks)
    return result
}

var cache = mutableListOf<Set<Location>>()

tailrec fun tilt(roundRocks: Set<Location>, cubeRocks: Set<Location>, amount: Int): Set<Location> {
    if (amount == 0) {
        return roundRocks
    }
    cache.add(roundRocks)
    val updatedRoundRocks = tilt2(roundRocks, cubeRocks)
    if (updatedRoundRocks in cache) {
        val index = cache.indexOf(updatedRoundRocks)
        val cycleList = cache.subList(index, cache.size)
        return cycleList[(amount - 1) % cycleList.size]
    }
    return tilt(updatedRoundRocks, cubeRocks, amount - 1)
}

val result = tilt(roundRocks, cubeRocks, 1_000_000_000)
println(result.sumOf { nbRows - it.row })