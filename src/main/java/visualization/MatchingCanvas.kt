package visualization

import steps.KeypointDescriptorConstruction
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JComponent

class MatchingCanvas(private val imageA: BufferedImage, private val imageB: BufferedImage, private val matching: List<Pair<KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor, KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor>>) : JComponent() {

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.drawImage(imageA, 0, 0, this)
        g.drawImage(imageB, imageA.width, 0, this)
        for (matchingPair in matching) {
            val matchA = matchingPair.first
            val matchB = matchingPair.second
            g.color = Color.GREEN
            g.drawLine(matchA.interpolatedX.toInt(), matchA.interpolatedY.toInt(), matchB.interpolatedX.toInt() + imageA.width, matchB.interpolatedY.toInt())
        }
    }

}