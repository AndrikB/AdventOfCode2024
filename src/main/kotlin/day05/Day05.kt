package day05

import java.io.File
import java.util.Collections

fun main() {
    val (pageOrderingRules, updates) = parseFile()

    val (valid, invalid) = updates.partition { updatePages ->
        updatePages.isValid(pageOrderingRules)
    }

    println(valid.sumOf { it[it.size / 2] })

    val fixed = invalid.map { it.toCorrect(pageOrderingRules) }
    println(fixed.sumOf { it[it.size / 2] })

}

private fun List<Int>.toCorrect(pageOrderingRules: List<Set<Int>>): List<Int> {
    forEachIndexed { index, value ->
        val rule = pageOrderingRules[value]
        this.subList(0, index).forEachIndexed { beforeIndex, before ->
            if (before in rule){
                Collections.swap(this, index, beforeIndex)
            }
        }
    }
    return this
}

private fun List<Int>.isValid(pageOrderingRules: List<Set<Int>>): Boolean {
    forEachIndexed { index, value ->
        val rule = pageOrderingRules[value]
        this.subList(0, index).forEach { before ->
            if (before in rule) {
                return false
            }
        }
    }
    return true
}

fun parseFile(): Pair<List<Set<Int>>, List<List<Int>>> {
    val pageOrderingRules = List(100) { mutableSetOf<Int>() }
    val updates = mutableListOf<List<Int>>()

    File("src/main/kotlin/day05/input.txt").readLines().forEach { line ->
        if (line.contains("|")) {
            val rule = line.split("|")
            pageOrderingRules[rule[0].toInt()].add(rule[1].toInt())
        } else if (line.contains(",")) {
            updates.add(line.split(",").map { it.toInt() })
        }
    }
    return pageOrderingRules to updates
}