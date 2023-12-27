import java.io.File
import kotlin.math.roundToLong
import kotlin.math.sqrt

data class Vector3(val x: Double, val y: Double, val z: Double) {
    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Double) = Vector3(x * scalar, y * scalar, z * scalar)
    operator fun div(scalar: Double) = Vector3(x / scalar, y / scalar, z / scalar)
    fun norm() = sqrt(x * x + y * y + z * z)
    fun squaredNorm() = x * x + y * y + z * z
    fun clip(threshold: Double): Vector3 {
        val norm = norm()
        return if (norm > threshold) this * (threshold / norm) else this
    }

    val round get() = Vector3(x.roundToLong().toDouble(), y.roundToLong().toDouble(), z.roundToLong().toDouble())
}

data class Trajectory(val position: Vector3, val velocity: Vector3)

fun calculateClosestApproach(t1: Trajectory, t2: Trajectory): Double? {
    val deltaP = t1.position - t2.position
    val deltaV = t1.velocity - t2.velocity
    val numerator = deltaP.x * deltaV.x + deltaP.y * deltaV.y + deltaP.z * deltaV.z
    val denominator = deltaV.x * deltaV.x + deltaV.y * deltaV.y + deltaV.z * deltaV.z
    // Parallel trajectories return null
    val time = if (denominator > 1e-9) -numerator / denominator else return null
    val p1 = t1.position + t1.velocity * time
    val p2 = t2.position + t2.velocity * time
    return (p1 - p2).norm()
}

fun loss(t: Trajectory, targets: List<Trajectory>): Double {
    // Ignoring parallel trajectories
    return targets.mapNotNull { calculateClosestApproach(t, it) }.sum()
}

fun gradient(t: Trajectory, targets: List<Trajectory>, epsilon: Double): Trajectory {
    val currentLoss = loss(t, targets)
    val gradPosX = (loss(t.copy(position = t.position + Vector3(epsilon, 0.0, 0.0)), targets) - currentLoss) / epsilon
    val gradPosY = (loss(t.copy(position = t.position + Vector3(0.0, epsilon, 0.0)), targets) - currentLoss) / epsilon
    val gradPosZ = (loss(t.copy(position = t.position + Vector3(0.0, 0.0, epsilon)), targets) - currentLoss) / epsilon
    val gradVelX = (loss(t.copy(velocity = t.velocity + Vector3(epsilon, 0.0, 0.0)), targets) - currentLoss) / epsilon
    val gradVelY = (loss(t.copy(velocity = t.velocity + Vector3(0.0, epsilon, 0.0)), targets) - currentLoss) / epsilon
    val gradVelZ = (loss(t.copy(velocity = t.velocity + Vector3(0.0, 0.0, epsilon)), targets) - currentLoss) / epsilon
    return Trajectory(
        position = Vector3(gradPosX, gradPosY, gradPosZ).clip(1.0),
        velocity = Vector3(gradVelX, gradVelY, gradVelZ).clip(1.0)
    )
}

fun optimize(
    t: Trajectory,
    targets: List<Trajectory>,
    epsilon: Double,
    learningRate: Double,
    iterations: Int,
    velocity: Boolean,
    lossThreshold: Double
): Trajectory {
    var current = t
    var lr = learningRate
    var e = epsilon
    for (i in 0 until iterations) {
        val grad = gradient(current, targets, e)
        current = Trajectory(
            position = current.position - grad.position * lr,
            if (velocity) current.velocity - grad.velocity * lr else current.velocity
        )
        val loss = loss(current, targets)
        if (loss < lossThreshold) return current
        if (i % 50_000 == 0) {
            println("Iteration $i: $loss")
        }
        if (i % 50_000 == 0 && e > 5e-16) {
            e *= 0.9
        }
        if (i % 50_000 == 0) {
            lr *= 0.9
        }
    }
    return current
}

val trajectories = File("input").readLines().map { line ->
    val (positionString, velocityString) = line.split(" @ ")
    val (px, py, pz) = positionString.split(",").take(3).map { it.trim().toDouble() }
    val (dx, dy, dz) = velocityString.split(",").take(3).map { it.trim().toDouble() }
    Trajectory(
        Vector3(px, py, pz),
        Vector3(dx, dy, dz)
    )
}

val avgPositionNorm = trajectories.map { it.position.norm() }.average()
val trajectoriesWithNormalizedPositions = trajectories.map { it.copy(position = it.position / avgPositionNorm) }
val t = Trajectory(position = Vector3(0.0, 0.0, 0.0), velocity = Vector3(0.0, 0.0, 0.0))
val optimized = optimize(t, trajectoriesWithNormalizedPositions, 1e-9, 1e-2, 2_000_000, true, 1e-1)
val velocity = optimized.velocity.round
println("Continuing with fixed velocity: $velocity")
val optimized2 = optimize(
    optimized.copy(velocity = velocity),
    trajectoriesWithNormalizedPositions,
    5e-16,
    1e-3,
    15_000_000, false, 1e-14
)
val optimizedPos = optimized2.position * avgPositionNorm
println("Optimized position: $optimizedPos")
println("Optimized velocity: ${optimized2.velocity}")
val result = optimizedPos.x + optimizedPos.y + optimizedPos.z
println("Result: ${result.roundToLong()}")