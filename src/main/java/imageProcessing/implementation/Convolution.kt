package imageProcessing.implementation

import imageProcessing.ImageProcessor
import math.DoubleMatrix
import util.RGBImageArrayProxy
import java.awt.image.BufferedImage

open class Convolution(private val convolutionMatrix: DoubleMatrix) : ImageProcessor {


    override fun process(input: DoubleMatrix): DoubleMatrix {
        val outputMatrix = DoubleMatrix(input.rows, input.columns)
        for (pivotX in 0 until input.columns) {
            for (pivotY in 0 until input.rows) {
                val sourceMatrix3d = DoubleMatrix(convolutionMatrix.rows, convolutionMatrix.columns)


                for (x in 0 until sourceMatrix3d.columns) {
                    for (y in 0 until sourceMatrix3d.rows) {
                        var targetX = pivotX + (x - convolutionMatrix.columns / 2)
                        var targetY = pivotY + (y - convolutionMatrix.rows / 2)

                        if (targetX < 0) {
                            targetX = 0
                        } else if (targetX > input.columns - 1) {
                            targetX = input.columns - 1
                        }

                        if (targetY < 0) {
                            targetY = 0
                        } else if (targetY > input.rows - 1) {
                            targetY = input.rows - 1
                        }
                        sourceMatrix3d[x, y] = input[targetX, targetY]
                    }
                }

                val sum = sourceMatrix3d.timesComponentWise(convolutionMatrix).sum
                if (sum > 1.0) {

                    outputMatrix[pivotX, pivotY] = 1.0
                } else {
                    outputMatrix[pivotX, pivotY] = sum
                }
            }
        }
        return outputMatrix
    }
}
