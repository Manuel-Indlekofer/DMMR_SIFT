package model

import math.DoubleMatrix
import kotlin.math.abs
import kotlin.math.pow


data class Keypoint(val x: Double, val y: Double, val scale: Double, val octave: Int)

class KeypointSubpixelExtractor(private val doGPyramid: Array<Array<DoubleMatrix>>, private val extremumPoints: Array<Array<Array<Array<Boolean>>>>) {

    companion object {
        const val CONTRAST_DISCARD_THRESHOLD = 0.03
    }

    private val keypoints: Array<ArrayList<Keypoint>> = Array(doGPyramid.size) {
        ArrayList<Keypoint>()
    }

    fun process() {
        for (octave in extremumPoints.indices) {
            for (scale in extremumPoints[octave].indices) {
                for (x in extremumPoints[octave][scale].indices) {
                    for (y in extremumPoints[octave][scale][x].indices) {
                        if (extremumPoints[octave][scale][x][y]) {
                            processPossibleKeypoint(x, y, octave, scale)
                        }
                    }
                }
            }
        }
    }


    private fun processPossibleKeypoint(x: Int, y: Int, octave: Int, scale: Int) {
        val dx = (doGPyramid[octave][scale][x + 1, y] - doGPyramid[octave][scale][x - 1, y]) / 2.0
        val dy = (doGPyramid[octave][scale][x, y + 1] - doGPyramid[octave][scale][x, y - 1]) / 2.0
        val ds = (doGPyramid[octave][scale + 1][x, y] - doGPyramid[octave][scale - 1][x, y]) / 2.0

        val dxx = doGPyramid[octave][scale][x + 1, y] - 2.0 * doGPyramid[octave][scale][x, y] + doGPyramid[octave][scale][x - 1, y]
        val dxy = ((doGPyramid[octave][scale][x + 1, y + 1] - doGPyramid[octave][scale][x - 1, y + 1]) - (
                doGPyramid[octave][scale][x + 1, y - 1] - doGPyramid[octave][scale][x - 1, y - 1]
                )) / 4.0

        val dxs = ((doGPyramid[octave][scale + 1][x + 1, y] - doGPyramid[octave][scale - 1][x - 1, y]) -
                (doGPyramid[octave][scale - 1][x + 1, y] - doGPyramid[octave][scale - 1][x - 1, y])) / 4.0
        val dyy = doGPyramid[octave][scale][x, y + 1] - 2.0 * doGPyramid[octave][scale][x, y] + doGPyramid[octave][scale][x, y - 1]
        val dys = ((doGPyramid[octave][scale - 1][x, y + 1] - doGPyramid[octave][scale + 1][x, y - 1]) -
                (doGPyramid[octave][scale - 1][x, y + 1] - doGPyramid[octave][scale - 1][x, y - 1])) / 4.0
        val dss = doGPyramid[octave][scale + 1][x, y] - 2.0 * doGPyramid[octave][scale][x, y] + doGPyramid[octave][scale - 1][x, y]
        val jacobi = DoubleMatrix(arrayOf(doubleArrayOf(dx, dy, ds)))
        val hesse = DoubleMatrix(arrayOf(doubleArrayOf(dxx, dy, dxs),
                doubleArrayOf(dxy, dyy, dys),
                doubleArrayOf(dxs, dys, dss)))

        if (hesse.determinant == 0.0) {
            extremumPoints[octave][scale][x][y] = false
            return
        }
        val offset = (hesse.inverse * jacobi)

        if (abs(offset[0, 0]) > 0.5 || abs(offset[1, 0]) > 0.5) {
            extremumPoints[octave][scale][x][y] = false
            return
        }

        val subpixelContrast = doGPyramid[octave][scale][x, y] + 0.5 * (jacobi.transposed * offset)[0, 0]
        if (abs(subpixelContrast) < CONTRAST_DISCARD_THRESHOLD) {
            extremumPoints[octave][scale][x][y] = false
            return
        }

        val hesseWithoutScale = hesse.getSubMatrix(0, 2, 0, 2)
        val ratio = hesseWithoutScale.trace.pow(2.0) / hesseWithoutScale.determinant
        if (ratio > 12.1) {
            extremumPoints[octave][scale][x][y] = false
            return
        }
        val keypoint = Keypoint(x + offset[0, 0], y + offset[1, 0], scale + offset[2, 0], octave)

        keypoints[octave].add(keypoint)
        println("Found keypoint for octave $octave with $keypoint")
    }

}
