import java.io.File

data class Cubes(val blue: Int, val red: Int, val green: Int) {
    fun possible(reference: Cubes): Boolean {
        return (blue <= reference.blue) && (red <= reference.red) && (green <= reference.green)
    }
}

data class Game(val id: Int, val cubes: List<Cubes>)

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

val reference = Cubes(14, 12, 13)
val possibleGames = games.filter { game -> game.cubes.all { show -> show.possible(reference) } }
println(possibleGames.sumOf { it.id })