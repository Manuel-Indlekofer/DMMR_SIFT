package visualization

import model.GaussianPyramid
import util.RGBImageArrayProxy
import java.awt.Graphics
import javax.swing.JComponent
import kotlin.math.roundToInt

class GaussianPyramidCanvas(inputgaussianPyramid: GaussianPyramid, imageMultiplicator: Double = 1.0) : JComponent() {


    private val gaussianPyramid: GaussianPyramid = GaussianPyramid(inputgaussianPyramid.numberOfOctaves, inputgaussianPyramid.numberOfScales)

    init {
        for (octave in 0 until gaussianPyramid.numberOfOctaves) {
            for (scale in 0 until gaussianPyramid.numberOfScales) {
                gaussianPyramid.setImage(octave, scale, inputgaussianPyramid.getImage(octave, scale).timesScalar(imageMultiplicator))
            }
        }
    }


    private val imageMultiplicatorY: Double
        get() {
            val largestImageHeight = gaussianPyramid.getImage(0, 0).rows
            val totalHeight = gaussianPyramid.numberOfScales * largestImageHeight
            return super.getHeight().toDouble() / totalHeight
        }

    private val imageMultiplicatorX: Double
        get() {
            var totalWidth = 0
            for (octave in 0 until gaussianPyramid.numberOfOctaves) {
                totalWidth += gaussianPyramid.getImage(octave, 0).columns
            }
            return super.getWidth().toDouble() / totalWidth
        }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        for (octave in 0 until gaussianPyramid.numberOfOctaves) {
            for (scale in 0 until gaussianPyramid.numberOfScales) {
                g.drawImage(RGBImageArrayProxy(gaussianPyramid.getImage(octave, scale)).bufferedImage, getXPosition(octave), getYPosition(octave, scale), getWidth(octave), getHeight(octave), this)
            }
        }
    }

    private fun getXPosition(octave: Int): Int {
        if (octave == 0) {
            return 0
        }
        return (gaussianPyramid.getImage(octave, 0).columns * imageMultiplicatorX * 2 + getXPosition(octave - 1)).roundToInt()
    }

    private fun getYPosition(octave: Int, scale: Int): Int {
        val octaveHeight = (gaussianPyramid.getImage(octave, scale).rows * imageMultiplicatorY).roundToInt()
        return scale * octaveHeight
    }

    private fun getHeight(octave: Int): Int {
        return (gaussianPyramid.getImage(octave, 0).rows * imageMultiplicatorY).roundToInt()
    }

    private fun getWidth(octave: Int): Int {
        return (gaussianPyramid.getImage(octave, 0).columns * imageMultiplicatorX).roundToInt()
    }
}
