import java.io.File
import kotlin.math.max
import kotlin.math.min

val lines = File("input").readLines()
val numericRegex = "\\D+".toRegex()
val time = lines[0].replace(numericRegex, "").toLong()
val distance = lines[1].replace(numericRegex, "").toLong()

fun calculateDistance(totalTime: Long, waitingTime: Long) = waitingTime * (totalTime - waitingTime)

enum class LimitType {
    LOW, HIGH
}

// Modified binary search
fun findLimit(low: Long, high: Long, limitType: LimitType, predicate: (Long) -> Boolean): Long {
    var low = low
    var high = high
    var result: Long? = null
    while (low <= high) {
        val mid = low + (high - low) / 2
        if (predicate(mid)) {
            result = mid
            if (limitType == LimitType.LOW) {
                high = mid - 1
            } else {
                low = mid + 1
            }
        } else {
            if (limitType == LimitType.LOW) {
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
    }
    return result!!
}

val predicate = { waitingTime: Long -> calculateDistance(time, waitingTime) > distance }
val l1 = findLimit(0, time, LimitType.LOW, predicate)
val l2 = findLimit(0, time, LimitType.HIGH, predicate)
println(max(l1, l2) - min(l1, l2) + 1)
