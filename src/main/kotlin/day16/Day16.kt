package day16

import day16.Cell.*
import day16.Reindeer.Direction.*
import java.awt.Point
import java.io.File
import java.util.*


fun main() {
    val (map, reindeer, end) = parseFile()

    val mapWithScores: List<MutableList<Int?>> = map.map { line -> line.map { null }.toMutableList() }
    val finishList = mutableListOf<Reindeer>()

    val queue = PriorityQueue<Reindeer> { a, b -> a.score - b.score }
    queue.add(reindeer)
    while (queue.isNotEmpty()) {
        val currentReindeer = queue.poll()

        if (finishList.isNotEmpty() && finishList.first().score < currentReindeer.score) {
            break
        }

        if (currentReindeer.point == end) {
            finishList.add(currentReindeer.copy(visited = currentReindeer.visited + end))
            continue
        }

        val newPoints = currentReindeer.getAllNeighbours()
            .filter { map.get(it.point) != WALL }
            .filter { it.score - (mapWithScores.get(it.point) ?: Int.MAX_VALUE) <= 1001 }

        queue.addAll(newPoints)
        newPoints.forEach { mapWithScores.set(it.point, it.score) }
    }

    println(mapWithScores.get(end))
    println(finishList.first().score)
    println(finishList.flatMap { it.visited }.toSet().size)
}

enum class Cell(val c: Char) {
    WALL('#'),
    EMPTY('.'),
    START('S'),
    END('E');

    companion object {
        private val map = values().associateBy { it.c }
        fun parseChar(c: Char): Cell? {
            return map[c]
        }
    }

    override fun toString(): String {
        return c.toString()
    }
}

fun <T> List<List<T>>.get(point: Point) = this[point.y][point.x]
private fun <T> List<MutableList<T?>>.set(point: Point, t: T) {
    this[point.y][point.x] = t
}

data class Reindeer(
    val direction: Direction,
    val point: Point,
    val score: Int = 0,
    val visited: Set<Point> = emptySet()
) {
    private fun move(): Reindeer {
        return Reindeer(direction, point.nextPoint(direction), score + 1, visited.plus(point))
    }

    fun getAllNeighbours(): List<Reindeer> {
        return listOf(
            this.copy(direction = direction.prevClockwise, score = score + 1000),
            this,
            this.copy(direction = direction.nextClockwise, score = score + 1000),
        ).map { it.move() }
    }

    enum class Direction(val dx: Int, val dy: Int) {
        TOP(0, -1),
        RIGHT(1, 0),
        BOTTOM(0, 1),
        LEFT(-1, 0);

        val nextClockwise by lazy {
            val values = Direction.values()
            values[(this.ordinal + 1) % values.size]
        }

        val prevClockwise by lazy {
            val values = Direction.values()
            values[(this.ordinal - 1).mod(values.size)]
        }
    }

    private fun Point.nextPoint(direction: Direction): Point =
        this.location.also { it.translate(direction.dx, direction.dy) }

}

fun parseFile(): Triple<List<List<Cell>>, Reindeer, Point> {
    lateinit var reindeer: Reindeer
    lateinit var end: Point
    return Triple(File("src/main/kotlin/day16/input.txt").readLines().mapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            Cell.parseChar(c).also {
                if (it == START) reindeer = Reindeer(RIGHT, Point(x, y))
                else if (it == END) end = Point(x, y)
            }
        }
    }, reindeer, end)
}
