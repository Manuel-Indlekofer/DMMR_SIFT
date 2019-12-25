package imageProcessing.implementation

import imageProcessing.ImageProcessor
import math.Matrix3d
import util.RGBImageArrayProxy
import java.awt.image.BufferedImage
import kotlin.math.sign

open class Convolution(private val convolutionMatrix: Matrix3d) : ImageProcessor {


    override fun process(input: RGBImageArrayProxy): RGBImageArrayProxy {
        val outputImage = RGBImageArrayProxy(BufferedImage(input.width, input.height, BufferedImage.TYPE_INT_RGB))
        for (pivotX in 0 until input.width) {
            for (pivotY in 0 until input.height) {
                val sourceMatrix3d = Matrix3d(convolutionMatrix.size)


                for (x in 0 until sourceMatrix3d.size) {
                    for (y in 0 until sourceMatrix3d.size) {
                        var targetX = pivotX + (x - convolutionMatrix.size / 2)
                        var targetY = pivotY + (y - convolutionMatrix.size / 2)

                        if (targetX < 0) {
                            targetX = 0
                        } else if (targetX > input.width - 1) {
                            targetX = input.width - 1
                        }

                        if (targetY < 0) {
                            targetY = 0
                        } else if (targetY > input.height - 1) {
                            targetY = input.height - 1
                        }
                        sourceMatrix3d[x, y] = input.bufferedImage.raster.getPixel(targetX, targetY, IntArray(3))[0].toDouble()
                    }
                }

                outputImage[pivotX, pivotY] = IntArray(3) {
                    sourceMatrix3d.timesComponentWise(convolutionMatrix).sum.toInt() % 255
                }

            }
        }
        return outputImage
    }
}
