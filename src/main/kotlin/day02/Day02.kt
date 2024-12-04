package day02

import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sign


fun main() {
    val reports = getReports()

    val countCorrectReports = reports.count { checkReportIsCorrect(it) }
    println(countCorrectReports)

    val countHandledReports = reports.count { canHandleReport(it) }
    println(countHandledReports)
}

private fun checkReportIsCorrect(report: List<Int>): Boolean {
    val diffList = report.zipWithNext { a, b -> a - b }
    val hasMixedSigns = diffList.map { it.sign }.distinct().size > 1
    val hasInvalidDiffs = diffList.map { it.absoluteValue }.any { it !in 1..3 }

    return !hasMixedSigns && !hasInvalidDiffs
}


fun canHandleReport(report: List<Int>): Boolean {
    if (report.size <= 2) return true
    val diffList = report.zipWithNext { a, b -> a - b }

    val signs = diffList.groupBy { it.sign }.mapValues { it.value.size }
    if (signs.size > 2) return false // the sequence has a rise, plateau, and decline
    if (signs.getOrDefault(0, 0) > 1) return false //plateau too big

    if (checkReportIsCorrect(report)) {
        return true
    }
    for (i in report.indices) {
        report.toMutableList().apply { removeAt(i) }.let {
            if (checkReportIsCorrect(it)) return true
        }
    }
    return false
}

fun getReports(): List<List<Int>> {
    return File("src/main/kotlin/day02/input.txt").readLines()
        .map { it.split(Regex("\\s+")).map { it.toInt() } }
}