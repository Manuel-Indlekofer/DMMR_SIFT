package steps

import imageProcessing.implementation.GaussianBlur
import math.BilinearInterpolation
import math.DoubleMatrix
import model.GaussianPyramid
import kotlin.math.pow
import kotlin.math.sqrt


class GaussianScaleSpace(val numberOfOctaves: Int = 6,
                         val numberOfScales: Int = 3,
                         val minBlurSigma: Double = 0.8,
                         val minInterPixelDistance: Double = 0.5,
                         val initialBlur: Double = 0.5
) : Step<DoubleMatrix, GaussianPyramid> {
    override fun process(input: DoubleMatrix): GaussianPyramid {
        val gaussianPyramid = GaussianPyramid(numberOfOctaves, numberOfScales + 2)
        val seedImage = BilinearInterpolation.interpolate(input, minInterPixelDistance)
        val initialBlur = 1.0 / minInterPixelDistance * sqrt(minBlurSigma.pow(2) - initialBlur.pow(2))
        println("\u001B[36m [GaussianScaleSpace] \u001B[0m Blurring image of Octave 0 and Scale 0 with sigma $initialBlur ...")
        gaussianPyramid.setImage(0, 0, GaussianBlur(initialBlur, (initialBlur * 8).toInt()).process(seedImage))

        for (scale in 1 until numberOfScales + 2) {
            val sigma = minBlurSigma / minInterPixelDistance * sqrt(2.0.pow(2 * scale.toDouble() / numberOfScales) - 2.0.pow((2 * (scale - 1)) / numberOfScales.toDouble()))
            println("\u001B[36m [GaussianScaleSpace] \u001B[0m Blurring image of Octave 0 and Scale $scale with sigma $sigma ...")
            gaussianPyramid.setImage(0, scale, GaussianBlur(sigma, (sigma * 8).toInt()).process(gaussianPyramid.getImage(0, scale - 1).copy))
        }

        for (octave in 1 until numberOfOctaves) {
            gaussianPyramid.setImage(octave, 0, BilinearInterpolation.interpolate(gaussianPyramid.getImage(octave - 1, numberOfScales).copy, 1 / minInterPixelDistance))
            for (scale in 1 until numberOfScales + 2) {
                val sigma = minBlurSigma / minInterPixelDistance * sqrt(2.0.pow(2 * scale.toDouble() / numberOfScales) - 2.0.pow(2 * (scale - 1) / numberOfScales.toDouble()))
                println("\u001B[36m [GaussianScaleSpace] \u001B[0m Blurring image of Octave $octave and Scale $scale with sigma $sigma ...")
                gaussianPyramid.setImage(octave, scale, GaussianBlur(sigma, (sigma * 8).toInt()).process(gaussianPyramid.getImage(octave, scale - 1).copy))
            }

        }
        return gaussianPyramid
    }
}