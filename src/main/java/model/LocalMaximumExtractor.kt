package model

import util.RGBImageArrayProxy

class LocalMaximumExtractor {

    lateinit var extremumPoints: Array<Array<Array<Array<Boolean>>>>

    fun extractMaximumValues(differences: Array<Array<RGBImageArrayProxy?>>): Array<Array<Array<Array<Boolean>>>> {
        extremumPoints = Array(differences.size) { octave ->
            Array(differences[octave].size) { scale ->
                Array(differences[octave][scale]!!.width) {
                    Array(differences[octave][scale]!!.height) {
                        false
                    }
                }
            }
        }
        for (octave in differences.indices) {
            for (scale in 1 until differences[octave].size - 1) {
                for (scanX in 1 until differences[octave][scale]!!.width - 1) {
                    for (scanY in 1 until differences[octave][scale]!!.height - 1) {

                        var leastVal = 255
                        var greatestVal = 0
                        val scanPixelColor = differences[octave][scale]!![scanX, scanY][0]
                        for (probeX in scanX - 1..scanX + 1) {
                            for (probeY in scanY - 1..scanY + 1) {

                                if (probeX == scanX && probeY == scanY) {
                                    continue
                                }
                                for (probeDepth in scale - 1..scale + 1) {
                                    val neighbourPixelColor = differences[octave][probeDepth]!![probeX, probeY][0]
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
