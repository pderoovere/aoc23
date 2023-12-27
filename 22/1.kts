import java.io.File

data class Range(val min: Int, val max: Int) {
    private fun contains(value: Int) = value in min..max
    fun intersects(other: Range) =
        contains(other.min) || contains(other.max) || other.contains(min) || other.contains(max)

    operator fun minus(value: Int) = Range(min - value, max - value)
}

data class Coordinate(val x: Int, val y: Int, val z: Int)

data class Brick(val xRange: Range, val yRange: Range, val zRange: Range) {

    constructor(c1: Coordinate, c2: Coordinate) : this(
        Range(minOf(c1.x, c2.x), maxOf(c1.x, c2.x)),
        Range(minOf(c1.y, c2.y), maxOf(c1.y, c2.y)),
        Range(minOf(c1.z, c2.z), maxOf(c1.z, c2.z))
    )

    fun dropUntil(z: Int): Brick {
        val distance = if (zRange.min > z) zRange.min - z else 0
        return Brick(xRange, yRange, zRange - distance)
    }

    fun below(other: Brick) =
        zRange.max <= other.zRange.min && xRange.intersects(other.xRange) && yRange.intersects(other.yRange)

    fun supports(other: Brick) = this.below(other) && zRange.max == other.zRange.min - 1
    fun supportedBy(other: Brick) = other.supports(this)

}

val bricks = File("input").readLines().map { line ->
    val (c1String, c2String) = line.split("~").take(2)
    val (c1x, c1y, c1z) = c1String.split(",").map { it.toInt() }
    val (c2x, c2y, c2z) = c2String.split(",").map { it.toInt() }
    Brick(Coordinate(c1x, c1y, c1z), Coordinate(c2x, c2y, c2z))
}

val droppedBricks = mutableListOf<Brick>()
bricks.sortedBy { it.zRange.min }.forEach { brick ->
    val z = droppedBricks.filter { it.below(brick) }.maxOfOrNull { it.zRange.max }?.let { it + 1 } ?: 1
    droppedBricks.add(brick.dropUntil(z))
}

val supportsMap = droppedBricks.associateWith { brick ->
    droppedBricks.filter { brick.supports(it) }
}
val supportedByMap = droppedBricks.associateWith { brick ->
    droppedBricks.filter { brick.supportedBy(it) }
}
println(droppedBricks.count { brick ->
    val supportingBricks = supportsMap[brick]!!
    supportingBricks.none { supportedByMap[it]!!.size == 1 }
})