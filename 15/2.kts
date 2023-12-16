import java.io.File

fun hash(s: String): Int {
    var result = 0
    for (c in s) {
        result = ((result + c.code) * 17) % 256
    }
    return result
}

data class Lens(
    val name: String,
    var focalLength: Int,
)

val boxes = (0..255).associateWith { mutableListOf<Lens>() }

File("input").readText().trim().split(",").forEach { step ->
    if (step.endsWith('-')) {
        val name = step.dropLast(1)
        boxes[hash(name)]?.removeIf { it.name == name }
    } else {
        val (name, focalLength) = step.split("=")
        val lenses = boxes[hash(name)]!!
        if (lenses.any { it.name == name }) {
            lenses.single { it.name == name }.focalLength = focalLength.toInt()
        } else {
            lenses.add(Lens(name, focalLength.toInt()))
        }
    }
}

val result = boxes.map { (nb, lenses) ->
    lenses.mapIndexed { i, lens ->
        (1 + nb) * (1 + i) * lens.focalLength
    }.sum()
}.sum()
println(result)