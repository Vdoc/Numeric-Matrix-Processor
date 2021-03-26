package processor

fun main() {
    val instructions = """

        1. Add matrices
        2. Multiply matrix by a constant
        3. Multiply matrices
        4. Transpose matrix
        5. Calculate a determinant
        0. Exit
    """.trimIndent()
    while (true) {
        println(instructions)
        print("Your choice: ")
        when (readLine()!!.toInt()) {
            0 -> return
            1 -> addMatrices()
            2 -> multiplyByConstant()
            3 -> multiplyMatrices()
            4 -> transposeMatrix()
            5 -> calculateDeterminant()
            else -> println("Invalid choice.")
        }
    }
}

fun addMatrices() {
    print("Enter size of first matrix: ")
    val firstMatrix = NumberMatrix.createFromInput()
    print("Enter size of second matrix: ")
    val secondMatrix = NumberMatrix.createFromInput()
    try {
        println(firstMatrix + secondMatrix)
    } catch (e: IllegalArgumentException) {
        println("The operation cannot be performed.")
    }
}

fun multiplyByConstant() {
    print("Enter size of matrix: ")
    val matrix = NumberMatrix.createFromInput()
    val multiplyConstant = readLine()!!.toInt()
    try {
        println(matrix * multiplyConstant)
    } catch (e: IllegalArgumentException) {
        println("The operation cannot be performed.")
    }
}

fun multiplyMatrices() {
    print("Enter size of first matrix: ")
    val firstMatrix = NumberMatrix.createFromInput()
    print("Enter size of second matrix: ")
    val secondMatrix = NumberMatrix.createFromInput()
    try {
        println(firstMatrix * secondMatrix)
    } catch (e: IllegalArgumentException) {
        println("The operation cannot be performed.")
    }
}

fun transposeMatrix() {
    println(
            """
        
        1. Main diagonal
        2. Side diagonal
        3. Vertical line
        4. Horizontal line
    """.trimIndent()
    )
    print("Your choice: ")
    val transposeChoice = readLine()!!.toInt()
    print("Enter size of matrix: ")
    val matrix = NumberMatrix.createFromInput()

    println(
            when (transposeChoice) {
                1 -> matrix.transposeMain()
                2 -> matrix.transposeSide()
                3 -> matrix.transposeVertical()
                4 -> matrix.transposeHorizontal()
                else -> throw IllegalArgumentException()
            }
    )
}

fun calculateDeterminant() {
    print("Enter size of matrix: ")
    val matrix = NumberMatrix.createFromInput()
    println("The result is:\n${matrix.determinant()}")
}


sealed class NumberMatrix<T : Number>(
        val rows: Int,
        val columns: Int,
        private val matrix: Array<NumberVector<T>>
) : Iterable<NumberVector<T>> {
    val size = Pair(rows, columns)
    val indices = matrix.indices
    override fun iterator(): Iterator<NumberVector<T>> = matrix.iterator()

    companion object {
        fun createFromInput(): NumberMatrix<Number> {
            val (height, width) = readLine()!!.split(" ").map { it.toInt() }
            var matrix: NumberMatrix<Number>? = null
            var convertMethod = ""
            println("Enter matrix:")
            for (i in 0 until height) {
                val numbersAsString = readLine()!!.split(" ")
                if (matrix == null) {
                    if (numbersAsString[0].toIntOrNull() != null) {
                        convertMethod = "int"
                        matrix = IntMatrix(height, width) as NumberMatrix<Number>
                    } else {
                        convertMethod = "float"
                        matrix = FloatMatrix(height, width) as NumberMatrix<Number>
                    }
                }
                val numbers = when (convertMethod) {
                    "int" -> numbersAsString.map {
                        if (it.toIntOrNull() == null) it.toFloat().toInt() else it.toInt()
                    }
                    "float" -> numbersAsString.map { it.toFloat() }
                    else -> throw Exception()
                } as List<Number>
                for (j in matrix[i].indices) {
                    matrix[i][j] = numbers[j]
                }
            }
            return matrix!!
        }
    }

    override fun toString(): String {
        return matrix.joinToString("\n") { it.joinToString(" ") }
    }

    fun set(rowIndex: Int, columnIndex: Int, value: T) {
        matrix[rowIndex][columnIndex] = value
    }

    fun get(rowIndex: Int, columnIndex: Int): T {
        return matrix[rowIndex][columnIndex]
    }

    operator fun get(index: Int): NumberVector<T> {
        return getRow(index)
    }

    fun getRow(rowIndex: Int): NumberVector<T> {
        return matrix[rowIndex]
    }

    fun getColumn(columnIndex: Int): NumberVector<T> {
        // Finds the Vector class that is used for the first row and creates a new instance
        // of that class.
        val column = newVector()
        for (i in matrix.indices) {
            column[i] = matrix[i][columnIndex]
        }
        return column
    }

    fun transposeMain(): NumberMatrix<T> {
        val main = newInstanceOfThis(this.columns, this.rows)
        for (i in main.indices) {
            main.matrix[i] = this.getColumn(i)
        }
        return main
    }

    fun transposeSide(): NumberMatrix<T> {
        val side = newInstanceOfThis(this.columns, this.rows)
        val columnsReversed = Array(this.columns) { i -> this.getColumn(i) }.reversed()
        for (i in side.indices) {
            val vector = newVector()
            vector.fill(columnsReversed[i].reversed())
            side.matrix[i] = vector
        }
        return side
    }

    fun transposeVertical(): NumberMatrix<T> {
        // A vertical transpose is reversing the columns
        val vertical = newInstanceOfThis()
        val columnsReversed = Array(this.columns) { i -> this.getColumn(i) }.reversed()
        for ((i, row) in vertical.withIndex()) {
            for ((j, column) in columnsReversed.withIndex()) {
                row[j] = column[i]
            }
        }
        return vertical
    }

    fun transposeHorizontal(): NumberMatrix<T> {
        // A horizontal transpose is reversing the rows
        val horizontal = newInstanceOfThis()
        for ((i, row) in this.reversed().withIndex()) {
            horizontal.matrix[i] = row
        }
        return horizontal
    }

    fun determinant(): T {
        assertIsSquare()
        if (this.rows == 1) {
            return this[0][0]
        } else if (this.rows == 2) {
            return when (this) {
                is IntMatrix -> this[0][0] * this[1][1] - this[1][0] * this[0][1]
                is FloatMatrix -> this[0][0] * this[1][1] - this[1][0] * this[0][1]
            } as T
        }
        return when(this) {
            is IntMatrix -> {
                this[0].mapIndexed { column, cofactor ->
                    val sign = if (column != 0 && column % 2 != 0) -1 else 1
                    sign * cofactor * this.getMinor(0, column).determinant()
                }.sum()
            }
            is FloatMatrix -> {
                this[0].mapIndexed { column, cofactor ->
                    val sign = if (column != 0 && column % 2 != 0) -1 else 1
                    sign * cofactor * this.getMinor(0, column).determinant()
                }.sum()
            }
        } as T
    }

    fun getMinor(i: Int, j: Int): NumberMatrix<T> {
        assertIsSquare()
        if (this.rows < 3) {
            throw Exception("Matrix must be at least 3x3 to generate a minor")
        } else if (i !in this.indices) {
            throw IllegalArgumentException("i ($i) is not a row in this matrix")
        } else if (j !in this[0].indices) {
            throw IllegalArgumentException("j ($j) is not a column in this matrix")
        }
        val minorMatrix = newInstanceOfThis(this.rows - 1, this.columns - 1)
        for (rowIndex in this.indices) {
            if (i == rowIndex) continue
            for (columnIndex in this[rowIndex].indices) {
                if (j == columnIndex) continue
                val minorRowIndex = if (rowIndex > i) rowIndex - 1 else rowIndex
                val minorColumnIndex = if (columnIndex > j) columnIndex - 1 else columnIndex
                minorMatrix[minorRowIndex][minorColumnIndex] = this[rowIndex][columnIndex]
            }
        }
        return minorMatrix
    }

    operator fun plus(other: NumberMatrix<T>): NumberMatrix<T> {
        assertCanAdd(other)
        val addedMatrix = newInstanceOfThis(this.rows, this.columns)
        for (i in this.indices) {
            for (j in this.getRow(i).indices) {
                val value = when (this) {
                    is IntMatrix -> this[i][j] + other[i][j].toInt()
                    is FloatMatrix -> this[i][j] + other[i][j].toFloat()
                }
                addedMatrix.set(i, j, value as T)
            }
        }
        return addedMatrix
    }

    operator fun times(constant: Int): NumberMatrix<T> {
        val multipliedMatrix = newInstanceOfThis(this.rows, this.columns)
        for (i in this.indices) {
            for (j in this.getRow(i).indices) {
                val value = when (this) {
                    is IntMatrix -> this[i][j] * constant
                    is FloatMatrix -> this[i][j] * constant
                }
                multipliedMatrix.set(i, j, value as T)
            }
        }
        return multipliedMatrix
    }

    operator fun times(other: NumberMatrix<T>): NumberMatrix<T> {
        assertCanMultiply(other)
        val result = newInstanceOfThis(this.rows, other.columns)
        for (i in this.indices) {
            for (j in 0 until other.columns) {
                result[i][j] = (this[i] * other.getColumn(j))
            }
        }
        return result
    }

    private fun assertCanAdd(other: NumberMatrix<T>) {
        if (this.size != other.size) {
            throw IllegalArgumentException("Matrices do not have the same size!")
        }
    }

    private fun assertCanMultiply(other: NumberMatrix<T>) {
        if (this.columns != other.rows) {
            throw IllegalArgumentException("These matrices can't be multiplied!")
        }
    }

    private fun assertIsSquare() {
        if (this.columns != this.rows) {
            throw Exception("Matrix is not a square (1x1, 2x2, 3x3 etc.)!")
        }
    }

    private fun newInstanceOfThis(rows: Int? = null, columns: Int? = null): NumberMatrix<T> {
        return this.javaClass.getDeclaredConstructor(Int::class.java, Int::class.java)
                .newInstance(rows ?: this.rows, columns ?: this.columns)
    }

    private fun newVector(): NumberVector<T> {
        return this[0].javaClass.getDeclaredConstructor(Int::class.java).newInstance(this.rows)
    }
}

class IntMatrix(rows: Int, columns: Int) :
        NumberMatrix<Int>(rows, columns, Array(rows) { IntVector(columns) })

class FloatMatrix(rows: Int, columns: Int) :
        NumberMatrix<Float>(rows, columns, Array(rows) { FloatVector(columns) })

sealed class NumberVector<T : Number>(private var vector: Array<T>) : Iterable<T> {
    val size = vector.size
    val indices = vector.indices
    override fun iterator() = vector.iterator()

    operator fun get(index: Int): T {
        return vector[index]
    }

    operator fun set(index: Int, value: T) {
        vector[index] = value
    }

    fun fill(list: List<T>) {
        for (i in list.indices) {
            this[i] = list[i]
        }
    }

    operator fun times(other: NumberVector<T>): T {
        assertCanMultiply(other)
        return when (this) {
            is IntVector -> this.mapIndexed { index, number: Int -> number * other[index].toInt() }.sum()
            is FloatVector -> this.mapIndexed { index, number: Float -> number * other[index].toFloat() }.sum()
        } as T
    }

    private fun assertCanMultiply(other: NumberVector<T>) {
        if (this.size != other.size) {
            throw IllegalArgumentException("Vectors are not of same size!")
        }
    }
}

class IntVector(size: Int) : NumberVector<Int>(Array(size) { 0 })
class FloatVector(size: Int) : NumberVector<Float>(Array(size) { 0.0F })