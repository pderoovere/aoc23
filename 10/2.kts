import _2.Path
import java.io.File

data class Location(val row: Int, val column: Int) {
    val north get() = Location(row - 1, column)
    val south get() = Location(row + 1, column)
    val east get() = Location(row, column + 1)
    val west get() = Location(row, column - 1)
    val neighbours get() = listOf(west, south, east, north)
}

fun next(from: Location, option1: Location, option2: Location) =
    when (from) {
        option1 -> option2
        option2 -> option1
        else -> null
    }

fun neighbour(before: Cell, current: Cell) = with(current.location) {
    when (current.char) {
        '|' -> next(before.location, north, south)
        '-' -> next(before.location, east, west)
        'L' -> next(before.location, north, east)
        'J' -> next(before.location, north, west)
        '7' -> next(before.location, south, west)
        'F' -> next(before.location, south, east)
        else -> null
    }
}

data class Cell(
    val char: Char,
    val location: Location,
    var onPath: Boolean = false,
    var enclosedLeftRight: Boolean = false,
    var enclosedTopBottom: Boolean = false
)

typealias Path = List<Cell>

data class PathSegment(
    val index: Int,
    val before: Cell,
    val after: Cell,
    val parts: List<Cell>,
) {
    val cells = listOf(before, after) + parts
    val rows = cells.map { it.location.row }.toSet()
    val columns = cells.map { it.location.column }.toSet()
    fun crossesRow(row: Int) = rows.containsAll(setOf(row - 1, row, row + 1))
    fun crossesColumn(column: Int) = columns.containsAll(setOf(column - 1, column, column + 1))
}

tailrec fun travel(
    grid: List<Cell>,
    from: Cell,
    to: Cell,
    path: Path
): Path {
    to.onPath = true
    if (to.char == 'S') return path + to
    val next = grid.single { it.location == neighbour(from, to)!! }
    val newPath = path + to
    return travel(grid, to, next, newPath)
}

fun directionChanged(before: Cell, current: Cell, after: Cell): Boolean {
    val result = if (before.location.row == current.location.row) {
        after.location.row != current.location.row
    } else {
        after.location.column != current.location.column
    }
    return result
}

fun split(path: Path): List<PathSegment> {
    // Split path into segments
    val currentSegments = mutableListOf<Cell>()
    val segments = mutableListOf<List<Cell>>()
    for ((i, cell) in path.withIndex()) {
        currentSegments.add(cell)
        if (i == 0) {
            continue
        }
        val before = path[i - 1]
        val after = path.getOrNull(i + 1)
        if (after == null || directionChanged(before, cell, after)) {
            // Direction change after this cell
            segments.add(currentSegments.toList())
            currentSegments.clear()
            currentSegments.add(cell)
        }
    }
    // Join first and last segment if they are in the same direction
    val first = segments.first()
    val last = segments.last()
    if (last.size == 1 || first.size == 1 || !directionChanged(last.last(), first.first(), first[1])) {
        segments.removeAt(segments.size - 1)
        segments.removeAt(0)
        segments.add(last + first)
    }
    // Convert segments to PathSegments
    return segments.mapIndexed { index, cells ->
        val firstIndex = path.indexOf(cells.first())
        val lastIndex = path.indexOf(cells.last())
        val before = path[if (firstIndex == 0) path.size - 1 else firstIndex - 1]
        val after = path[(lastIndex + 1) % path.size]
        PathSegment(index, before, after, cells)
    }
}

fun indicesBetween(segment1: PathSegment, segment2: PathSegment, axis: (Cell) -> Int): IntRange {
    val segment1Min = segment1.parts.minOf(axis)
    val segment1Max = segment1.parts.maxOf(axis)
    val segment2Min = segment2.parts.minOf(axis)
    val segment2Max = segment2.parts.maxOf(axis)
    return if (segment1Min < segment2Min) {
        segment1Max + 1..<segment2Min
    } else {
        segment2Max + 1..<segment1Min
    }
}

val lines = File("input").readLines()
val grid = lines.mapIndexed { row, line ->
    line.mapIndexed { column, char -> Cell(char, Location(row, column)) }
}.flatten()
val startingCell = grid.single { it.char == 'S' }
val nextCell = grid.single { it.location == startingCell.location.west } // We know this from part 1
val path = travel(grid, startingCell, nextCell, listOf(startingCell))
val segments = split(path)

val rows = grid.map { it.location.row }.toSet()
val columns = grid.map { it.location.column }.toSet()

for (row in rows) {
    val relevantSegments = segments.filter { it.crossesRow(row) }.sortedBy { it.index }
    relevantSegments.chunked(2).forEach { s ->
        if (s.size == 2) {
            val (segment1, segment2) = s
            val indices = indicesBetween(segment1, segment2) { it.location.column }
            indices.forEach { column ->
                val cell = grid.single { it.location == Location(row, column) }
                cell.enclosedLeftRight = !cell.enclosedLeftRight
            }
        }
    }
}

for (column in columns) {
    val relevantSegments = segments.filter { it.crossesColumn(column) }.sortedBy { it.index }
    relevantSegments.chunked(2).forEach { s ->
        if (s.size == 2) {
            val (segment1, segment2) = s
            val indices = indicesBetween(segment1, segment2) { it.location.row }
            indices.forEach { row ->
                val cell = grid.single { it.location == Location(row, column) }
                cell.enclosedTopBottom = !cell.enclosedTopBottom
            }
        }
    }
}

println("Result: ${grid.count { it.enclosedLeftRight && it.enclosedTopBottom && !it.onPath }}")