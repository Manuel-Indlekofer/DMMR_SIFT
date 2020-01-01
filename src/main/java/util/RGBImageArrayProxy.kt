package util

import math.DoubleMatrix
import java.awt.image.BufferedImage
import java.io.File
import java.lang.IllegalArgumentException
import javax.imageio.ImageIO
import kotlin.math.abs

class RGBImageArrayProxy {

    constructor(f: File) {
        bufferedImage = ImageIO.read(f)
    }

    constructor(bufferedImageInput: BufferedImage) {
        bufferedImage = bufferedImageInput
    }

    constructor(width: Int, height: Int) {
        bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    }

    constructor(matrix: DoubleMatrix) {
        bufferedImage = BufferedImage(matrix.columns, matrix.rows, BufferedImage.TYPE_INT_RGB)
        val raster = bufferedImage.raster
        for (x in 0 until matrix.columns) {
            for (y in 0 until matrix.rows) {
                for (i in 0 until 3) {
                    val pixelValue = abs((matrix[x, y] * 255).toInt())
                    raster.setPixel(x, y, intArrayOf(pixelValue, pixelValue, pixelValue))
                }
            }
        }
    }

    val matrix: DoubleMatrix
        get() {
            val raster = bufferedImage.raster
            val matrix = DoubleMatrix(height, width)
            for (x in 0 until matrix.columns) {
                for (y in 0 until matrix.rows) {
                    val pixelValues = IntArray(3)
                    raster.getPixel(x, y, pixelValues)
                    matrix[x, y] = pixelValues[0] / 255.0
                }
            }
            return matrix
        }

    val bufferedImage: BufferedImage

    val width: Int
        get() = bufferedImage.width

    val height: Int
        get() = bufferedImage.height

    operator fun get(x: Int, y: Int): IntArray {
        return bufferedImage.raster.getPixel(x, y, IntArray(3))
    }

    operator fun set(x: Int, y: Int, rgb: IntArray) {
        if (rgb.size != 3) {
            throw IllegalArgumentException()
        }
        bufferedImage.raster.setPixel(x, y, rgb)
    }

    fun forEachPixel(function: (x: Int, y: Int) -> IntArray): RGBImageArrayProxy {
        val result = RGBImageArrayProxy(bufferedImage.copy())
        for (x in 0 until width) {
            for (y in 0 until height) {
                result[x, y] = function.invoke(x, y)
            }
        }
        return result
    }
}
