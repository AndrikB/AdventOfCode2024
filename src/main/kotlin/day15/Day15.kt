package day15


import day15.Cell.*
import day15.Direction.*
import java.awt.Point
import java.io.File


fun main() {
    val (map, robot, moves) = parseFile()

    val map1 = evaluatePart1(map, robot, moves)
    println(map1.calculateSumOfGps())

    val biggerMap = map.map { line -> line.flatMap { it.toBiggerMap() } }
    val map2 = evaluatePart2(biggerMap, robot.toBiggerMap(), moves)
    println(map2.calculateSumOfGps())
}


fun evaluatePart1(startMap: List<List<Cell>>, robotStartPoint: Point, moves: List<Direction>): List<List<Cell>> {
    val robot = robotStartPoint.location
    val map = startMap.map { it.toMutableList() }

    moves.forEach { direction ->
        fun getNextEmptySpace(current: Point): Point? {
            val nextPoint = current.nextPoint(direction)
            return when (map[nextPoint.y][nextPoint.x]) {
                WALL -> null
                EMPTY -> nextPoint
                else -> getNextEmptySpace(nextPoint)
            }
        }

        val nextCellPoint = getNextEmptySpace(robot) ?: return@forEach

        robot.translate(direction.dx, direction.dy)
        map.swap(robot, nextCellPoint)
    }
    return map
}

fun evaluatePart2(startMap: List<List<Cell>>, robotStartPoint: Point, moves: List<Direction>): List<List<Cell>> {
    val robot = robotStartPoint.location
    val map = startMap.map { it.toMutableList() }

    fun Point.takeWithNeighbourIfBox(map: List<List<Cell>>): List<Point> {
        return when (map[y][x]) {
            BOX_END -> listOf(this, this.nextPoint(LEFT))
            BOX_START -> listOf(this, this.nextPoint(RIGHT))
            else -> listOf(this)
        }
    }

    fun Point.canMove(direction: Direction): Boolean {
        val nextPoint = nextPoint(direction)
        val nextPoints = when (direction) {
            RIGHT, LEFT -> listOf(nextPoint)
            TOP, BOTTOM -> nextPoint.takeWithNeighbourIfBox(map)
        }

        return nextPoints.all {
            when (map.get(it)) {
                WALL -> false
                EMPTY -> true
                BOX_END, BOX_START -> it.canMove(direction)
                else -> false
            }
        }
    }

    fun Point.move(direction: Direction) {
        val nextPoint = nextPoint(direction)
        if (map.get(nextPoint) in setOf(BOX_START, BOX_END)) {
            val nextPoints = when (direction) {
                RIGHT, LEFT -> listOf(nextPoint)
                TOP, BOTTOM -> nextPoint.takeWithNeighbourIfBox(map)
            }
            nextPoints.forEach { it.move(direction) }
        }
        map.swap(this, nextPoint)
    }

    moves.forEach { direction ->
        if (robot.canMove(direction)) {
            robot.move(direction)
            robot.translate(direction.dx, direction.dy)
        }
    }
    return map
}

fun List<List<Cell>>.calculateSumOfGps(): Int {
    return mapIndexed { y, line ->
        line.mapIndexedNotNull { x, c -> c.takeIf { it in setOf(BOX_START, BOX) }?.let { y * 100 + x } }.sum()
    }.sum()
}

enum class Cell(val c: Char) {
    WALL('#'),
    BOX('O'),
    BOX_START('['),
    BOX_END(']'),
    ROBOT('@'),
    EMPTY('.');

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

private fun Cell.toBiggerMap(): List<Cell> {
    return when (this) {
        WALL -> listOf(WALL, WALL)
        BOX -> listOf(BOX_START, BOX_END)
        EMPTY -> listOf(EMPTY, EMPTY)
        else -> emptyList()
    }
}

private fun Point.toBiggerMap(): Point = Point(x * 2, y)

private fun List<List<Cell>>.get(point: Point) = this[point.y][point.x]
private fun List<MutableList<Cell>>.set(point: Point, cell: Cell): Cell = this[point.y].set(point.x, cell)
private fun List<MutableList<Cell>>.swap(a: Point, b: Point) = this.set(a, this.set(b, this.get(a)))

enum class Direction(val dx: Int, val dy: Int) {
    TOP(0, -1),
    RIGHT(1, 0),
    BOTTOM(0, 1),
    LEFT(-1, 0);

    companion object {
        fun parseChar(c: Char): Direction? {
            return when (c) {
                '<' -> LEFT
                '^' -> TOP
                'v' -> BOTTOM
                '>' -> RIGHT
                else -> null
            }
        }
    }
}

fun Point.nextPoint(direction: Direction): Point = this.location.also { it.translate(direction.dx, direction.dy) }

fun parseFile(): Triple<List<List<Cell>>, Point, List<Direction>> {
    with(File("src/main/kotlin/day15/input.txt").readText().split("\n\n")) {
        lateinit var robot: Point

        val map = this[0].split('\n').mapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                val cell = Cell.parseChar(c)
                if (cell == ROBOT) {
                    robot = Point(x, y)
                    EMPTY
                } else cell
            }
        }

        val moves = this[1].mapNotNull { Direction.parseChar(it) }

        return Triple(map, robot, moves)
    }
}
