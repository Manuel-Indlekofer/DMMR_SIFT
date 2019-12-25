package math

import java.lang.StringBuilder


class Matrix3d(val size: Int = 3) {

    private var matrix: Array<DoubleArray>


    val width: Int
        get() {
            return matrix[0].size
        }

    val height: Int
        get() {
            return matrix.size
        }

    val sum: Double
        get() {
            var result = 0.0
            for (i in 0 until size) {
                for (j in 0 until size) {
                    result += matrix[i][j]
                }
            }
            return result
        }

    init {
        matrix = Array(size) {
            return@Array DoubleArray(size)
        }
    }


    constructor(array: Array<DoubleArray>, size: Int) : this(size) {
        if (array.size != size) {
            return
        }

        for (i in array.indices) {
            if (array[i].size != size) {
                return
            }
        }
        matrix = array
    }


    operator fun times(second: Matrix3d): Matrix3d {
        val result = Array(size) {
            return@Array DoubleArray(size) {
                return@DoubleArray 0.0
            }
        }

        for (i in 0 until size) {
            for (j in 0 until size) {
                for (k in 0 until size) {
                    result[i][j] += this[i, k] * second[k, j]
                }
            }
        }
        return Matrix3d(result, size)
    }

    fun timesComponentWise(second: Matrix3d): Matrix3d {
        val result = Array(size) {
            return@Array DoubleArray(size) {
                return@DoubleArray 0.0
            }
        }
        for (i in 0 until size) {
            for (j in 0 until size) {
                result[i][j] = matrix[i][j] * second.matrix[i][j]
            }
        }
        return Matrix3d(result, size)
    }

    operator fun get(i: Int, j: Int): Double {
        return matrix[i][j]
    }

    operator fun set(i: Int, j: Int, value: Double) {
        matrix[i][j] = value
    }


    override fun toString(): String {
        val output: StringBuilder = StringBuilder("[")

        for ((lineIndex, line) in matrix.withIndex()) {
            for ((numberIndex, number) in line.withIndex()) {
                output.append(when (numberIndex) {
                    0 -> {
                        "[$number, "
                    }
                    line.size - 1 -> {
                        if (lineIndex == matrix.size - 1) {
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
            if (lineIndex != line.size - 1) {
                output.append("\n")
            }
        }
        output.append("]")
        return output.toString()

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix3d) return false

        if (!matrix.contentDeepEquals(other.matrix)) return false

        return true
    }

    override fun hashCode(): Int {
        return matrix.contentDeepHashCode()
    }
}