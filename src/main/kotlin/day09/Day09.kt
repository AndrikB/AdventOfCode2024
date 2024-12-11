package day09


import java.io.File
import java.util.Collections

fun main() {
    val diskMap = parseFile()

    val disk = diskMap.flatMapIndexed { index, c ->
        val count = c.digitToInt()
        val idOrNull = if (index % 2 == 0) {
            val id = index / 2L
            id
        } else {
            null
        }
        List(count) { idOrNull }
    }

    println(disk.processOldDefragmentation().getChecksum())
    println(disk.processDefragmentation().getChecksum())
}

fun List<Long?>.processOldDefragmentation(): List<Long?> {
    val disk = toMutableList()
    var i = 0
    var j = disk.size - 1

    while (true) {
        if (i == j) return disk
        else if (disk[i] != null) i++
        else if (disk[j] == null) j--
        else (Collections.swap(disk, i, j))
    }
}

fun List<Long?>.processDefragmentation(): List<Long?> {
    val disk = toMutableList()

    for (i in disk.size - 1 downTo 0) {
        val value = disk[i]
        if (value == disk.getOrNull(i + 1)) continue
        if (value == null) continue

        val firstFromBlock = disk.subList(0, i).indexOfLast { it != value } + 1

        val blockSize = i + 1 - firstFromBlock
        val firstNullBlock = disk.findFirstSublistWithNulls(blockSize)
        if (firstNullBlock == null || firstNullBlock > i) continue

        for (j in 0 until blockSize) {
            disk[firstNullBlock + j] = value
            disk[firstFromBlock + j] = null
        }
    }
    return disk
}

fun List<Long?>.findFirstSublistWithNulls(count: Int): Int? {
    for (i in 0..this.size - count) {
        if (this.subList(i, i + count).all { it == null }) {
            return i
        }
    }
    return null
}

fun List<Long?>.getChecksum(): Long =
    withIndex().mapNotNull { it.value?.times(it.index) }.sum()

fun parseFile(): String {
    return File("src/main/kotlin/day09/input.txt").readText()
}
