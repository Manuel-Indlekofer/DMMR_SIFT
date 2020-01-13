package steps

import model.GaussianPyramid
import kotlin.math.abs

class DiscreteExtremaExtraction : Step<GaussianPyramid.DifferenceOfGaussiansPyramid, DiscreteExtremaExtraction.DiscreteExtrema> {

    data class DiscreteExtrema(val extrema: ArrayList<Extremum>) {
        data class Extremum(val octave: Int, val scale: Int, val x: Int, val y: Int)
    }

    override fun process(input: GaussianPyramid.DifferenceOfGaussiansPyramid): DiscreteExtrema {
        val extrema = DiscreteExtrema(ArrayList())


        for (octave in 0 until input.numberOfOctaves) {
            for (scale in 1 until input.numberOfScales - 1) {
                for (scanX in 1 until input.getImage(octave, scale).columns - 1) {
                    for (scanY in 1 until input.getImage(octave, scale).rows - 1) {

                        var leastVal = 1.0
                        var greatestVal = 0.0
                        val scanPixelColor = input.getImage(octave, scale)[scanX, scanY]
                        for (probeX in scanX - 1..scanX + 1) {
                            for (probeY in scanY - 1..scanY + 1) {

                                if (probeX == scanX && probeY == scanY) {
                                    continue
                                }
                                for (probeDepth in scale - 1..scale + 1) {
                                    val neighbourPixelColor = input.getImage(octave, probeDepth)[probeX, probeY]
                                    if (neighbourPixelColor < leastVal) {
                                        leastVal = neighbourPixelColor
                                    }
                                    if (neighbourPixelColor > greatestVal) {
                                        greatestVal = neighbourPixelColor
                                    }
                                }
                            }
                        }
                        if (scanPixelColor < leastVal || scanPixelColor > greatestVal && abs(scanPixelColor) >= 0.8 * 0.015 ) {
                            extrema.extrema.add(DiscreteExtrema.Extremum(octave, scale, scanX, scanY))
                        }
                    }
                }
            }
        }

        println("\u001B[35m [DiscreteExtremaExtraction] \u001B[0m Found a total of ${extrema.extrema.size} possible keypoints!")
        return extrema
    }
}