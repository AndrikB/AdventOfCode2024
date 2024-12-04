package day03

import java.io.File


fun main() {
    val memory = getMemory()

    part1(memory)
    part2(memory)
}

fun part1(memory: String) {
    val regex = """mul\((\d+),(\d+)\)""".toRegex()

    val result = regex.findAll(memory).mapNotNull {
        it.multiplyGroup()
    }.sum()
    println(result)
}

private fun MatchResult.multiplyGroup(): Int? {
    return this.groups[1]?.value?.toInt()?.times(this.groups[2]?.value?.toInt() ?: 0)
}

fun part2(memory: String) {
    val regex = """mul\((\d+),(\d+)\)|(don't)|(do)""".toRegex()
    var shouldMultiply = true
    val result = regex.findAll(memory).mapNotNull {
        when (it.value) {
            "don't" -> shouldMultiply = false
            "do" -> shouldMultiply = true
            else -> {
                if (shouldMultiply) return@mapNotNull it.multiplyGroup()
            }
        }
        null
    }.sum()
    println(result)
}


fun getMemory(): String {
    return File("src/main/kotlin/day03/input.txt").readLines().joinToString()
}