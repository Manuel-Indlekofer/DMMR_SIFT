package imageProcessing.implementation

import imageProcessing.ImageProcessor
import math.DoubleMatrix
import util.RGBImageArrayProxy
import kotlin.math.abs

class Subtract(private val other: DoubleMatrix) : ImageProcessor {
    override fun process(input: DoubleMatrix): DoubleMatrix {
        val outMatrix = DoubleMatrix(input.rows, input.columns)

        for (x in 0 until input.columns) {
            for (y in 0 until input.rows) {
                outMatrix[x, y] = input[x, y] - other[x, y]
            }
        }
        return outMatrix
    }
}
