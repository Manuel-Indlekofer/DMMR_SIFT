package matching

import steps.KeypointDescriptorConstruction
import kotlin.math.pow
import kotlin.math.sqrt

class SiftMatcher {

    private data class Result(val featureVector: KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor, val distance: Double)


    fun match(descriptorsA: KeypointDescriptorConstruction.KeypointDescriptors, descriptorsB: KeypointDescriptorConstruction.KeypointDescriptors): List<Pair<KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor, KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor>> {

        val result : ArrayList<Pair<KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor,KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor>> = ArrayList()

        for (descriptorA in descriptorsA.descriptors) {
            val compareDescriptors = ArrayList(descriptorsB.descriptors)
            val firstNeighbour = calculateNearestNeighbour(descriptorA, compareDescriptors)
            compareDescriptors.remove(firstNeighbour.featureVector)
            val secondNeighbour = calculateNearestNeighbour(descriptorA, compareDescriptors)

            if (firstNeighbour.distance < 0.6 * secondNeighbour.distance) {
                result.add(Pair(descriptorA,firstNeighbour.featureVector))
            }
        }
        return result
    }

    private fun calculateEuclideanNorm(featureVector: Array<Int>): Double {

        var sum = 0.0
        for (number in featureVector) {
            sum += number.toDouble().pow(2.0)
        }

        return sqrt(sum)
    }

    private fun calculateNearestNeighbour(sourceVector: KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor, compareVectors: ArrayList<KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor>): Result {

        val featureVectorsB = compareVectors.map { it.featureVector }.toList()
        val difference = featureVectorsB.map { vector ->
            vector.mapIndexed { index, i ->
                i - sourceVector.featureVector[index]
            }.toTypedArray()
        }.map { vector -> calculateEuclideanNorm(vector) }

        return Result(compareVectors[difference.withIndex().minBy { it.value }!!.index], difference.withIndex().minBy { it.value }!!.value)
    }

}
