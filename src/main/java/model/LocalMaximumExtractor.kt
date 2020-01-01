package model

import math.DoubleMatrix

class LocalMaximumExtractor {

    lateinit var extremumPoints: Array<Array<Array<Array<Boolean>>>>

    fun extractMaximumValues(differences: Array<Array<DoubleMatrix>>): Array<Array<Array<Array<Boolean>>>> {
        extremumPoints = Array(differences.size) { octave ->
            Array(differences[octave].size) { scale ->
                Array(differences[octave][scale].columns) {
                    Array(differences[octave][scale].rows) {
                        false
                    }
                }
            }
        }
        for (octave in differences.indices) {
            for (scale in 1 until differences[octave].size - 1) {
                for (scanX in 1 until differences[octave][scale].columns - 1) {
                    for (scanY in 1 until differences[octave][scale].rows - 1) {

                        var leastVal = 1.0
                        var greatestVal = 0.0
                        val scanPixelColor = differences[octave][scale][scanX, scanY]
                        for (probeX in scanX - 1..scanX + 1) {
                            for (probeY in scanY - 1..scanY + 1) {

                                if (probeX == scanX && probeY == scanY) {
                                    continue
                                }
                                for (probeDepth in scale - 1..scale + 1) {
                                    val neighbourPixelColor = differences[octave][probeDepth][probeX, probeY]
                                    if (neighbourPixelColor < leastVal) {
                                        leastVal = neighbourPixelColor
                                    }
                                    if (neighbourPixelColor > greatestVal) {
                                        greatestVal = neighbourPixelColor
                                    }
                                }
                            }
                        }
                        if (scanPixelColor < leastVal || scanPixelColor > greatestVal) {
                            extremumPoints[octave][scale][scanX][scanY] = true
                        }
                    }
                }
            }
        }
        println("max extraction done")
        return extremumPoints
    }
}
