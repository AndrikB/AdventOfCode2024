package day08


import java.awt.Point
import java.io.File

fun main() {
    val matrix = parseFile()

    val tableOfAntennas = mutableMapOf<Char, List<Point>>()

    matrix.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            if (c != '.') {
                val before = tableOfAntennas.getOrDefault(c, emptyList())
                tableOfAntennas[c] = before + Point(x, y)
            }
        }
    }

    val antiNodeMatrix = matrix.map { row -> row.map { false }.toMutableList() }
    val antiNodeMatrix2 = matrix.map { row -> row.map { false }.toMutableList() }
    tableOfAntennas.forEach { (_, points) ->
        points.flatMap { a -> points.mapNotNull { b -> if (a != b) a to b else null } }
            .forEach { (pointA, pointB) ->
                antiNodeMatrix.getOrNull(pointA.y * 2 - pointB.y)?.setTrueIfAccessible(pointA.x * 2 - pointB.x)

                for (i in -50..50) {
                    antiNodeMatrix2.getOrNull(pointA.y + i * (pointA.y - pointB.y))
                        ?.setTrueIfAccessible(pointA.x + i * (pointA.x - pointB.x))
                }
            }
    }
    antiNodeMatrix.sumOf { row -> row.count { it } }.also { println(it) }
    antiNodeMatrix2.sumOf { row -> row.count { it } }.also { println(it) }

}

private fun MutableList<Boolean>.setTrueIfAccessible(index: Int) {
    if (index in 0 until size)
        set(index, true)
}


fun parseFile(): List<String> {
    return File("src/main/kotlin/day08/input.txt").readLines()
}
