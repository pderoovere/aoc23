import java.io.File

data class Range(val destinationRangeStart: Long, val sourceRangeStart: Long, val rangeLength: Long)
data class Mapping(val ranges: List<Range>) {
    fun map(index: Long): Long {
        val range = ranges.find { it.sourceRangeStart <= index && index < it.sourceRangeStart + it.rangeLength }
        return if (range != null) {
            range.destinationRangeStart + index - range.sourceRangeStart
        } else {
            index
        }
    }
}

val lines = File("input").readText().dropLastWhile { it == '\n' }
val blocks = lines.split("\n\n").map { it.split("\n") }

val seeds = blocks[0][0].split(":")[1].trim().split(" ").map(String::toLong)
val maps = blocks.drop(1).map { map ->
    val ranges = map.drop(1).map {
        val (destinationRangeStart, sourceRangeStart, rangeLength) = it.split(" ").take(3).map(String::toLong)
        Range(destinationRangeStart, sourceRangeStart, rangeLength)
    }
    Mapping(ranges)
}

val result = seeds.minOf { seed ->
    val mapped = maps.fold(seed) { acc, map ->
        map.map(acc)
    }
    mapped
}
println(result)