package model

import math.DoubleMatrix
import math.GaussianProvider
import kotlin.math.roundToInt
import kotlin.math.sqrt

class OrientationAssignment(private val keypoints: Array<ArrayList<Keypoint>>, private val doGPyramid: Array<Array<DoubleMatrix>>) {


    fun assignOrientation() {
        for (octave in keypoints) {
            for (keyPoint in octave) {
                val scale = keyPoint.scale.roundToInt().coerceIn(0, doGPyramid[0].size)
                val x = keyPoint.x
                val y = keyPoint.y
                val sigma = scale * 1.5
                val kernel = GaussianProvider.getGaussianMatrix(sigma, 6 * sigma.toInt())
            }
        }
    }

   
}