package imageProcessing.implementation

import imageProcessing.ImageProcessor
import util.RGBImageArrayProxy

class BlackAndWhite : ImageProcessor {
    override fun process(input: RGBImageArrayProxy): RGBImageArrayProxy {
        return input.forEachPixel { x, y ->
            val avg = input[x, y].reduce { acc, i -> acc + i } / input[x, y].size
            return@forEachPixel intArrayOf(avg, avg, avg)
        }
    }
}