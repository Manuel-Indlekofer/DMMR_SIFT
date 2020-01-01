package imageProcessing.implementation

import math.DoubleMatrix
import util.RGBImageArrayProxy
import util.copy

object Normalizer {
    fun normalizeImage(image: RGBImageArrayProxy): DoubleMatrix {
        val input = RGBImageArrayProxy(image.bufferedImage.copy())
        input.forEachPixel { x, y ->
            val avg = input[x, y].reduce { acc, i -> acc + i } / input[x, y].size
            return@forEachPixel intArrayOf(avg, avg, avg)
        }
        val width = input.width
        val height = input.height
        val matrix = DoubleMatrix(height, width)
        for (x in 0 until width) {
            for (y in 0 until height) {
                matrix[x, y] = input[x, y][0].toDouble() / 255.0
            }
        }
        return matrix
    }
}
