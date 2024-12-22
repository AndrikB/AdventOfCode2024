package day18

import java.awt.Point
import java.io.File

typealias Matrix = List<MutableList<Int>>

fun main() {
    val bytes = parseFile()

    println(findNumberStepToExit(bytes.take(1024)))

    val count = findNumberCanBeDeleted(bytes)
    println(count)
    println(bytes[count])
}

fun findNumberCanBeDeleted(bytes: List<Point>): Int {
    var start = 0
    var end = bytes.size
    while (end - start > 1) {
        val current = (end + start) / 2
        val passExists = findNumberStepToExit(bytes.take(current)) != null
        if (passExists) {
            start = current
        } else {
            end = current
        }
    }

    return start
}

val endPoint = Point(70, 70)
fun findNumberStepToExit(bytes: List<Point>): Int? {
    val matrix = List(71) { MutableList(71) { 0 } }
    bytes.forEach {
        matrix[it.y][it.x] = -1
    }

    val queue = ArrayDeque(listOf(Point(0, 0)))
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val currentValue = matrix.get(current)!!

        for (direction in directions) {
            val cell = current.next(direction)
            val cellValue = matrix.get(cell)

            if (cell == endPoint) {
                return currentValue + 1
            }
            if (cellValue == 0) {
                matrix.set(cell, currentValue + 1)
                queue.addLast(cell)
            }
        }
    }

    return null
}

val directions = listOf((-1 to 0), (1 to 0), (0 to -1), (0 to 1))
fun Point.next(direction: Pair<Int, Int>): Point =
    this.location.also { it.translate(direction.first, direction.second) }

fun Matrix.get(point: Point): Int? = this.getOrNull(point.y)?.getOrNull(point.x)
fun Matrix.set(point: Point, value: Int): Int? = this.getOrNull(point.y)?.set(point.x, value)

fun parseFile(): List<Point> {
    return File("src/main/kotlin/day18/input.txt").readLines().map { line ->
        with(line.split(',').map { it.toInt() }) {
            Point(get(0), get(1))
        }
    }
}
