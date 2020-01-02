package model

import math.DoubleMatrix

open class GaussianPyramid(val numberOfOctaves: Int, val numberOfScales: Int) {

    private val pyramid: Array<Array<DoubleMatrix>> = Array(numberOfOctaves) {
        Array(numberOfScales) {
            DoubleMatrix(5, 5)
        }
    }

    private val deltas : Array<Double> = Array(numberOfOctaves){
        0.0
    }

    fun getImage(octave: Int, scale: Int): DoubleMatrix {
        return pyramid[octave][scale]
    }

    fun setImage(octave: Int, scale: Int, image: DoubleMatrix) {
        pyramid[octave][scale] = image
    }

    fun forEach(action: (DoubleMatrix) -> (Unit)) {
        for (octave in 0 until numberOfOctaves) {
            for (scale in 0 until numberOfScales) {
                action.invoke(pyramid[octave][scale])
            }
        }
    }

    class DifferenceOfGaussiansPyramid(numberOfOctaves: Int, numberOfScales: Int) : GaussianPyramid(numberOfOctaves, numberOfScales)
}