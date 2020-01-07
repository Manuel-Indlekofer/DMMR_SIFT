package visualization

import steps.KeypointDescriptorConstruction
import java.awt.Canvas
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel

class Visualization {


    fun showImage(bufferedImage: BufferedImage) {
        val frame: JFrame = JFrame()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(800, 400)
        val label: JLabel = JLabel()
        frame.add(label)
        frame.isVisible = true
        label.icon = ImageIcon(bufferedImage)
    }

    fun showMatches(bufferedImageA: BufferedImage, bufferedImageB: BufferedImage, matches: List<Pair<KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor, KeypointDescriptorConstruction.KeypointDescriptors.KeypointDescriptor>>) {
        val frame: JFrame = JFrame()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(800, 400)
        val matchingDisplay = MatchingCanvas(bufferedImageA, bufferedImageB, matches)
        frame.add(matchingDisplay)
        frame.isVisible = true
    }

    fun showStepCanvas(canvas: JComponent, title: String) {
        val frame: JFrame = JFrame(title)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(800, 400)
        frame.add(canvas)
        frame.isVisible = true
    }

}
