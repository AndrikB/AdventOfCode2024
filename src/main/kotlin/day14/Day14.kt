package day14


import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.sign

const val width = 101
const val widthMedian = width / 2
const val height = 103
const val heightMedian = height / 2

fun main() {
    val robots = parseFile()
    printQuadrants(robots.map { it.copy() })
    printEasterEgg(robots)
}

fun printQuadrants(robots: List<Robot>) {
    val newPositions = robots.map { it.iterate(100) }
    val quadrants = mutableListOf(0, 0, 0, 0)
    newPositions.forEach {
        val x = (it.p.x - widthMedian).sign
        val y = (it.p.y - heightMedian).sign

        if (x > 0 && y > 0) quadrants[0] += 1
        if (x > 0 && y < 0) quadrants[1] += 1
        if (x < 0 && y > 0) quadrants[2] += 1
        if (x < 0 && y < 0) quadrants[3] += 1
    }
    println(quadrants.reduce { acc, n -> acc * n })
}

fun printEasterEgg(robots: List<Robot>, from: Int = 6000, to: Int = 9000) {
    var newPositions = robots.map { it.iterate(from) }
    for (i in from..to) {
        printRobots(newPositions, i)
        newPositions = newPositions.map { it.iterate() }
    }
}

fun printRobots(robots: List<Robot>, i: Int) {
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    robots.forEach { image.setRGB(it.p.x, it.p.y, Color.WHITE.rgb) }

    val outputFile = File("src/main/kotlin/day14/images/$i.png")
    ImageIO.write(image, "png", outputFile)
}

data class Robot(
    val p: Point,
    val v: Pair<Int, Int>
) {
    fun iterate(i: Int = 1): Robot {
        return Robot(Point((p.x + v.first * i).mod(width), (p.y + v.second * i).mod(height)), v)
    }
}

val robotRegex = """p=(\d+),(\d+) v=(-?\d+),(-?\d+)""".toRegex()

fun parseFile(): List<Robot> {
    return File("src/main/kotlin/day14/input.txt").readLines().map {
        with(robotRegex.find(it)) {
            Robot(
                Point(this!!.groups[1]!!.value.toInt(), this.groups[2]!!.value.toInt()),
                this.groups[3]!!.value.toInt() to this.groups[4]!!.value.toInt()
            )
        }
    }
}
