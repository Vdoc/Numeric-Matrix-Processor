package processor

data class Matrix(val m: Int, val n: Int, val matrix: List<List<Double>>)

fun addition() {
    val (mA, nA, a) = getMatrix(" first")
    val (mB, nB, b) = getMatrix(" second")
    println("The result is:")
    if (mA to nA == mB to nB) List(mA as Int) { a[it].zip(b[it]) { x, y -> x + y } }.printMatrix()
    else println("The operation cannot be performed.")
}

fun multiplyConst() {
    val (_, n, a) = getMatrix("")
    print("Enter constant: ")
    val c = readLine()!!.toInt()
    println("The result is:")
    a.flatten()
            .map { it * c }
            .chunked(n)
            .printMatrix()
}

fun multiplyMatrices() {
    val (mA, nA, a) = getMatrix(" first")
    val (mB, nB, b) = getMatrix(" second")
    println("The result is:")
    val c = List(mA) { MutableList(nB) { 0.0 } }
    if (nA == mB) {
        for (i in 0 until mA)
            for (j in 0 until nB)
                for (k in 0 until nA)
                    c[i][j] += a[i][k] * b[k][j]
        c.printMatrix()
    } else println("The operation cannot be performed.")
}

fun transposeMatrix() {
    println("\n1. Main diagonal\n2. Side diagonal\n3. Vertical line\n4. Horizontal line\nYour choice: ")
    val choice = readLine()!!.toInt()
    val (m, n, a) = getMatrix("")
    when (choice) {
        1 -> {
            val b = List(n) { MutableList(m) { 0.0 } }
            a.forEachIndexed { i, v -> v.forEachIndexed { j, x -> b[j][i] = x } }
            b.printMatrix()
        }
        2 -> {
            val b = List(n) { MutableList(m) { 0.0 } }
            a.forEachIndexed { i, v -> v.forEachIndexed { j, x -> b[n - j - 1][m - i - 1] = x } }
            b.printMatrix()
        }
        3 -> {
            val b = List(m) { MutableList(n) { 0.0 } }
            a.forEachIndexed { i, v -> v.forEachIndexed { j, x -> b[i][n - j - 1] = x } }
            b.printMatrix()
        }
        4 -> {
            val b = List(m) { MutableList(n) { 0.0 } }
            a.forEachIndexed { i, v -> v.forEachIndexed { j, x -> b[m - i - 1][j] = x } }
            b.printMatrix()
        }
    }
}

fun getMatrix(name: String): Matrix {
    print("Enter size of$name matrix: ")
    val (m, n) = readLine()!!.split(" ").map(String::toInt)
    println("Enter$name matrix: ")
    val a = List(m) { readLine()!!.split(" ").map(String::toDouble) }
    return Matrix(m, n, a)
}

fun List<List<Double>>.printMatrix() = joinToString("\n") {
    it.joinToString(" ") {
        if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
    }
}.let(::println)

fun main() {
    while (true) {
        print("1. Add matrices\n2. Multiply matrix by a constant\n3. Multiply matrices\n4. Transpose matrix\n0. Exit\nYour choice: ")
        when (readLine()!!.toInt()) {
            1 -> addition()
            2 -> multiplyConst()
            3 -> multiplyMatrices()
            4 -> transposeMatrix()
            0 -> return
        }
        println()
    }
}