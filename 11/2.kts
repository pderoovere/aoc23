import java.io.File
import kotlin.math.abs

data class Galaxy(val initialRow: Int, val initialColumn: Int, var rowOffset: Long = 0, var columnOffset: Long = 0) {
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

val offset = 1_000_000 - 1

val emptyRows = rows.filter { row -> galaxies.none { it.initialRow == row } }
emptyRows.forEach { row ->
    galaxies.filter { it.initialRow > row }.forEach { it.rowOffset += offset }
}
val emptyColumns = columns.filter { column -> galaxies.none { it.initialColumn == column } }
emptyColumns.forEach { column ->
    galaxies.filter { it.initialColumn > column }.forEach { it.columnOffset += offset }
}

val distances = galaxies.flatMapIndexed { index, galaxy ->
    galaxies.drop(index + 1).map { galaxy2 -> manhattanDistance(galaxy, galaxy2) }
}
println(distances.sum())
