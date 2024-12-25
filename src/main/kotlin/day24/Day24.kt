package day24

import java.io.File
import java.math.BigInteger

fun main() {
    val (input, rules) = parseFile()
    part1(input, rules)
    part2(rules)
}

fun part2(rules: List<Rule>) {
    val rulesGrouped = rules.groupBy { it.operator }
    val maxResult = rules.map { it.result }.filter { it.startsWith('z') }.max()

    val xor = rulesGrouped[Rule.Operator.XOR] ?: emptyList()
    val inputForXor = xor.flatMap { it.parameters() }
    val (xXORy, xorToZ) = xor.partition { rule -> "xy".any { rule.a.startsWith(it) } }

    val and = rulesGrouped[Rule.Operator.AND] ?: emptyList()

    val or = rulesGrouped[Rule.Operator.OR] ?: emptyList()
    val inputForOr = or.flatMap { it.parameters() }


    val wrongOr = or.filter { it.startsWithZ() }.filter { it.result != maxResult } //all wrong
    println("Result of OR should not be Z $wrongOr")

    val wrongXXorY = xXORy.filter { it.result !in inputForXor }.filter { it.result != "z00" }
    println("x XOR y result should be input for next XOR: $wrongXXorY")

    val wrongXorToZ = xorToZ.filterNot { it.startsWithZ() }
    println("XOR (not from x y) result should be Z: $wrongXorToZ")

    val wrongAnd = and.filter { it.result !in inputForOr }.filterNot { it.a.endsWith("00") }
    println("Result for AND should be input for OR: $wrongAnd")

    println((wrongAnd + wrongXorToZ + wrongXXorY + wrongOr).map { it.result }.sorted().joinToString(","))
}

fun Rule.startsWithZ() = this.result.startsWith('z')

fun part1(input: Map<String, Boolean>, rules: List<Rule>) {
    val mapWithGates = input.toMutableMap()
    var unprocessedRules = rules.toList()

    while (unprocessedRules.isNotEmpty()) {
        val (toBeProcessed, unprocessed) = unprocessedRules.partition { rule -> rule.a in mapWithGates && rule.b in mapWithGates }
        toBeProcessed.forEach { rule ->
            mapWithGates[rule.result] = rule.operator.process(mapWithGates[rule.a]!!, mapWithGates[rule.b]!!)
        }
        unprocessedRules = unprocessed
    }

    val result = mapWithGates.filter { (k, _) -> k.startsWith('z') }
        .mapKeys { (k, _) -> k.filter { it.isDigit() } }
        .toList()
        .sortedByDescending { it.first }
        .joinToString("") { (_, v) -> "1".takeIf { v } ?: "0" }
    println(result)
    println(BigInteger(result, 2))

}

data class Rule(val a: String, val operator: Operator, val b: String, val result: String) {
    enum class Operator(val process: (Boolean, Boolean) -> Boolean) {
        AND({ a, b -> a and b }),
        OR({ a, b -> a or b }),
        XOR({ a, b -> a xor b });
    }

    fun parameters() = listOf(a, b)
}

val inputRegex = """([xy0-9]+): (\d+)""".toRegex()
val ruleRegex = """([a-z0-9]+) (AND|OR|XOR) ([a-z0-9]+) -> ([a-z0-9]+)""".toRegex()

fun parseFile(): Pair<Map<String, Boolean>, List<Rule>> {
    with(File("src/main/kotlin/day24/input.txt").readText().split("\n\n")) {

        val inputs = this[0].split('\n')
            .map { inputRegex.find(it)!! }
            .associate { it.groups[1]!!.value to (it.groups[2]!!.value == "1") }

        val rules = this[1].split('\n')
            .map { rule -> ruleRegex.find(rule)!!.groups.map { it!!.value } }
            .map { Rule(it[1], Rule.Operator.valueOf(it[2]), it[3], it[4]) }

        return inputs to rules
    }
}
