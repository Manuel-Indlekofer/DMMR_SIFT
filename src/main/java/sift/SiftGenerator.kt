package sift

import imageProcessing.implementation.Normalizer
import steps.*
import util.RGBImageArrayProxy
import visualization.*
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
            Visualization().showStepCanvas(GaussianPyramidCanvas(dOG,20.0),"Difference of Gaussians Scale-Space")
        }
        val discreteExtrema = DiscreteExtremaExtraction().process(dOG)
        val candidateKeypoints = SubPixelPrecisionExtractor(dOG, scaleSpace).process(discreteExtrema)

        if(visualizeSteps){
            Visualization().showStepCanvas(ShowRefinedExtremaCanvas(candidateKeypoints,rawImage.bufferedImage),"Extrema Points")
        }

        val keypoints = FilterCandidates(dOG).process(candidateKeypoints)

        if(visualizeSteps){
            Visualization().showStepCanvas(ShowFilteredKeypointsCanvas(keypoints,rawImage.bufferedImage),"Filtered Extrema Points")
        }

        val orientationAssignment = OrientationAssignment(gaussianPyramid)

        val orientedKeypoints = orientationAssignment.process(keypoints)

        if(visualizeSteps){
            Visualization().showStepCanvas(ShowRotationCanvas(orientedKeypoints, rawImage.bufferedImage),"Orientation Assignment")
        }

        return KeypointDescriptorConstruction(orientationAssignment.xGradient, orientationAssignment.yGradient).process(orientedKeypoints)
    }

}