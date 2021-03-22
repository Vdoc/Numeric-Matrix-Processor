package processor

import java.util.*

val scanner = Scanner(System.`in`)

fun main() {
    val m1 = Matrix(scanner.nextInt(), scanner.nextInt(), true)
//    val m2 = Matrix(scanner.nextInt(), scanner.nextInt(), true)

    try {
//        val m3 = m1.sum(m2)
        val m3 = m1.multiply(scanner.nextInt())
        m3.print()
    } catch (e: Error) {
        println("ERROR")
    }
}

class Matrix {
    val width: Int
    val height: Int
    val m: Array<IntArray>

    constructor (Rows: Int, Cols: Int, readInput: Boolean = false) {
        width = Cols
        height = Rows
        m = Array(height) { IntArray(width) }
        if (readInput) {
            for (_Y in 0 until height) {
                for (_X in 0 until width) {
                    setElement(_Y, _X, scanner.nextInt())
                }
            }
        }
    }

    fun getElenent(row: Int, column: Int): Int = m[row][column]

    fun setElement(row: Int, column: Int, nomber: Int) {
        m[row][column] = nomber
    }

    fun sum(m2: Matrix): Matrix {
        val m3 = Matrix(height, width)
        if (width != m2.width || height != m2.height) {
            throw Error()
        }
        for (_Y in 0 until height) {
            for (_X in 0 until width) {
                m3.setElement(_Y, _X, getElenent(_Y, _X) + m2.getElenent(_Y, _X))
            }
        }
        return m3
    }

    fun print() {
        var output = ""
        for (_Y in 0 until height) {
            for (_X in 0 until width) {
                output += "" + getElenent(_Y, _X) + " "
            }
            output += "\n"
        }
        print(output)
    }

    fun multiply(constant: Int): Matrix {
        val m3 = Matrix(height, width)
        for (_Y in 0 until height) {
            for (_X in 0 until width) {
                m3.setElement(_Y, _X, getElenent(_Y, _X) * constant)
            }
        }
        return m3
    }

}