package day19

import java.io.File


fun main() {
    val (patterns, designs) = parseFile()

    println(designs.count { it.canBeCreated(patterns) })
    println(designs.sumOf { it.countAvailableWays(patterns) })
}

private fun String.canBeCreated(patterns: List<String>): Boolean {
    if (this.isEmpty()) return true
    return patterns.asSequence()
        .filter { this.startsWith(it) }
        .map { this.removePrefix(it) }
        .any { it.canBeCreated(patterns) }
}

val calculatedValues: MutableMap<String, Long> = mutableMapOf()

private fun String.countAvailableWays(patterns: List<String>): Long {
    if (this.isEmpty()) return 1
    return calculatedValues.getOrPut(this) {
        patterns.asSequence()
            .filter { this.startsWith(it) }
            .map { this.removePrefix(it) }
            .sumOf { it.countAvailableWays(patterns) }
    }
}

fun parseFile(): Pair<List<String>, List<String>> {
    return with(File("src/main/kotlin/day19/input.txt").readText().split("\n\n")) {
        get(0).split(", ") to get(1).split('\n')
    }
}
