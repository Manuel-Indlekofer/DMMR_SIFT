package visualization

import steps.KeypointDescriptorConstruction
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JComponent
import kotlin.math.max

class MatchingCanvas(private val imageA: BufferedImage, private val imageB: BufferedImage, private val matching: List<Pair<KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor, KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor>>) : JComponent() {

    private val multiplierY: Double
        get() {
            return super.getHeight().toDouble() / max(imageA.height, imageB.height)
        }

    private val multiplierX: Double
        get() {
            return super.getWidth().toDouble() / (imageA.width + imageB.width)
        }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.drawImage(imageA, 0, 0, (imageA.width * multiplierX).toInt(), (imageA.height * multiplierY).toInt(), this)
        g.drawImage(imageB, (imageA.width * multiplierX).toInt(), 0, (imageB.width * multiplierX).toInt(), (imageB.height * multiplierY).toInt(), this)
        for (matchingPair in matching) {
            val matchA = matchingPair.first
            val matchB = matchingPair.second
            g.color = Color.GREEN
            g.drawLine((matchA.interpolatedX*multiplierX).toInt(), (matchA.interpolatedY*multiplierY).toInt(), (matchB.interpolatedX*multiplierX).toInt() + (imageA.width*multiplierX).toInt(), (matchB.interpolatedY*multiplierY).toInt())
        }
    }

}