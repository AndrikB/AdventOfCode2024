package day06

import day06.Cell.*
import day06.Direction.*
import java.awt.Point
import java.io.File

fun main() {
    val (map, guard) = parseFile()
    val afterEmulation = emulateGuard(map.generateAttendanceMap(), guard)
    println(afterEmulation.flatten().count { it > 0 })

    afterEmulation.mapIndexed { y, line ->
        line.filterIndexed { x, cell ->
            cell > 0 && checkHaveLoop(map.generateAttendanceMap().also { it[y][x] = -1 }, guard)
        }.count()
    }.sum().also { println(it) }

}

fun emulateGuard(mapToEmulate: List<MutableList<Int>>, initialGuard: Point): List<List<Int>> {
    var direction = TOP

    val guard = initialGuard.location
    mapToEmulate[guard.y][guard.x]++

    fun nextCelValue(): Int? {
        return mapToEmulate.getOrNull(guard.y + direction.movementY)?.getOrNull(guard.x + direction.movementX)
    }

    do {
        when (nextCelValue()) {
            in 0..4 -> {
                guard.translate(direction.movementX, direction.movementY)
                mapToEmulate[guard.y][guard.x]++
            }

            -1 -> {
                direction = direction.nextClockwise
            }

            null -> {
                return mapToEmulate
            }
        }

    } while (true)
}


fun checkHaveLoop(mapToEmulate: List<MutableList<Int>>, initialGuard: Point): Boolean {
    val guard = initialGuard.location
    mapToEmulate[guard.y][guard.x]++
    var direction = TOP

    fun nextCelValue(): Int? {
        return mapToEmulate.getOrNull(guard.y + direction.movementY)?.getOrNull(guard.x + direction.movementX)
    }

    do {
        when (nextCelValue()) {
            4 -> return true
            in 0..3 -> {
                guard.translate(direction.movementX, direction.movementY)
                mapToEmulate[guard.y][guard.x]++
            }

            -1 -> {
                direction = direction.nextClockwise
            }

            else -> {
                return false
            }
        }

    } while (true)
}

private fun List<List<Cell>>.generateAttendanceMap() =
    this.map { row -> row.map { cell -> cell.cellValue }.toMutableList() }


enum class Direction(val movementX: Int, val movementY: Int) {
    TOP(0, -1),
    RIGHT(1, 0),
    BOTTOM(0, 1),
    LEFT(-1, 0);

    val nextClockwise by lazy {
        val values = values()
        values[(this.ordinal + 1) % values.size]
    }
}

enum class Cell(val cellValue: Int) { OBSTACLE(-1), EMPTY(0); }


fun parseFile(): Pair<List<List<Cell>>, Point> {
    lateinit var guardPosition: Point
    return File("src/main/kotlin/day06/input.txt").readLines().mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            when (c) {
                '^' -> {
                    guardPosition = Point(x, y)
                    EMPTY
                }

                '.' -> EMPTY
                '#' -> OBSTACLE
                else -> OBSTACLE//??
            }
        }
    } to guardPosition
}