package steps

import imageProcessing.implementation.Subtract
import model.GaussianPyramid

class DifferenceOfGaussians : Step<GaussianPyramid, GaussianPyramid.DifferenceOfGaussiansPyramid> {
    override fun process(input: GaussianPyramid): GaussianPyramid.DifferenceOfGaussiansPyramid {
        val differenceOfGaussians = GaussianPyramid.DifferenceOfGaussiansPyramid(input.numberOfOctaves, input.numberOfScales - 1)
        for (octave in 0 until differenceOfGaussians.numberOfOctaves) {
            for (scale in 0 until differenceOfGaussians.numberOfScales) {
                differenceOfGaussians.setImage(octave, scale, Subtract(input.getImage(octave, scale)).process(input.getImage(octave, scale + 1)))
            }
        }
        return differenceOfGaussians
    }
}
