package day11


import java.io.File
import java.math.BigInteger

fun main() {
    val stones = parseFile()

    println(stones.map { processStone(it, 25) }.sumOf { it })
    println(stones.map { processStone(it, 75) }.sumOf { it })
}


val calculatedValues: MutableMap<Pair<BigInteger, Int>, BigInteger> = mutableMapOf()

private fun processStone(value: BigInteger, iterationsLeft: Int = 25): BigInteger {
    if (iterationsLeft == 0) return BigInteger.ONE

    return calculatedValues.getOrPut(value to iterationsLeft) {
        if (value == BigInteger.ZERO) {
            processStone(BigInteger.ONE, iterationsLeft - 1)
        } else if (value.toString().length % 2 == 0) {
            val s = value.toString()
            val leftPart = s.substring(0, s.length / 2).toBigInteger()
            val rightPart = s.substring(s.length / 2).toBigInteger()
            processStone(leftPart, iterationsLeft - 1) + processStone(rightPart, iterationsLeft - 1)
        } else {
            processStone(value.times(BigInteger.valueOf(2024)), iterationsLeft - 1)
        }
    }
}

fun parseFile(): List<BigInteger> {
    return File("src/main/kotlin/day11/input.txt").readText().split(' ').map { it.toBigInteger() }
}
