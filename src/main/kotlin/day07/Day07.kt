package day07

import java.io.File

fun main() {
    val equations = parseFile()
    equations.filter { it.couldBeValid(listOf(Operator.ADD, Operator.MULTIPLY)) }
        .sumOf { it.first }.also { println(it) }

    equations.filter { it.couldBeValid(listOf(Operator.ADD, Operator.MULTIPLY, Operator.CONCATENATION)) }
        .sumOf { it.first }.also { println(it) }
}

private fun Pair<Long, List<Long>>.couldBeValid(operators: List<Operator>): Boolean {
    val expectedResult = first
    val currentValue = second.first()
    val remainingElements = second.drop(1)
    return evaluateNext(operators, expectedResult, currentValue, remainingElements)
}

private fun evaluateNext(operators: List<Operator>, expected: Long, current: Long, remaining: List<Long>): Boolean {
    if (remaining.isEmpty()) return expected == current
    if (current > expected) return false

    val first = remaining.first()
    val newRemaining = remaining.drop(1)
    return operators.any { evaluateNext(operators, expected, it.apply(current, first), newRemaining) }
}

fun parseFile(): List<Pair<Long, List<Long>>> {
    return File("src/main/kotlin/day07/input.txt").readLines()
        .map { line -> line.split(": ") }
        .map { line -> line[0].toLong() to line[1].split(' ').map { it.toLong() } }
}

enum class Operator(val apply: (Long, Long) -> Long) {
    ADD({ first, second -> first + second }),
    MULTIPLY({ first, second -> first * second }),
    CONCATENATION({ first, second -> (first.toString() + second.toString()).toLong() }),
}