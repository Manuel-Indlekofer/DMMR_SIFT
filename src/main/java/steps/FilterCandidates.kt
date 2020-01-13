package steps

import math.DoubleMatrix
import model.GaussianPyramid
import kotlin.math.abs
import kotlin.math.pow

class FilterCandidates(private val dOGPyramid: GaussianPyramid.DifferenceOfGaussiansPyramid) : Step<SubPixelPrecisionExtractor.CandidateKeypoints, FilterCandidates.Keypoints> {

    data class Keypoints(val keypoints: ArrayList<Keypoint>) {
        data class Keypoint(val octave: Int, val scale: Int, val x: Int, val y: Int, val interpolatedScale: Double, val interpolatedX: Double, val interpolatedY: Double, val interpolatedValue: Double)
    }

    override fun process(input: SubPixelPrecisionExtractor.CandidateKeypoints): Keypoints {

        val result = Keypoints(ArrayList())

        //contrast filtering
        val filteredKeypoints = input.candidates.filter {
            abs(it.interpolatedValue) >= 0.015
        }

        //edge filtering
        filteredKeypoints.filter {
            val hessian = calculateHessian(it)
            hessian.trace.pow(2.0) / hessian.determinant < (10 + 1.0).pow(2.0) / 10.0
        }.map { candidateKeypoint ->
            Keypoints.Keypoint(candidateKeypoint.octave, candidateKeypoint.scale, candidateKeypoint.x, candidateKeypoint.y, candidateKeypoint.interpolatedScale, candidateKeypoint.interpolatedX, candidateKeypoint.interpolatedY, candidateKeypoint.interpolatedValue)
        }.toCollection(result.keypoints)

        println("\u001B[33m [KeypointFilter] \u001B[0m found a total of ${result.keypoints.size} keypoints!")
        return result
    }

    private fun calculateHessian(extremum: SubPixelPrecisionExtractor.CandidateKeypoints.CandidateKeypoint): DoubleMatrix {
        val x = extremum.x
        val y = extremum.y
        val octave = extremum.octave
        val scale = extremum.scale

        val h11 = dOGPyramid.getImage(octave, scale + 1)[x, y] + dOGPyramid.getImage(octave, scale - 1)[x, y] - 2.0 * dOGPyramid.getImage(octave, scale)[x, y]
        val h12 = (dOGPyramid.getImage(octave, scale + 1)[x + 1, y] - dOGPyramid.getImage(octave, scale + 1)[x - 1, y] - dOGPyramid.getImage(octave, scale - 1)[x + 1, y] + dOGPyramid.getImage(octave, scale - 1)[x - 1, y]) / 4.0
        val h22 = dOGPyramid.getImage(octave, scale)[x + 1, y] + dOGPyramid.getImage(octave, scale)[x - 1, y] - 2.0 * dOGPyramid.getImage(octave, scale)[x, y]
        val h13 = (dOGPyramid.getImage(octave, scale + 1)[x, y + 1] - dOGPyramid.getImage(octave, scale + 1)[x, y - 1] - dOGPyramid.getImage(octave, scale - 1)[x, y + 1] + dOGPyramid.getImage(octave, scale - 1)[x, y - 1]) / 4.0
        val h33 = dOGPyramid.getImage(octave, scale)[x, y + 1] + dOGPyramid.getImage(octave, scale)[x, y - 1] - 2.0 * dOGPyramid.getImage(octave, scale)[x, y]
        val h23 = (dOGPyramid.getImage(octave, scale)[x + 1, y + 1] - dOGPyramid.getImage(octave, scale)[x + 1, y - 1] - dOGPyramid.getImage(octave, scale)[x - 1, y + 1] + dOGPyramid.getImage(octave, scale)[x - 1, y - 1]) / 4.0

        val hessian = DoubleMatrix(3, 3)
        hessian[0, 0] = h11
        hessian[1, 0] = h12
        hessian[2, 0] = h13
        hessian[0, 1] = h12
        hessian[1, 1] = h22
        hessian[2, 1] = h23
        hessian[0, 2] = h13
        hessian[1, 2] = h23
        hessian[2, 2] = h33
        return hessian
    }

}