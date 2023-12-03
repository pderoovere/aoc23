import java.io.File

data class Cubes(val blue: Int, val red: Int, val green: Int)
data class Game(val id: Int, val cubes: List<Cubes>) {
    val minCubes = Cubes(
        cubes.maxOfOrNull(Cubes::blue) ?: 0,
        cubes.maxOfOrNull(Cubes::red) ?: 0,
        cubes.maxOfOrNull(Cubes::green) ?: 0
    )
}

fun parseCubes(string: String): Cubes {
    var (blue, red, green) = listOf(0, 0, 0)
    string.split(",").forEach { cube ->
        val (amountStr, color) = cube.trim().split(" ")
        val amount = amountStr.toInt()
        when (color) {
            "blue" -> blue = amount
            "red" -> red = amount
            "green" -> green = amount
        }
    }
    return Cubes(blue, red, green)
}

fun parseGame(string: String): Game {
    val (indexStr, showStrings) = string.split(":")
    val index = indexStr.split(" ")[1].toInt()
    val shows = showStrings.split(";").map(::parseCubes)
    return Game(index, shows)
}

val lines = File("input").readLines()
val games = lines.map(::parseGame)
println(games.sumOf { game -> game.minCubes.let { it.blue * it.red * it.green } })