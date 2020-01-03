package steps

import math.DoubleMatrix
import model.GaussianPyramid
import kotlin.math.*

class OrientationAssignment(private val dOGPyramid: GaussianPyramid.DifferenceOfGaussiansPyramid) : Step<FilterCandidates.Keypoints, OrientationAssignment.OrientedKeypoints> {

    data class OrientedKeypoints(val orientedKeypoints: ArrayList<OrientedKeypoint>) {
        data class OrientedKeypoint(val octave: Int, val scale: Int, val x: Int, val y: Int, val interpolatedScale: Double, val interpolatedX: Double, val interpolatedY: Double, val interpolatedValue: Double, val orientationTheta: Double)
    }

    private val xGradient = Gradient()
    private val yGradient = Gradient()

    companion object {
        private const val lambdaOrientation: Double = 1.5
        private const val numberOfBins = 36
    }

    inner class Gradient {

        private val gradients: Array<Array<DoubleMatrix>> = Array(dOGPyramid.numberOfOctaves) { octave ->
            Array(dOGPyramid.numberOfScales) { scale ->
                DoubleMatrix(dOGPyramid.getImage(octave, scale).rows, dOGPyramid.getImage(octave, scale).columns)
            }
        }

        fun setGradient(octave: Int, scale: Int, x: Int, y: Int, gradientValue: Double) {
            gradients[octave][scale][x, y] = gradientValue
        }

        fun getGradient(octave: Int, scale: Int, x: Int, y: Int): Double {
            return gradients[octave][scale][x, y]
        }

    }


    override fun process(input: FilterCandidates.Keypoints): OrientedKeypoints {

        computeGradients()

        val result = OrientedKeypoints(ArrayList())

        for (keypoint in input.keypoints) {
            processKeypoint(keypoint)?.let {
                result.orientedKeypoints.addAll(it)
            }
        }
        return result
    }

    private fun processKeypoint(keypoint: FilterCandidates.Keypoints.Keypoint): ArrayList<OrientedKeypoints.OrientedKeypoint>? {
        if (!(3 * lambdaOrientation * keypoint.interpolatedScale <= keypoint.x && keypoint.x <= dOGPyramid.getImage(keypoint.octave, keypoint.scale).columns - 3.0 * lambdaOrientation * keypoint.interpolatedScale &&
                        3 * lambdaOrientation * keypoint.interpolatedScale <= keypoint.y && keypoint.y <= dOGPyramid.getImage(keypoint.octave, keypoint.scale).rows - 3.0 * lambdaOrientation * keypoint.interpolatedScale)) {
            return null
        }

        val orientationHistogram = Array(numberOfBins) {
            0.0
        }

        val interPixelDistance = 0.5 * 2.0.pow(keypoint.octave)

        for (x in ((keypoint.x - 3.0 * lambdaOrientation * keypoint.interpolatedScale) / interPixelDistance).roundToInt()..((keypoint.x + 3.0 * lambdaOrientation * keypoint.interpolatedScale) / interPixelDistance).roundToInt()) {
            for (y in ((keypoint.y - 3.0 * lambdaOrientation * keypoint.interpolatedScale) / interPixelDistance).roundToInt()..((keypoint.y + 3.0 * lambdaOrientation * keypoint.interpolatedScale) / interPixelDistance).roundToInt()) {
                val contribution = exp(-(sqrt((x * interPixelDistance - keypoint.x).pow(2.0) + (y * interPixelDistance - keypoint.y).pow(2.0)).pow(2.0) / 2 * (lambdaOrientation * keypoint.interpolatedScale).pow(2.0))) * sqrt(xGradient.getGradient(keypoint.octave, keypoint.scale, x, y).pow(2.0) + yGradient.getGradient(keypoint.octave, keypoint.scale, x, y).pow(2.0))
                val binIndex = (numberOfBins.toDouble() / 2 * Math.PI * (atan2(xGradient.getGradient(keypoint.octave, keypoint.scale, x, y), yGradient.getGradient(keypoint.octave, keypoint.scale, x, y)) % 2 * Math.PI)).roundToInt()
                orientationHistogram[binIndex] += contribution
            }
        }

        smoothHistogram(orientationHistogram)

        val listOfKeypoints = ArrayList<OrientedKeypoints.OrientedKeypoint>()

        for (index in orientationHistogram.indices) {
            val previousIndex = (index - 1) % orientationHistogram.size
            val nextIndex = (index + 1) % orientationHistogram.size
            if (orientationHistogram[index] > orientationHistogram[previousIndex] && orientationHistogram[index] > orientationHistogram[nextIndex] && orientationHistogram[index] >= 0.8 * orientationHistogram.max()!!) {
                val theta = 2 * Math.PI * index + Math.PI / numberOfBins * ((orientationHistogram[previousIndex] - orientationHistogram[nextIndex]) / (orientationHistogram[previousIndex] - 2.0 * orientationHistogram[index] + orientationHistogram[nextIndex]))
                listOfKeypoints.add(OrientedKeypoints.OrientedKeypoint(keypoint.octave, keypoint.scale, keypoint.x, keypoint.y, keypoint.interpolatedScale, keypoint.interpolatedX, keypoint.interpolatedY, keypoint.interpolatedValue, theta))
            }
        }

        return listOfKeypoints
    }


    private fun smoothHistogram(histogram: Array<Double>) {
        for (iteration in 0 until 6) {
            val tempArray = histogram.copyOf()
            for (index in histogram.indices) {
                val previousIndex = (index - 1) % histogram.size
                val nextIndex = (index + 1) % histogram.size
                histogram[index] = (histogram[previousIndex] + histogram[index] + histogram[nextIndex]) / 3.0
            }
        }
    }


    private fun computeGradients() {
        for (octave in 0 until dOGPyramid.numberOfOctaves) {
            for (scale in 0 until dOGPyramid.numberOfScales) {
                for (x in 1 until dOGPyramid.getImage(octave, scale).columns - 1) {
                    for (y in 1 until dOGPyramid.getImage(octave, scale).rows - 1) {
                        xGradient.setGradient(octave, scale, x, y, (dOGPyramid.getImage(octave, scale)[x + 1, y] - dOGPyramid.getImage(octave, scale)[x - 1, y]) / 2.0)
                        yGradient.setGradient(octave, scale, x, y, (dOGPyramid.getImage(octave, scale)[x, y + 1] - dOGPyramid.getImage(octave, scale)[x, y - 1]) / 2.0)
                    }
                }
            }
        }

    }
}
