package day13


import java.io.File
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.RoundingMode

fun main() {
    val arcades = parseFile()

    println(arcades.mapNotNull { it.getTokenCount() }.sumOf { it })
    println(arcades.map { it.movePrizeLocation() }.mapNotNull { it.getTokenCount() }.sumOf { it })

}

private fun Arcade.movePrizeLocation(delta: BigDecimal = BigDecimal.valueOf(10000000000000)): Arcade {
    return copy(prize = Arcade.Prize(prize.x + delta, prize.y + delta))
}

//a = (p.x - b.x*b)/a.x
//b = (p.y - (a.y/a.x)*px)/(b.y - (a.y/a.x)*b.x)
fun Arcade.getTokenCount(): BigDecimal? {
    val pressBCount = (prize.y - a.dy * prize.x.divideWithScale(a.dx)) / (b.dy - a.dy * b.dx.divideWithScale(a.dx))
    val pressACount = (prize.x - b.dx * pressBCount).divideWithScale(a.dx)
    return (pressACount.round() * BigDecimal.valueOf(3) + pressBCount.round())
        .takeIf { pressACount.isValidCount() && pressBCount.isValidCount() }
}

val epsilon = BigDecimal("1e-20")
fun BigDecimal.round(): BigDecimal = this.setScale(0, RoundingMode.HALF_UP)
fun BigDecimal.divideWithScale(b: BigDecimal): BigDecimal = this.divide(b, 50, RoundingMode.HALF_UP)

fun BigDecimal.isValidCount() = this > BigDecimal.ZERO &&
        (this.remainder(ONE).abs() < epsilon || this.remainder(ONE).minus(ONE).abs() < epsilon)


data class Arcade(
    val a: Button,
    val b: Button,
    val prize: Prize
) {
    data class Button(
        val dx: BigDecimal, val dy: BigDecimal
    )

    data class Prize(
        val x: BigDecimal, val y: BigDecimal
    )
}

val buttonRegex = """Button .: X\+(\d+), Y\+(\d+)""".toRegex()
fun String.toButton(): Arcade.Button {
    with(buttonRegex.find(this)!!) {
        return Arcade.Button(groups[1]!!.value.toBigDecimal(), groups[2]!!.value.toBigDecimal())
    }
}

val prizeRegex = """Prize: X=(\d+), Y=(\d+)""".toRegex()
fun String.toPrizePoint(): Arcade.Prize {
    with(prizeRegex.find(this)!!) {
        return Arcade.Prize(groups[1]!!.value.toBigDecimal(), groups[2]!!.value.toBigDecimal())
    }
}

fun parseFile(): List<Arcade> {
    return File("src/main/kotlin/day13/input.txt").readText().split("\n\n").map {
        val (a, b, prize) = it.split("\n")
        Arcade(a.toButton(), b.toButton(), prize.toPrizePoint())
    }
}
