import java.io.File
import kotlin.math.abs

data class Galaxy(val initialRow: Int, val initialColumn: Int, var rowOffset: Int = 0, var columnOffset: Int = 0) {
    val row get() = initialRow + rowOffset
    val column get() = initialColumn + columnOffset
}

fun manhattanDistance(from: Galaxy, to: Galaxy) = abs(from.row - to.row) + abs(from.column - to.column)

val lines = File("input").readLines()
val rows = lines.indices
val columns = lines.first().indices
val galaxies = mutableListOf<Galaxy>()
for (row in rows) {
    for (column in columns) {
        if (lines[row][column] == '#') {
            galaxies.add(Galaxy(row, column))
        }
    }
}

val emptyRows = rows.filter { row -> galaxies.none { it.initialRow == row } }
emptyRows.forEach { row ->
    galaxies.filter { it.initialRow > row }.forEach { it.rowOffset++ }
}
val emptyColumns = columns.filter { column -> galaxies.none { it.initialColumn == column } }
emptyColumns.forEach { column ->
    galaxies.filter { it.initialColumn > column }.forEach { it.columnOffset++ }
}

val distances = galaxies.flatMapIndexed { index, galaxy ->
    galaxies.drop(index + 1).map { galaxy2 -> manhattanDistance(galaxy, galaxy2) }
}
println(distances.sum())
