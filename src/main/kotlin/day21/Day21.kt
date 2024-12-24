package day21

import java.awt.Point
import java.io.File
import kotlin.math.absoluteValue

fun main() {
    val codes = parseFile()

    println(codes.sumOf { code -> code.countOfPresses(3) * code.codeToInt() })
    println(codes.sumOf { code -> code.countOfPresses(26) * code.codeToInt() })
}

val cache = mutableMapOf<Pair<Int, String>, Long>()

fun String.countOfPresses(level: Int, preCalculated: Map<Pair<Char, Char>, List<String>> = firstPreCalculated): Long {
    return cache.getOrPut((level to this)) {
        if (level == 0) this.length.toLong()
        else "A$this".zipWithNext { a, b -> a to b }.sumOf { path ->
            preCalculated[path]!!.minOf {
                it.countOfPresses(level - 1, secondPreCalculated)
            }
        }
    }
}

fun String.codeToInt() = this.filter { it.isDigit() }.toInt()

val firstPreCalculated = preCalculate(
    listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf(null, '0', 'A')
    )
)

val secondPreCalculated = preCalculate(
    listOf(
        listOf(null, '^', 'A'),
        listOf('<', 'v', '>'),
    )
)

fun preCalculate(matrix: List<List<Char?>>): Map<Pair<Char, Char>, List<String>> {
    val map = matrix.flatMapIndexed { y, line ->
        line.mapIndexed { x, c -> Point(x, y) to c }
    }.toMap().filterValues { it != null }.mapValues { it.value!! }

    return map.flatMap { (startPoint, startChar) ->
        map.map { (endPoint, endChar) ->
            val dx = endPoint.x - startPoint.x
            val dy = endPoint.y - startPoint.y
            val dxSymbol = "<".takeIf { dx < 0 } ?: ">"
            val dySymbol = "^".takeIf { dy < 0 } ?: "v"

            val xChars = dxSymbol.repeat(dx.absoluteValue)
            val yChars = dySymbol.repeat(dy.absoluteValue)

            (startChar to endChar) to listOf(xChars + yChars, yChars + xChars).distinct().filter { path ->
                val point = startPoint.location
                for (c in path) {
                    point.move(c)
                    if (map[point] == null)
                        return@filter false
                }
                true
            }.map { it + 'A' }
        }
    }.toMap()
}

fun Point.move(char: Char) {
    when (char) {
        '<' -> this.translate(-1, 0)
        'v' -> this.translate(0, 1)
        '>' -> this.translate(1, 0)
        '^' -> this.translate(0, -1)
        else -> {}
    }
}

fun parseFile(): List<String> {
    return File("src/main/kotlin/day21/input.txt").readLines()
}
