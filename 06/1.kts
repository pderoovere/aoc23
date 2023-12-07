import java.io.File

val spacesRegex = "\\s+".toRegex()
val lines = File("input").readLines()
val times = lines[0].split(spacesRegex).drop(1).map(String::toInt)
val distances = lines[1].split(spacesRegex).drop(1).map(String::toInt)
val races = times.zip(distances)

fun calculateDistance(totalTime: Int, waitingTime: Int) = waitingTime * (totalTime - waitingTime)

races.map { (time, distance) ->
    (1..time).count { calculateDistance(time, it) > distance }
}.reduce(Int::times).let(::println)