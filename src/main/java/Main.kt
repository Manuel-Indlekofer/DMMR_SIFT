import imageProcessing.implementation.Normalizer
import matching.SiftMatcher
import math.BilinearInterpolation
import model.DifferenceOfGaussiansPyramidBuilder
import model.LocalMaximumExtractor
import model.KeypointSubpixelExtractor
import sift.SiftGenerator
import steps.*
import util.RGBImageArrayProxy
import visualization.Visualization
import java.io.File

fun main() {

    val image1descriptors = SiftGenerator().generateKeypoints("C:\\Users\\manue\\Desktop\\test.jpg")
    val image2descriptors = SiftGenerator().generateKeypoints("C:\\Users\\manue\\Desktop\\test2.jpg")


    val matcher = SiftMatcher()

    val result = matcher.match(image1descriptors, image2descriptors)

    val displayImageA = RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test.jpg"))
    val displayImageB = RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test2.jpg"))


    for (matchA in result.map { it.first }) {
        displayImageA[matchA.interpolatedX.toInt(),matchA.interpolatedY.toInt()] = intArrayOf(255,0,0)
    }

    for (matchB in result.map { it.second }) {
        displayImageB[matchB.interpolatedX.toInt(), matchB.interpolatedY.toInt()] = intArrayOf(255,0,0)
    }

    Visualization().showImage(displayImageA.bufferedImage)
    Visualization().showImage(displayImageB.bufferedImage)


    /* val gaussianPyramidBuilder = DifferenceOfGaussiansPyramidBuilder()
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
     }*/
}
