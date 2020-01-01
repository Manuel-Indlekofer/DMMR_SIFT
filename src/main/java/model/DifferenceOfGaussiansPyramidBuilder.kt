package model

import imageProcessing.implementation.GaussianBlur
import imageProcessing.implementation.DownSampler
import imageProcessing.implementation.Subtract
import math.DoubleMatrix
import util.RGBImageArrayProxy
import visualization.Visualization
import kotlin.math.pow


class DifferenceOfGaussiansPyramidBuilder(private val startSigma: Double = 1.6,
                                          private val numberOfOctaves: Int = 4,
                                          private val numberOfScaleLevels: Int = 5) {

    private val octaves: Array<Array<DoubleMatrix?>> = Array(numberOfOctaves) {
        arrayOfNulls<DoubleMatrix>(numberOfScaleLevels)
    }

    private val differences: Array<Array<DoubleMatrix?>> = Array(numberOfOctaves) {
        arrayOfNulls<DoubleMatrix>(numberOfScaleLevels - 1)
    }

    val sigmaLevels: Array<Double>
        get() {
            val tempArray = Array<Double>(_sigmaLevels.size) {
                0.0
            }
            _sigmaLevels.toArray(tempArray)
            return tempArray
        }

    private val _sigmaLevels = ArrayList<Double>()


    fun calculateGaussianPyramid(input: DoubleMatrix): Array<Array<DoubleMatrix>> {
        _sigmaLevels.clear()
        var currentScale = 1.0
        for (octave in 0 until numberOfOctaves) {
            for (scale in 0 until numberOfScaleLevels) {
                var currentSigma = startSigma
                if (scale != 0) {
                    currentSigma = startSigma * (2.0.pow(scale.toDouble() / numberOfScaleLevels))
                }
                octaves[octave][scale] = GaussianBlur(currentSigma, (6 * currentSigma).toInt()).process(DownSampler(currentScale).process(input))
                _sigmaLevels.add(currentSigma)
                Visualization().showImage(RGBImageArrayProxy(octaves[octave][scale]!!).bufferedImage)
            }
            currentScale *= 0.5
        }

        for (octave in 0 until numberOfOctaves) {
            for (scale in 0 until numberOfScaleLevels - 1) {
                differences[octave][scale] = Subtract(octaves[octave][scale + 1]!!).process(octaves[octave][scale]!!)
                Visualization().showImage(RGBImageArrayProxy(differences[octave][scale]!!).bufferedImage)
            }
        }
        return differences as Array<Array<DoubleMatrix>>
    }

}