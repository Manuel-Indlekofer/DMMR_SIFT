package math

import org.apache.commons.math3.linear.EigenDecomposition
import org.apache.commons.math3.linear.LUDecomposition
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix


class DoubleMatrix {

    var matrix: RealMatrix
        private set


    val columns: Int
        get() {
            return matrix.columnDimension
        }

    val copy: DoubleMatrix
        get() {
            return DoubleMatrix(matrix.copy())
        }
    val trace: Double
        get() {
            return matrix.trace
        }

    val rows: Int
        get() {
            return matrix.rowDimension
        }

    val eigenvalues: DoubleArray
        get() {
            return EigenDecomposition(matrix).realEigenvalues
        }

    val sum: Double
        get() {
            var result = 0.0
            for (i in 0 until columns) {
                for (j in 0 until rows) {
                    result += matrix.getEntry(i, j)
                }
            }
            return result
        }

    val transposed: DoubleMatrix
        get() {
            return DoubleMatrix(matrix.copy().transpose())
        }

    val determinant: Double
        get() {
            return LUDecomposition(matrix).determinant
        }

    val inverse: DoubleMatrix
        get() {
            val inverseData = LUDecomposition(matrix).solver.inverse
            return DoubleMatrix(inverseData.data)
        }


    constructor(initRows: Int = 3, initColumns: Int = 3) {
        matrix = MatrixUtils.createRealMatrix(initRows, initColumns)
    }


    constructor(array: Array<DoubleArray>) : this() {

        matrix = MatrixUtils.createRealMatrix(array)
    }

    private constructor(matrix: RealMatrix) {
        this.matrix = matrix.copy()
    }

    private constructor(matrix: DoubleMatrix) {
        this.matrix = matrix.matrix.copy()
    }


    operator fun times(second: DoubleMatrix): DoubleMatrix {
        return DoubleMatrix(matrix.preMultiply(second.matrix))
    }

    operator fun get(i: Int, j: Int): Double {
        return matrix.getEntry(j, i)
    }

    operator fun set(i: Int, j: Int, value: Double) {
        matrix.setEntry(j, i, value)
    }

    fun timesComponentWise(second: DoubleMatrix): DoubleMatrix {
        val result = DoubleMatrix(second)
        for (i in 0 until result.rows) {
            for (j in 0 until result.columns) {
                result.matrix.setEntry(i, j, matrix.getEntry(i, j) * second.matrix.getEntry(i, j))
            }
        }
        return result
    }

    fun getSubMatrix(startRow: Int, endRow: Int, startCol: Int, endCol: Int): DoubleMatrix {
        return DoubleMatrix(matrix.getSubMatrix(startRow, endRow, startCol, endCol))
    }

    override fun toString(): String {
        val output: StringBuilder = StringBuilder("[")

        for (lineIndex in 0 until matrix.rowDimension) {
            for (numberIndex in 0 until matrix.columnDimension) {
                val number = matrix.getEntry(lineIndex, numberIndex)
                output.append(when {
                    numberIndex == 0 -> {
                        "[$number, "
                    }
                    numberIndex == matrix.columnDimension - 1 -> {
                        if (lineIndex == matrix.rowDimension - 1) {
                            "$number]"
                        } else {
                            "$number],"
                        }
                    }
                    else -> {
                        "$number, "
                    }
                })
            }
            if (lineIndex != matrix.rowDimension - 1) {
                output.append("\n")
            }
        }
        output.append("]")
        return output.toString()

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DoubleMatrix) return false
        if (matrix != other.matrix) return false
        return true
    }

    override fun hashCode(): Int {
        return matrix.hashCode()
    }
}