package steps

import kotlin.math.*

class KeypointDescriptorConstruction(private val xGradient: OrientationAssignment.Gradient, private val yGradient: OrientationAssignment.Gradient) : Step<OrientationAssignment.OrientedKeypoints, KeypointDescriptorConstruction.KeypointDescriptors> {

    companion object {
        private const val numberOfHistograms = 4
        private const val numberOfOrientations = 8
        private const val lambdaDescription = 6.0
    }

    data class KeypointDescriptors(val descriptors: ArrayList<KeypointDescriptor>) {
        data class KeypointDescriptor(val octave: Int, val scale: Int, val x: Int, val y: Int, val interpolatedScale: Double, val interpolatedX: Double, val interpolatedY: Double, val interpolatedValue: Double, val orientationTheta: Double, val featureVector: Array<Int>)
    }

    override fun process(input: OrientationAssignment.OrientedKeypoints): KeypointDescriptors {

        val result = KeypointDescriptors(ArrayList())

        for (keypoint in input.orientedKeypoints) {
            processKeypoint(keypoint)?.let {
                result.descriptors.add(it)
            }
        }
        return result
    }

    private fun processKeypoint(keypoint: OrientationAssignment.OrientedKeypoints.OrientedKeypoint): KeypointDescriptors.KeypointDescriptor? {

        val interPixelDistance = 0.5 * 2.0.pow(keypoint.octave)

        val imageXRange = 0 until xGradient.getXDimension(keypoint.octave, keypoint.scale)

        val imageYRange = 0 until xGradient.getYDimension(keypoint.octave, keypoint.scale)

        val targetXRange = ((keypoint.interpolatedX - sqrt(2.0) * lambdaDescription * keypoint.interpolatedScale * (numberOfHistograms + 1.0) / numberOfHistograms) / interPixelDistance).roundToInt()..((keypoint.interpolatedX + sqrt(2.0) * lambdaDescription * keypoint.interpolatedScale * (numberOfHistograms + 1) / numberOfHistograms) / interPixelDistance).roundToInt()
        val targetYRange = ((keypoint.interpolatedY - sqrt(2.0) * lambdaDescription * keypoint.interpolatedScale * (numberOfHistograms + 1.0) / numberOfHistograms) / interPixelDistance).roundToInt()..((keypoint.interpolatedY + sqrt(2.0) * lambdaDescription * keypoint.interpolatedScale * (numberOfHistograms + 1) / numberOfHistograms) / interPixelDistance).roundToInt()

        if (targetXRange.first !in imageXRange || targetXRange.last !in imageXRange || targetYRange.first !in imageYRange || targetYRange.last !in imageYRange) {
            return null
        }

        val weightedHistograms: Array<Array<Array<Double>>> = Array(numberOfHistograms) {
            Array(numberOfHistograms) {
                Array(numberOfOrientations) {
                    0.0
                }
            }
        }

        for (x in targetXRange) {
            for (y in targetYRange) {

                val normalizedX = ((x * interPixelDistance - keypoint.interpolatedX) * cos(keypoint.orientationTheta) + (y * interPixelDistance - keypoint.interpolatedY) * sin(keypoint.orientationTheta)) / keypoint.interpolatedScale
                val normalizedY = (-(x * interPixelDistance - keypoint.interpolatedX) * sin(keypoint.orientationTheta) + (y * interPixelDistance - keypoint.interpolatedY) * cos(keypoint.orientationTheta)) / keypoint.interpolatedScale


                if (max(abs(normalizedX), abs(normalizedY)) < lambdaDescription * (numberOfHistograms + 1.0) / numberOfHistograms) {
                    val normalizedOrientation = atan2(xGradient.getGradient(keypoint.octave, keypoint.scale, x, y), yGradient.getGradient(keypoint.octave, keypoint.scale, x, y)) - abs(keypoint.orientationTheta.rem(2 * PI))
                    val sampleContribution = exp(-(sqrt((x * interPixelDistance - keypoint.interpolatedX).pow(2.0) + (y * interPixelDistance - keypoint.interpolatedY).pow(2.0)).pow(2.0) / (2 * (lambdaDescription * keypoint.interpolatedScale).pow(2.0)))) * sqrt(xGradient.getGradient(keypoint.octave, keypoint.scale, x, y).pow(2.0) + yGradient.getGradient(keypoint.octave, keypoint.scale, x, y).pow(2.0))


                    for (i in 1..numberOfHistograms) {
                        if (abs((i - (1 + numberOfHistograms) / 2.0) * (2 * lambdaDescription) / numberOfHistograms - normalizedX) > (2 * lambdaDescription) / numberOfHistograms) {
                            continue
                        }
                        for (j in 1..numberOfHistograms) {
                            if (abs((j - (1 + numberOfHistograms) / 2.0) * (2 * lambdaDescription) / numberOfHistograms - normalizedY) > (2 * lambdaDescription) / numberOfHistograms) {
                                continue
                            }

                            for (k in 1..numberOfOrientations) {
                                if (abs((2*PI*(k-1)/ numberOfOrientations) - abs(normalizedOrientation.rem(2 * PI))) < (2 * PI) / numberOfOrientations) {
                                    weightedHistograms[i - 1][j - 1][k - 1] += (1 - numberOfHistograms / (2 * lambdaDescription) * abs(normalizedX - (i - (1 + numberOfHistograms) / 2.0) * (2 * lambdaDescription) / numberOfHistograms)) * (1 - numberOfHistograms / (2 * lambdaDescription) * abs(normalizedY - (j - (1 + numberOfHistograms) / 2.0) * (2 * lambdaDescription) / numberOfHistograms)) * (1 - numberOfOrientations / (2 * PI) * abs(normalizedOrientation - (2 * PI * (k - 1) / numberOfOrientations.toDouble()).rem(2 * PI))) * sampleContribution
                                }
                            }
                        }
                    }
                }
            }
        }

        val featureVector: Array<Double> = Array(numberOfHistograms * numberOfOrientations * numberOfOrientations) {
            0.0
        }
        for (i in 0 until numberOfHistograms) {
            for (j in 0 until numberOfHistograms) {
                for (k in 0 until numberOfOrientations) {
                    featureVector[i * numberOfOrientations * numberOfOrientations + j * numberOfOrientations + k] = weightedHistograms[i][j][k]
                }
            }
        }

        for (i in 0 until numberOfOrientations * numberOfHistograms * numberOfOrientations) {
            featureVector[i] = min(featureVector[i], 0.2 * calculateL2NormForFeatureVector(featureVector))
            featureVector[i] = min(floor(512 * featureVector[i] / calculateL2NormForFeatureVector(featureVector)), 255.0)
        }

        return KeypointDescriptors.KeypointDescriptor(keypoint.octave, keypoint.scale, keypoint.x, keypoint.y, keypoint.interpolatedScale,
                keypoint.interpolatedX, keypoint.interpolatedY, keypoint.interpolatedValue, keypoint.orientationTheta, featureVector.map {
            it.toInt()
        }.toTypedArray())
    }

    private fun calculateL2NormForFeatureVector(featureVector: Array<Double>): Double {

        var sum = 0.0
        for (number in featureVector) {
            sum += number.pow(2.0)
        }

        return sqrt(sum)
    }

}
