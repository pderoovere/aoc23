import java.io.File
import kotlin.math.sqrt

data class Vector2D(val x: Double, val y: Double) {
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun times(scalar: Double) = Vector2D(x * scalar, y * scalar)
    fun distance(other: Vector2D) = sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y))
    fun inRange(min: Double, max: Double) = x in min..max && y in min..max
}

data class Hailstone(
    val position: Vector2D,
    val velocity: Vector2D
)

val hailstones = File("input").readLines().map { line ->
    val (positionString, velocityString) = line.split(" @ ")
    val (positionX, positionY) = positionString.split(",").take(2).map { it.trim().toDouble() }
    val (velocityX, velocityY) = velocityString.split(",").take(2).map { it.trim().toDouble() }
    Hailstone(
        position = Vector2D(positionX, positionY),
        velocity = Vector2D(velocityX, velocityY)
    )
}

fun pathIntersection(h1: Hailstone, h2: Hailstone): Vector2D? {
    val denominator = h2.velocity.x * h1.velocity.y - h2.velocity.y * h1.velocity.x

    if (denominator == 0.0) {
        // Parallel
        return null
    }

    val nominator =
        h2.position.y * h1.velocity.x + h1.position.x * h1.velocity.y - h1.position.y * h1.velocity.x - h2.position.x * h1.velocity.y
    val t2 = nominator / denominator
    val t1 = (h2.position.x + h2.velocity.x * t2 - h1.position.x) / h1.velocity.x
    if (t2 < 0 || t1 < 0) {
        // Intersection is in the past
        return null
    }
    val i1 = h1.position + h1.velocity * t1
    return i1
}

var result = 0
for (i in hailstones.indices) {
    for (j in i + 1..<hailstones.size) {
        val intersection = pathIntersection(hailstones[i], hailstones[j])
        if (intersection != null && intersection.inRange(200000000000000.0, 400000000000000.0)) {
            result++
        }
    }
}
println(result)