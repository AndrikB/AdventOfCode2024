package day10


import java.awt.Point
import java.io.File

fun main() {
    val map = parseFile()

    val trailHeadStats = map.mapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '0') {
                calculateTrailHeads(map, Point(x, y))
            } else null
        }
    }.flatten()

    println("scores ${trailHeadStats.sumOf { it.first }}")
    println("ratings ${trailHeadStats.sumOf { it.second }}")
}

fun calculateTrailHeads(map: List<String>, trailhead: Point): Pair<Int, Int> {
    val listOfHills = getAllAvailableHill(map, -1, trailhead)
    return listOfHills.distinct().size to listOfHills.size
}

val directions = listOf((-1 to 0), (1 to 0), (0 to -1), (0 to 1))
fun getAllAvailableHill(map: List<String>, prevValue: Int, currentPoint: Point): List<Point> {
    val current = map.getOrNull(currentPoint.y)?.getOrNull(currentPoint.x)?.digitToIntOrNull()
    if (prevValue + 1 != current) return emptyList()

    if (current == 9) return listOf(currentPoint)

    return directions.flatMap { (dx, dy) ->
        getAllAvailableHill(map, current, currentPoint.location.also { p -> p.translate(dx, dy) })
    }
}

fun parseFile(): List<String> {
    return File("src/main/kotlin/day10/input.txt").readLines()
}
