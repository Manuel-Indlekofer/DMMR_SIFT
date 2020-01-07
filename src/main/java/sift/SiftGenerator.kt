package sift

import imageProcessing.implementation.Normalizer
import steps.*
import util.RGBImageArrayProxy
import visualization.GaussianPyramidCanvas
import visualization.ShowRefinedExtremaCanvas
import visualization.Visualization
import java.io.File

class SiftGenerator(private val visualizeSteps: Boolean = true) {

    fun generateKeypoints(fileName: String): KeypointDescriptorConstruction.KeypointDescriptors {
        val rawImage = RGBImageArrayProxy(File(fileName))
        val image = Normalizer.normalizeImage(rawImage)

        val scaleSpace = GaussianScaleSpace()
        val gaussianPyramid = scaleSpace.process(image)
        if(visualizeSteps){
            Visualization().showStepCanvas(GaussianPyramidCanvas(gaussianPyramid),"Gaussian Scale-Space")
        }
        val dOG = DifferenceOfGaussians().process(gaussianPyramid)
        if(visualizeSteps){
            Visualization().showStepCanvas(GaussianPyramidCanvas(dOG),"Difference of Gaussians Scale-Space")
        }
        val discreteExtrema = DiscreteExtremaExtraction().process(dOG)
        val candidateKeypoints = SubPixelPrecisionExtractor(dOG, scaleSpace).process(discreteExtrema)

        if(visualizeSteps){
            Visualization().showStepCanvas(ShowRefinedExtremaCanvas(candidateKeypoints,rawImage.bufferedImage),"Extrema Points")
        }

        val keypoints = FilterCandidates(dOG).process(candidateKeypoints)

        val orientationAssignment = OrientationAssignment(gaussianPyramid)

        val orientedKeypoints = orientationAssignment.process(keypoints)

        return KeypointDescriptorConstruction(orientationAssignment.xGradient, orientationAssignment.yGradient).process(orientedKeypoints)
    }

}