package visualization

import steps.OrientationAssignment
import util.copy
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JComponent
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class ShowRotationCanvas(private val keypoints: OrientationAssignment.OrientedKeypoints, inputImage: BufferedImage) : JComponent() {

    private val image = inputImage.copy()

    companion object {
        private const val circleMultiplier = 2.0
    }


    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.drawImage(image, 0, 0, this)
        g.color = Color.GREEN
        for (keypoint in keypoints.orientedKeypoints) {
            val x = keypoint.interpolatedX.roundToInt()
            val y = keypoint.interpolatedY.roundToInt()
            val radius = keypoint.interpolatedScale * circleMultiplier
            val circleX = x - radius
            val circleY = y - radius
            g.drawArc(circleX.roundToInt(), circleY.roundToInt(), (2 * radius).roundToInt(), (2 * radius).roundToInt(), 0, 360)
            g.drawLine(x,y,(x+cos(keypoint.orientationTheta)*radius).roundToInt(),(y+sin(keypoint.orientationTheta) * radius).roundToInt())
        }
    }
}
