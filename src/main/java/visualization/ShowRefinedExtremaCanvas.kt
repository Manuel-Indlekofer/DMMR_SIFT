package visualization

import steps.SubPixelPrecisionExtractor
import util.copy
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JComponent

open class ShowRefinedExtremaCanvas(extrema: SubPixelPrecisionExtractor.CandidateKeypoints, inputImage: BufferedImage) : JComponent() {

    private val image = inputImage.copy()

    companion object {
        private const val CROSS_LENGTH = 10
    }

    init {
        for (extremum in extrema.candidates) {
            drawCross(extremum)
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.drawImage(image, 0, 0, this)
    }

    private fun drawCross(candidateKeypoint: SubPixelPrecisionExtractor.CandidateKeypoints.CandidateKeypoint) {
        val x = candidateKeypoint.interpolatedX.toInt()
        val y = candidateKeypoint.interpolatedY.toInt()
        for (crossX in x - CROSS_LENGTH / 2..x + CROSS_LENGTH / 2) {
            if(crossX in 0 until image.width){
                image.setRGB(crossX, y, 16711680)
            }
        }
        for (crossY in y - CROSS_LENGTH / 2..y + CROSS_LENGTH / 2) {
            if ( crossY in 0 until image.height) {
                image.setRGB(x, crossY, 16711680)
            }
        }
    }
}
