package processor

import java.util.*

val scanner = Scanner(System.`in`)

fun main() {
    val np = NumericProcessor()
    while (!np.exit) {
        np.menu()
    }
}

class NumericProcessor {
    var exit = false
    fun menu() {
        print(  "1. Add matrices\n" +
                "2. Multiply matrix by a constant\n" +
                "3. Multiply matrices\n" +
                "0. Exit\n" +
                "Your choice: ")
        val answer = scanner.nextInt()
        try {
            val matrix: Matrix = when (answer) {
                1 -> createMatrix(1).add(createMatrix(2))
                2 -> createMatrix().multiply()
                3 -> createMatrix(1).multiply(createMatrix(2))
                else -> {
                    exit = true
                    return
                }
            }
            println("The result is:")
            matrix.print()
            println()

        } catch (e: Error) {
            println("The operation cannot be performed.\n")
        }
    }

    private fun createMatrix(count: Int = 0): Matrix {
        var word = ""
        when (count) {
            1 -> word = " first"
            2 -> word = " second"
        }
        print("Enter size of$word matrix: ")
        val a = scanner.nextInt()
        val b = scanner.nextInt()
        print("Enter$word matrix:\n")

        return Matrix(a, b, true)
    }
}

class Matrix {
    val width: Int
    val height: Int
    var matrix: Array<DoubleArray> = emptyArray()
    var lines: Array<String> = emptyArray()
    var line: String = ""
    var number: Double = 0.0
    var isDoublesHere: Boolean

    constructor (rows: Int, columns: Int, usersInput: Boolean = false, doublesHere: Boolean = false) {
        width = columns
        height = rows
        matrix = Array(height) { DoubleArray(width) }
        isDoublesHere = doublesHere

        if (usersInput) {
            while (lines.size != height) {
                line = scanner.nextLine()
                if (line == "") continue
                if (line.contains('.')) isDoublesHere = true
                lines += line
            }

            for (_Y in 0 until height) {
                line = lines[_Y]
                val elements = line.split(" ")
                for (_X in 0 until width) {
                    if (elements[_X] == "") continue
                    setElement(_Y, _X, elements[_X].toDouble())
                }
            }
        }

    }

    fun getElenent(row: Int, column: Int): Double = matrix[row][column]

    fun setElement(row: Int, column: Int, nomber: Double) {
        matrix[row][column] = nomber
    }

    fun add(otherMatrix: Matrix): Matrix {
        if (width != otherMatrix.width || height != otherMatrix.height ) throw Error()

        var newMatrix: Matrix = Matrix(height, width,false, isDoublesHere)
        for (_Y in 0 until height) {
            for (_X in 0 until width) {
                number = getElenent(_Y, _X) + otherMatrix.getElenent(_Y, _X)
                newMatrix.setElement(_Y, _X, number)
            }
        }
        return newMatrix
    }

    fun multiply(): Matrix {
        println("Enter constant: ")
        val constant = scanner.nextLine().toDouble()

        var newMatrix: Matrix = Matrix(height, width, false, isDoublesHere)
        for (_Y in 0 until height) {
            for (_X in 0 until width) {
                number = getElenent(_Y, _X) * constant
                newMatrix.setElement(_Y, _X, number)
            }
        }
        return newMatrix
    }

    fun multiply(otherMatrix: Matrix): Matrix {
        if (width != otherMatrix.height )  throw Error()

        val newMatrix: Matrix = Matrix(height, otherMatrix.width, false, isDoublesHere)
        for (_Y in 0 until height) {
            for (_X in 0 until otherMatrix.width) {
                number = multiplyElements(_Y, _X, otherMatrix)
                newMatrix.setElement(_Y, _X, number)
            }
        }
        return newMatrix
    }

    private fun multiplyElements(_Y: Int, _X: Int, otherMatrix: Matrix): Double {
        var result: Double = 0.0
        for (i in 0 until width) {
            result += getElenent(_Y, i) * otherMatrix.getElenent(i, _X)
        }
        return result
    }

    fun print() {
        var output = ""
        for (_Y in 0 until height) {
            for (_X in 0 until width) {
                if (isDoublesHere)
                    output += "" + getElenent(_Y, _X) + " "
                else
                    output += "" + getElenent(_Y, _X).toInt() + " "
            }
            output += "\n"
        }
        print(output)
    }
}