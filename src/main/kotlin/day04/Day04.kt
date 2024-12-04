package day04

import java.io.File


fun main() {
    val searchMap = getSearchMap()
    println(searchXMAS(searchMap))
    println(searchMAS(searchMap))
}

fun searchMAS(searchMap: List<String>): Int {
    val rows = searchMap.size
    val cols = searchMap[0].length
    var count = 0

    val filteredValue = listOf("MSSM", "SSMM", "SMMS", "MMSS")

    fun checkXMas(x: Int, y: Int): Boolean {
        val c2 = searchMap[y - 1][x - 1]//↖️
        val c4 = searchMap[y - 1][x + 1]//↗️
        val c1 = searchMap[y + 1][x + 1]//↘️
        val c3 = searchMap[y + 1][x - 1]//↙️
        val value = "$c2$c4$c1$c3"
        return value in filteredValue
    }

    for (y in 1 until rows - 1) {
        for (x in 1 until cols - 1) {
            if (searchMap[y][x] == 'A') {
                if (checkXMas(x, y)) {
                    count++
                }
            }
        }
    }
    return count
}

val XMAS = "XMAS".toRegex()
val SAMX = "SAMX".toRegex()

fun List<String>.transpose(): List<String> {
    val maxLength = maxOfOrNull { it.length } ?: 0
    return (0 until maxLength).map { i ->
        mapNotNull { it.getOrNull(i) }.joinToString("")
    }
}

fun List<String>.shift(progression: List<Int>): List<String> {
    val size = this.size
    return this.mapIndexed { index, str ->
        val count = progression[index]
        " ".repeat(count) + str + " ".repeat(size - count)
    }
}

fun searchXMAS(searchMap: List<String>): Int {
    val size = searchMap.size
    val transposed = searchMap.transpose()
    val shiftedTransposedForMainDiagonal = searchMap.shift((size downTo 0).toList()).transpose()
    val shiftedTransposedForAntiDiagonal = searchMap.shift((0..size).toList()).transpose()
    return searchHorizontal(searchMap, XMAS) +   //➡️
            searchHorizontal(searchMap, SAMX) +  //⬅️
            searchHorizontal(transposed, XMAS) + //⬇️
            searchHorizontal(transposed, SAMX) + //⬆️
            searchHorizontal(shiftedTransposedForMainDiagonal, XMAS) + //↘️
            searchHorizontal(shiftedTransposedForMainDiagonal, SAMX) + //↖️
            searchHorizontal(shiftedTransposedForAntiDiagonal, XMAS) + //↙️
            searchHorizontal(shiftedTransposedForAntiDiagonal, SAMX)   //↗️

}

fun searchHorizontal(searchMap: List<String>, pattern: Regex): Int {
    return searchMap.sumOf { pattern.findAll(it).count() }
}

fun getSearchMap(): List<String> {
    return File("src/main/kotlin/day04/input.txt").readLines()
}