package model

import math.DoubleMatrix
import org.apache.commons.math3.linear.LUDecomposition
import util.RGBImageArrayProxy
import java.lang.Math.pow
import kotlin.math.pow

class MaximumSubpixelEnhancer(private val doGPyramid: Array<Array<RGBImageArrayProxy>>, private val extremumPoints: Array<Array<Array<Array<Boolean>>>>) {

    companion object {
        const val CONTRAST_DISCARD_THRESHOLD = 0.00
    }

    fun process() {
        for (octave in extremumPoints.indices) {
            for (scale in extremumPoints[octave].indices) {
                for (x in extremumPoints[octave][scale].indices) {
                    for (y in extremumPoints[octave][scale][x].indices) {
                        if (extremumPoints[octave][scale][x][y]) {
                            calculateOffset(x, y, octave, scale)
                        }
                    }
                }
            }
        }
    }


    private fun calculateOffset(x: Int, y: Int, octave: Int, scale: Int) {
        val dx = (doGPyramid[octave][scale][x + 1, y][0] - doGPyramid[octave][scale][x - 1, y][0]) / 2.0
        val dy = (doGPyramid[octave][scale][x, y + 1][0] - doGPyramid[octave][scale][x, y - 1][0]) / 2.0
        val ds = (doGPyramid[octave][scale + 1][x, y][0] - doGPyramid[octave][scale - 1][x, y][0]) / 2.0

        val dxx = doGPyramid[octave][scale][x + 1, y][0] - 2.0 * doGPyramid[octave][scale][x, y][0] + doGPyramid[octave][scale][x - 1, y][0]
        val dxy = ((doGPyramid[octave][scale][x + 1, y + 1][0] - doGPyramid[octave][scale][x - 1, y + 1][0]) - (
                doGPyramid[octave][scale][x + 1, y - 1][0] - doGPyramid[octave][scale][x - 1, y - 1][0]
                )) / 4.0

        val dxs = ((doGPyramid[octave][scale + 1][x + 1, y][0] - doGPyramid[octave][scale - 1][x - 1, y][0]) -
                (doGPyramid[octave][scale - 1][x + 1, y][0] - doGPyramid[octave][scale - 1][x - 1, y][0])) / 4.0
        val dyy = doGPyramid[octave][scale][x, y + 1][0] - 2.0 * doGPyramid[octave][scale][x, y][0] + doGPyramid[octave][scale][x, y - 1][0]
        val dys = ((doGPyramid[octave][scale - 1][x, y + 1][0] - doGPyramid[octave][scale + 1][x, y - 1][0]) -
                (doGPyramid[octave][scale - 1][x, y + 1][0] - doGPyramid[octave][scale - 1][x, y - 1][0])) / 4.0
        val dss = doGPyramid[octave][scale + 1][x, y][0] - 2.0 * doGPyramid[octave][scale][x, y][0] + doGPyramid[octave][scale - 1][x, y][0]
        val jacobi = DoubleMatrix(arrayOf(doubleArrayOf(dx, dy, ds)))
        val hesse = DoubleMatrix(arrayOf(doubleArrayOf(dxx, dy, dxs),
                doubleArrayOf(dxy, dyy, dys),
                doubleArrayOf(dxs, dys, dss)))

        if (hesse.determinant == 0.0) {
            return
        }
        val offset = (hesse.inverse * jacobi)
        val subpixelContrast = doGPyramid[octave][scale][x, y][0] + 0.5 * (jacobi.transposed * offset)[0, 0]
        if (subpixelContrast < CONTRAST_DISCARD_THRESHOLD) {
            extremumPoints[octave][scale][x][y] = false
            return
        }

        val hesseWithoutScale = hesse.getSubMatrix(0, 2, 0, 2)
        val ratio = hesseWithoutScale.trace.pow(2.0) / hesseWithoutScale.determinant
        if (ratio > 12.1) {
            extremumPoints[octave][scale][x][y] = false
            return
        }
    }

}
