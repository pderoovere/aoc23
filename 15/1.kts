import java.io.File

fun hash(s: String): Int {
    var result = 0
    for (c in s) {
        result = ((result + c.code) * 17) % 256
    }
    return result
}

println(File("input").readText().trim().split(",").map { hash(it) }.sum())