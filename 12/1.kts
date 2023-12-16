import java.io.File

fun options(record: String, sizes: List<Int>): List<String>? {
    if (record.none { it == '#' } && sizes.isEmpty()) {
        return listOf("")
    }
    if (record.all { it == '.' } || sizes.isEmpty()) {
        return null
    }

    val size = sizes[0]
    return when (val char = record.first()) {
        '.' -> options(record.drop(1), sizes)?.map { "$char$it" }
        '#' -> {
            if (record.takeWhile { it != '.' }.length < size) {
                null
            } else {
                if (record.length > size) {
                    if (record[size] == '#') {
                        return null
                    } else {
                        val newRecord = "." + record.drop(size + 1)
                        options(newRecord, sizes.drop(1))?.map { "${"#".repeat(size)}$it" }
                    }
                } else {
                    options(record.drop(size), sizes.drop(1))?.map { "${"#".repeat(size)}$it" }
                }
            }
        }

        '?' -> {
            val o1 = options(record.replaceFirst('?', '#'), sizes)
            val o2 = options(record.replaceFirst('?', '.'), sizes)
            if (o1 == null && o2 == null) {
                null
            } else {
                (o1 ?: emptyList()) + (o2 ?: emptyList())
            }
        }

        else -> throw IllegalArgumentException("Unexpected character: $char")
    }

}

val results = File("input").readLines().map { line ->
    val (record, sizesString) = line.split(" ")
    val sizes = sizesString.split(",").map { it.toInt() }
    options(record, sizes)!!.size
}

println(results.sum())
