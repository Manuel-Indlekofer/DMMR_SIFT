package imageProcessing

import math.DoubleMatrix

interface ImageProcessor {

    fun process(input: DoubleMatrix): DoubleMatrix

}
