package model

import util.RGBImageArrayProxy

class LocalMaximumExtractor {

    lateinit var extremumPoints: Array<Array<Array<Array<Boolean>>>>

    fun extractMaximumValues(differences: Array<Array<RGBImageArrayProxy>>) {
        extremumPoints = Array(differences.size) { octave ->
            Array(differences[octave].size) { scale ->
                Array(differences[octave][scale].width) {
                    Array(differences[octave][scale].height) {
                        false
                    }
                }
            }
        }
        for (octave in differences.indices) {
            for (scale in 1 until differences[octave].size - 1) {
                for (scanX in 0 until differences[octave][scale].width) {
                    for (scanY in 0 until differences[octave][scale].height) {

                        var leastVal = 255
                        var greatestVal = 0
                        var isExtremum = true
                        for (probeX in scanX - 1..scanX + 1) {
                            if (!isExtremum) {
                                break
                            }
                            for (probeY in scanY - 1..scanY + 1) {
                                if (!isExtremum) {
                                    break
                                }
                                if (probeX == scanX && probeX == scanY) {
                                    continue
                                }
                                for (probeDepth in scale - 1..scale + 1) {
                                    val neighbourPixelColor = differences[octave][probeDepth][probeX, probeY][0]
                                    val scanPixelColor = differences[octave][scale][scanX, scanY][0]
                                    if (neighbourPixelColor < leastVal) {
                                        leastVal = neighbourPixelColor
                                    }
                                    if (neighbourPixelColor > greatestVal) {
                                        leastVal = neighbourPixelColor
                                    }
                                    if (scanPixelColor in (leastVal + 1) until greatestVal) {
                                        //can no longer be an extremum discard
                                        isExtremum = false
                                        break
                                    }
                                }
                            }
                        }
                        if (isExtremum) {
                            //mark
                        }
                    }
                }
            }
        }
    }
}
