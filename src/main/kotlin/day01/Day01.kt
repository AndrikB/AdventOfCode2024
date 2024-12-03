package day01

import java.io.File
import kotlin.math.absoluteValue


fun main() {
    val (firstColumn, secondColumn) = getColumnsFromFile()

    val distance = firstColumn.zip(secondColumn).sumOf { (it.first - it.second).absoluteValue }
    println(distance)

    val firstMap = firstColumn.groupBy { it }.mapValues { it.value.count() }
    val secondMap = secondColumn.groupBy { it }.mapValues { it.value.count() }

    val similarity = firstMap.mapNotNull { secondMap[it.key]?.times(it.key)?.times(it.value) }.sum()
    println(similarity)
}

fun getColumnsFromFile(filename: String = "src/main/kotlin/day01/input.txt"): Pair<List<Int>, List<Int>> {
    val list = File("src/main/kotlin/day01/input.txt").readLines()
        .map { it.split(Regex("\\s+")) }
        .map { it[0].toInt() to it[1].toInt() }

    val firstColumn = list.map { it.first }.sorted()
    val secondColumn = list.map { it.second }.sorted()

    return firstColumn to secondColumn
}