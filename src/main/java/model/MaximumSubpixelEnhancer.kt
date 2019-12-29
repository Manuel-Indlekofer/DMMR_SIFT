package model

import math.DoubleMatrix
import util.RGBImageArrayProxy

class MaximumSubpixelEnhancer(private val doGPyramid: Array<Array<RGBImageArrayProxy>>, private val extremumPoints: Array<Array<Array<Array<Boolean>>>>) {


    fun process() {
        for (octave in extremumPoints.indices) {
            for (scale in extremumPoints[octave].indices) {
                for (x in extremumPoints[octave][scale].indices) {
                    for (y in extremumPoints[octave][scale][x].indices) {
                        if(extremumPoints[octave][scale][x][y]){
                            calculateOffset(x,y,octave,scale)
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

        if(hesse.determinant == 0.0){
            return
        }
        val offset = (hesse.inverse * jacobi)
        println(offset.toString())
    }

}
