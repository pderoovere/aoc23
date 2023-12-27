import java.io.File

val cache = mutableMapOf<Pair<String, List<Int>>, Long?>()

fun options(record: String, sizes: List<Int>): Long? {
    val key = record to sizes
    if (key in cache) {
        return cache[key]
    }

    val result = if (record.none { it == '#' } && sizes.isEmpty()) {
        1
    } else if (record.all { it == '.' } || sizes.isEmpty()) {
        null
    } else {
        val size = sizes[0]
        when (val char = record.first()) {
            '.' -> options(record.drop(1), sizes)
            '#' -> {
                if (record.takeWhile { it != '.' }.length < size) {
                    null
                } else {
                    if (record.length > size) {
                        if (record[size] == '#') {
                            null
                        } else {
                            val newRecord = "." + record.drop(size + 1)
                            options(newRecord, sizes.drop(1))
                        }
                    } else {
                        options(record.drop(size), sizes.drop(1))
                    }
                }
            }

            '?' -> {
                val o1 = options(record.replaceFirst('?', '#'), sizes)
                val o2 = options(record.replaceFirst('?', '.'), sizes)
                if (o1 == null && o2 == null) {
                    null
                } else {
                    listOfNotNull(o1, o2).sum()
                }
            }

            else -> throw IllegalArgumentException("Unexpected character: $char")
        }
    }
    cache[key] = result
    return result
}

val results = File("input").readLines().map { line ->
    val (record, sizesString) = line.split(" ")
    val newRecord = (0..<5).joinToString("?") { record }
    val sizes = sizesString.split(",").map { it.toInt() }
    val newSizes = (0..<5).flatMap { sizes }
    options(newRecord, newSizes)!!
}
