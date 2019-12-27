package model

import imageProcessing.implementation.GaussianBlur
import imageProcessing.implementation.Scale
import imageProcessing.implementation.Subtract
import util.RGBImageArrayProxy
import visualization.Visualization
import kotlin.math.pow
import kotlin.math.sqrt


class DifferenceOfGaussians(private val startSigma: Double = 1.6,
                            private val numberOfOctaves: Int = 4,
                            private val numberOfScaleLevels: Int = 5,
                            private val k: Double = sqrt(2.0)) {

    private val octaves: Array<Array<RGBImageArrayProxy?>> = Array(numberOfOctaves) {
        arrayOfNulls<RGBImageArrayProxy>(numberOfScaleLevels)
    }

    private val differences: Array<Array<RGBImageArrayProxy?>> = Array(numberOfOctaves) {
        arrayOfNulls<RGBImageArrayProxy>(numberOfScaleLevels - 1)
    }


    fun calculateGaussianPyramid(input: RGBImageArrayProxy): Array<Array<RGBImageArrayProxy?>> {
        var currentScale = 1.0
        for (octave in 0 until numberOfOctaves) {
            for (scale in 0 until numberOfScaleLevels) {
                val currentSigma = (startSigma * (2.0.pow(1.0 / numberOfScaleLevels))) * (scale + 1)
                octaves[octave][scale] = GaussianBlur(currentSigma, (6 * currentSigma).toInt()).process(Scale(currentScale).process(input))
                Visualization().showImage(octaves[octave][scale]!!.bufferedImage)
            }
            currentScale *= 0.5
        }

        for (octave in 0 until numberOfOctaves) {
            for (scale in 0 until numberOfScaleLevels - 1) {
                differences[octave][scale] = Subtract(octaves[octave][scale + 1]!!).process(octaves[octave][scale]!!)
                Visualization().showImage(differences[octave][scale]!!.bufferedImage)
            }
        }
        return differences
    }

}