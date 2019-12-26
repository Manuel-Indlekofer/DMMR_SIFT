package imageProcessing.implementation

import imageProcessing.ImageProcessor
import util.RGBImageArrayProxy
import kotlin.math.abs

class Subtract(private val other: RGBImageArrayProxy) : ImageProcessor {
    override fun process(input: RGBImageArrayProxy): RGBImageArrayProxy {
        return input.forEachPixel { x, y ->
            val inputArray = input[x, y].mapIndexed { index, i ->
                abs(i - other[x, y][index])
            }.toIntArray()

            val outputArray = inputArray.map { pixelValue ->
                if (pixelValue < 0) {
                    return@map 0
                } else if (pixelValue > 255) {
                    return@map 255
                }
                return@map pixelValue
            }.toIntArray()
            println("\u001B[36m SUBTRACTION \u001B[0m ${input[x,y].contentToString()} and ${other[x, y].contentToString()}  processed to ${outputArray.contentToString()}")
            return@forEachPixel outputArray
        }
    }
}