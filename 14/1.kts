import java.io.File

data class Location(var row: Int, var col: Int)

val cubeRocks = mutableListOf<Location>()
val roundRocks = mutableListOf<Location>()
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

roundRocks.sortedBy { it.row }.forEach { rock ->
    val relevantCubes = cubeRocks.filter { it.col == rock.col && it.row < rock.row }.maxOfOrNull { it.row }
    val relevantRounds = roundRocks.filter { it.col == rock.col && it.row < rock.row }.maxOfOrNull { it.row }
    rock.row = maxOf(relevantCubes ?: -1, relevantRounds ?: -1) + 1
}
println(roundRocks.sumOf { nbRows - it.row })
