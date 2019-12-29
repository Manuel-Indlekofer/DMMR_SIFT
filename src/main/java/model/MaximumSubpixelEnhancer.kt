package model

import math.DoubleMatrix
import util.RGBImageArrayProxy

class MaximumSubpixelEnhancer(private val doGPyramid: Array<Array<RGBImageArrayProxy>>, private val extremumPoints: Array<Array<Array<Array<Boolean>>>>) {

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

        val offset = (hesse.inverse * jacobi)
    }

}
