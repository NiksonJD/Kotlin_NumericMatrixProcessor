package processor

import kotlin.math.pow

fun input(prompt: String) = println(prompt).run { readln() }
fun inputList(prompt: String) = input(prompt).split(" ").map { it.toInt() }

val inputListDouble = { readln().split(" ").map { it.toDouble() } }

val menuMap = mapOf(
    "1" to ::addMatrices, "2" to ::multiplyByConstant, "3" to ::multiplyMatrices, "4" to ::transposeMatrix,
    "5" to ::determinantMatrix, "6" to ::inverseMatrix
)

fun addMatrices() {
    val (a, b) = inputMatrices(2).also { println("The result is:") }
    a.zip(b) { row1, row2 -> println(row1.zip(row2) { value1, value2 -> value1.plus(value2) }.joinToString(" ")) }
}

fun multiplyByConstant() {
    val a = inputMatrices(1)[0]
    val constant = input("Enter constant:").toDouble().also { println("The result is:") }
    a.map { row -> println(row.map { v -> v.times(constant) }.joinToString(" ")) }
}

fun multiplyMatrices() {
    val (a, b) = inputMatrices(2).also { println("The result is:") }
    val c = List(a.size) { i -> List(b[0].size) { j -> a[i].zip(b.map { row -> row[j] }).sumOf { (a, b) -> a * b } } }
    c.forEach { row -> println(row.joinToString(" ")) }
}

fun transposeMatrix() {
    val choice = input("\n1. Main diagonal\n2. Side diagonal\n3. Vertical line\n4. Horizontal line\nYour choice:")
    val a = inputMatrices(1)[0]
    val transposed = List(a.size) { Array(a[0].size) { 0.0 } }
    val (s, column) = a.run { size to get(0).size }
    when (choice) {
        "1" -> a.forEachIndexed { i, row -> row.forEachIndexed { j, v -> transposed[j][i] = v } }
        "2" -> a.forEachIndexed { i, row -> row.forEachIndexed { j, v -> transposed[column - j - 1][s - i - 1] = v } }
        "3" -> a.forEachIndexed { i, row -> row.forEachIndexed { j, v -> transposed[i][column - j - 1] = v } }
        "4" -> a.forEachIndexed { i, row -> row.forEachIndexed { j, v -> transposed[s - i - 1][j] = v } }
    }
    transposed.forEach { row -> println(row.joinToString(" ")) }
}

fun determinantMatrix() = println("The result is:\n${determinant(inputMatrices(1)[0])}")

fun determinant(m: List<List<Double>>): Double {
    return if (m.size == 1) m[0][0] else {
        (m.indices).sumOf { j ->
            (-1.0).pow(j) * m[0][j] *
                    determinant(m.subList(1, m.size).map { it.subList(0, j) + it.subList(j + 1, it.size) })
        }
    }
}

fun inverseMatrix() {
    val m = inputMatrices(1)[0].map { it }.toMutableList()
    if (determinant(m) == 0.0) println("This matrix doesn't have an inverse.").also { return }
    val im = MutableList(m.size) { i -> List(m.size) { j -> if (i == j) 1.0 else 0.0 } }
    (0 until m.size).forEach { i ->
        val pivot = m[i][i]
        m[i] = m[i].map { it / pivot }
        im[i] = im[i].map { it / pivot }
        (0 until m.size).filter { it != i }.forEach { k ->
            val factor = m[k][i]
            m[k] = m[k].zip(m[i]).map { (a, b) -> a - b * factor }
            im[k] = im[k].zip(im[i]).map { (a, b) -> a - b * factor }
        }
    }
    println("The result is:").also { im.forEach { row -> println(row.joinToString(" ")) } }
}

fun inputMatrices(count: Int): Array<List<List<Double>>> {
    val array = Array(count) { listOf<List<Double>>() }
    for (i in 1..count) {
        val order = if (count == 2) if (i == 1) "first" else "second" else ""
        inputList("Enter size of $order matrix:").also { println("Enter matrix:") }
            .let { array[i - 1] = List(it[0]) { inputListDouble() } }
    }
    return array
}

fun main() {
    val menu = "\n1. Add matrices\n2. Multiply matrix by a constant\n3. Multiply matrices\n4. Transpose matrix" +
            "\n5. Calculate a determinant\n6. Inverse matrix\n0. Exit\nYour choice:"
    while (true) {
        val answer = input(menu); if (answer == "0") break else menuMap[answer]?.invoke()
    }
}