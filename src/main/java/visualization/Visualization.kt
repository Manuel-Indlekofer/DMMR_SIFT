package visualization

import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

class Visualization {

    private val frame: JFrame
    private val label: JLabel

    init {
        frame = JFrame()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(800, 400)
        label = JLabel()
        frame.add(label)
        frame.isVisible = true
    }


    fun showImage(bufferedImage: BufferedImage) {
        label.icon = ImageIcon(bufferedImage)
    }

    fun showMatches(bufferedImageA: BufferedImage, bufferedImageB: BufferedImage){

    }


}
