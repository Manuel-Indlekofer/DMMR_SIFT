package sift

import imageProcessing.implementation.Normalizer
import steps.*
import util.RGBImageArrayProxy
import java.io.File

class SiftGenerator {

    fun generateKeypoints(fileName: String): KeypointDescriptorConstruction.KeypointDescriptors {
        val image = Normalizer.normalizeImage(RGBImageArrayProxy(File(fileName)))
        val scaleSpace = GaussianScaleSpace()
        val gaussianPyramid = scaleSpace.process(image)

        val dOG = DifferenceOfGaussians().process(gaussianPyramid)
        val discreteExtrema = DiscreteExtremaExtraction().process(dOG)
        val candidateKeypoints = SubPixelPrecisionExtractor(dOG, scaleSpace).process(discreteExtrema)

        val keypoints = FilterCandidates(dOG).process(candidateKeypoints)

        val orientationAssignment = OrientationAssignment(dOG)

        val orientedKeypoints = orientationAssignment.process(keypoints)

        return KeypointDescriptorConstruction(orientationAssignment.xGradient, orientationAssignment.yGradient).process(orientedKeypoints)
    }

}