import java.io.File
import kotlin.math.max
import kotlin.math.min

data class ClosedRange(val start: Long, val endInclusive: Long) {

    fun offset(offset: Long) = ClosedRange(start + offset, endInclusive + offset)

    fun intersection(range: ClosedRange): ClosedRange? {
        val start = max(this.start, range.start)
        val end = min(this.endInclusive, range.endInclusive)
        return if (start <= end) {
            ClosedRange(start, end)
        } else {
            null
        }
    }

    fun difference(range: ClosedRange): Set<ClosedRange> {
        val intersection = intersection(range)
        return if (intersection == null) {
            setOf(this)
        } else {
            val differences = mutableSetOf<ClosedRange>()
            if (start < intersection.start) {
                differences.add(ClosedRange(start, intersection.start - 1))
            }
            if (intersection.endInclusive < endInclusive) {
                differences.add(ClosedRange(intersection.endInclusive + 1, endInclusive))
            }
            differences
        }
    }

}

data class ClosedRangeMapping(val range: ClosedRange, val offset: Long) {

    constructor(sourceStart: Long, length: Long, destinationStart: Long) : this(
        ClosedRange(
            sourceStart,
            sourceStart + length - 1
        ), destinationStart - sourceStart
    )
}

val lines = File("input").readText().dropLastWhile { it == '\n' }
val blocks = lines.split("\n\n").map { it.split("\n") }

val seeds = blocks[0][0].split(":")[1].trim().split(" ").map(String::toLong).chunked(2).map {
    ClosedRange(it[0], it[0] + it[1] - 1)
}.toSet()
val mappings = blocks.drop(1).map { map ->
    map.drop(1).map {
        val (destinationRangeStart, sourceRangeStart, rangeLength) = it.split(" ").take(3).map(String::toLong)
        ClosedRangeMapping(sourceRangeStart, rangeLength, destinationRangeStart)
    }.toSet()
}

var result = seeds.toMutableList()
mappings.forEach { mapping ->
    val toResolve = result.toMutableList()
    result.clear()
    while (toResolve.isNotEmpty()) {
        val r = toResolve.removeAt(0)
        var resolved = false
        for (m in mapping) {
            val intersection = r.intersection(m.range)
            if (intersection != null) {
                resolved = true
                toResolve.addAll(r.difference(intersection))
                result.add(intersection.offset(m.offset))
            }
        }
        if (!resolved) {
            result.add(r)
        }
    }
}

println(result.minOf { it.start })