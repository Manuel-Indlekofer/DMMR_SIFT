import imageProcessing.implementation.Normalizer
import math.BilinearInterpolation
import model.DifferenceOfGaussiansPyramidBuilder
import model.LocalMaximumExtractor
import model.KeypointSubpixelExtractor
import steps.*
import util.RGBImageArrayProxy
import visualization.Visualization
import java.io.File

fun main() {

    val image = Normalizer.normalizeImage(RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test.jpg")))
    val scaleSpace = GaussianScaleSpace()
    val gaussianPyramid = scaleSpace.process(image)

    val dOG = DifferenceOfGaussians().process(gaussianPyramid)
    dOG.forEach {
        Visualization().showImage(RGBImageArrayProxy(it).bufferedImage)
    }

    val discreteExtrema = DiscreteExtremaExtraction().process(dOG)
    val candidateKeypoints = SubPixelPrecisionExtractor(dOG, scaleSpace).process(discreteExtrema)

    val keypoints = FilterCandidates(dOG).process(candidateKeypoints)

    val orientationAssignment = OrientationAssignment(dOG)

    val orientedKeypoints = orientationAssignment.process(keypoints)

    val keypointDescriptors = KeypointDescriptorConstruction(orientationAssignment.xGradient,orientationAssignment.yGradient).process(orientedKeypoints)

    println("Got a total of ${keypointDescriptors.descriptors.size} descriptors")


    val displayImage = RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test.jpg"))

    for(descriptor in keypointDescriptors.descriptors){
        val x = descriptor.interpolatedX.toInt()
        val y = descriptor.interpolatedY.toInt()

        displayImage[x,y] = intArrayOf(0,255,0)
    }

    Visualization().showImage(displayImage.bufferedImage)




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
