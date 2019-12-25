package imageProcessing

import util.RGBImageArrayProxy

interface ImageProcessor {

    fun process(input: RGBImageArrayProxy): RGBImageArrayProxy

}
