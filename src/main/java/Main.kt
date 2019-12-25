import imageProcessing.implementation.BlackAndWhite
import imageProcessing.implementation.GaussianBlur
import imageProcessing.implementation.Subtract
import model.DifferenceOfGaussians
import util.RGBImageArrayProxy
import visualization.Visualization
import java.io.File

fun main() {

    val image = RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test.jpg"))
    val bnw = BlackAndWhite().process(image)

    DifferenceOfGaussians().calculateGaussianPyramid(bnw)
}
