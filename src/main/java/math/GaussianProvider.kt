package math

import kotlin.math.PI
import kotlin.math.exp

object GaussianProvider {

    fun getGaussianKernelValue(x: Int, y: Int, sigma: Double): Double {
        return (1.0 / (2.0 * PI * sigma * sigma)) * exp(-((x * x + y * y) / (2.0 * sigma * sigma)))
    }

    fun getGaussianMatrix(sigma: Double, size: Int): DoubleMatrix {
        val array = Array(size) { x ->
            return@Array DoubleArray(size) { y ->
                return@DoubleArray getGaussianKernelValue(x - size / 2, y - size / 2, sigma)
            }
        }
        val matrix3d = DoubleMatrix(array)
        val sum = matrix3d.sum

        for (x in 0 until matrix3d.columns) {
            for (y in 0 until matrix3d.rows) {
                matrix3d[x, y] *= 1.0 / sum
            }
        }
        return matrix3d
    }


}