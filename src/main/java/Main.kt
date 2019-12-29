import imageProcessing.implementation.BlackAndWhite
import imageProcessing.implementation.GaussianBlur
import imageProcessing.implementation.Subtract
import model.DifferenceOfGaussians
import model.LocalMaximumExtractor
import org.apache.commons.math3.linear.MatrixUtils
import util.RGBImageArrayProxy
import visualization.Visualization
import java.io.File

fun main() {

    val image = RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test2.jpg"))
    val bnw = BlackAndWhite().process(image)

    val gaussianPyramid = DifferenceOfGaussians().calculateGaussianPyramid(bnw)
    val result = LocalMaximumExtractor().extractMaximumValues(gaussianPyramid)

    for (octave in result.indices) {
        for (scale in result[octave].indices) {
            for (x in result[octave][scale].indices) {
                for (y in result[octave][scale][x].indices) {
                    if (result[octave][scale][x][y]) {
                        gaussianPyramid[octave][scale]!![x, y] = intArrayOf(255, 0, 0);
                    }
                }
            }
        }
    }

    for(octave in gaussianPyramid.indices){
        for(scale in gaussianPyramid.indices){
            Visualization().showImage(gaussianPyramid[octave][scale]!!.bufferedImage)
        }
    }
}
