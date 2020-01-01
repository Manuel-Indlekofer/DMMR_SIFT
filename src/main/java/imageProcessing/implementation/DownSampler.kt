package imageProcessing.implementation

import imageProcessing.ImageProcessor
import math.DoubleMatrix
import util.RGBImageArrayProxy
import java.awt.RenderingHints

class DownSampler(private val scale: Double) : ImageProcessor {
    override fun process(input: DoubleMatrix): DoubleMatrix {
        val outputImage = RGBImageArrayProxy((input.columns * scale).toInt(), (input.rows * scale).toInt())
        outputImage.bufferedImage.createGraphics().apply {
            setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            drawImage(RGBImageArrayProxy(input).bufferedImage, 0, 0, (input.columns * scale).toInt(), (input.rows * scale).toInt(), null)
            dispose()
        }
        return outputImage.matrix
    }
}