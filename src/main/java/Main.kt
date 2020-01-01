import imageProcessing.implementation.Normalizer
import model.DifferenceOfGaussiansPyramidBuilder
import model.LocalMaximumExtractor
import model.KeypointSubpixelExtractor
import util.RGBImageArrayProxy
import visualization.Visualization
import java.io.File

fun main() {

    val image = Normalizer.normalizeImage(RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test.jpg")))

    val gaussianPyramidBuilder = DifferenceOfGaussiansPyramidBuilder()
    val gaussianPyramid = gaussianPyramidBuilder.calculateGaussianPyramid(image)
    val result = LocalMaximumExtractor().extractMaximumValues(gaussianPyramid)
    KeypointSubpixelExtractor(gaussianPyramid, result, gaussianPyramidBuilder.sigmaLevels).process()

    for (octave in result.indices) {
        for (scale in result[octave].indices) {
            for (x in result[octave][scale].indices) {
                for (y in result[octave][scale][x].indices) {
                    if (result[octave][scale][x][y]) {
                        gaussianPyramid[octave][scale][x, y] = 1.0
                    }
                }
            }
        }
    }

    for (octave in gaussianPyramid.indices) {
        for (scale in gaussianPyramid.indices) {
            Visualization().showImage(RGBImageArrayProxy(gaussianPyramid[octave][scale]).bufferedImage)
        }
    }
}
