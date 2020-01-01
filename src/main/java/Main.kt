import imageProcessing.implementation.DownSampler
import imageProcessing.implementation.Normalizer
import model.DifferenceOfGaussians
import model.LocalMaximumExtractor
import model.MaximumSubpixelEnhancer
import util.RGBImageArrayProxy
import visualization.Visualization
import java.io.File

fun main() {

    val image = Normalizer.normalizeImage(RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test.jpg")))

    val gaussianPyramid = DifferenceOfGaussians().calculateGaussianPyramid(image)
    val result = LocalMaximumExtractor().extractMaximumValues(gaussianPyramid)
    MaximumSubpixelEnhancer(gaussianPyramid, result).process()

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
