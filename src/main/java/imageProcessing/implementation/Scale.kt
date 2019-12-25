package imageProcessing.implementation

import imageProcessing.ImageProcessor
import util.RGBImageArrayProxy
import java.awt.RenderingHints

class Scale(private val scale: Double) : ImageProcessor {
    override fun process(input: RGBImageArrayProxy): RGBImageArrayProxy {
        val outputImage = RGBImageArrayProxy((input.width * scale).toInt(), (input.height * scale).toInt())
        outputImage.bufferedImage.createGraphics().apply {
            setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            drawImage(input.bufferedImage, 0, 0, (input.width * scale).toInt(), (input.height * scale).toInt(), null)
            dispose()
        }
        return outputImage
    }
}