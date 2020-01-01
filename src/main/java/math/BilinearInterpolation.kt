package math

import kotlin.math.floor

object BilinearInterpolation {
    fun interpolate(inputImage: DoubleMatrix, interPixelDistance: Double): DoubleMatrix {
        val sym = Symmetrization(inputImage.columns, inputImage.rows)
        val outputMatrix = DoubleMatrix((inputImage.rows / interPixelDistance).toInt(), (inputImage.columns / interPixelDistance).toInt())
        for (x in 0 until outputMatrix.columns) {
            for (y in 0 until outputMatrix.rows) {
                val scaledX = x * interPixelDistance
                val scaledY = y * interPixelDistance
                val firstSym = sym.sym(scaledX.toInt() + 1, scaledY.toInt() + 1)
                val secondSym = sym.sym(scaledX.toInt(), scaledY.toInt() + 1)
                val thirdSym = sym.sym(scaledX.toInt() + 1, scaledY.toInt())
                val fourthSym = sym.sym(scaledX.toInt(), scaledY.toInt())
                outputMatrix[x, y] = (scaledX - floor(scaledX)) * (scaledY - floor(scaledY)) * inputImage[firstSym.first, firstSym.second] +
                        (1 + floor(scaledX) - scaledX) * (scaledY - floor(scaledY)) * inputImage[secondSym.first, secondSym.second] +
                        (scaledX - floor(scaledX)) * (1 + floor(scaledY) - scaledY) * inputImage[thirdSym.first, thirdSym.second] +
                        (1 + floor(scaledX) - scaledX) * (1 + floor(scaledY) - scaledY) * inputImage[fourthSym.first, fourthSym.second]
            }
        }
        return outputMatrix
    }
}