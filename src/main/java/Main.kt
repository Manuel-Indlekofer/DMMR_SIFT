import matching.SiftMatcher
import sift.SiftGenerator
import util.RGBImageArrayProxy
import visualization.Visualization
import java.io.File

fun main() {

    val image1descriptors = SiftGenerator().generateKeypoints("C:\\Users\\manue\\Desktop\\test.jpg")
    val image2descriptors = SiftGenerator().generateKeypoints("C:\\Users\\manue\\Desktop\\test2.jpg")

    val keyPointImageA = RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test.jpg"))
    val keyPointImageB = RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test2.jpg"))

    for(keypoint in image1descriptors.descriptors){
        keyPointImageA[keypoint.interpolatedX.toInt(),keypoint.interpolatedY.toInt()] = intArrayOf(255,0,0)
    }

    for(keypoint in image2descriptors.descriptors){
        keyPointImageB[keypoint.interpolatedX.toInt(),keypoint.interpolatedY.toInt()] = intArrayOf(255,0,0)
    }

    Visualization().showImage(keyPointImageA.bufferedImage)
    Visualization().showImage(keyPointImageB.bufferedImage)

    val matcher = SiftMatcher()

    val result = matcher.match(image1descriptors, image2descriptors)


    val displayImageA = RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test.jpg"))
    val displayImageB = RGBImageArrayProxy(File("C:\\Users\\manue\\Desktop\\test2.jpg"))


    for (matchA in result.map { it.first }) {
        displayImageA[matchA.interpolatedX.toInt(),matchA.interpolatedY.toInt()] = intArrayOf(255,0,0)
    }

    for (matchB in result.map { it.second }) {
        displayImageB[matchB.interpolatedX.toInt(), matchB.interpolatedY.toInt()] = intArrayOf(255,0,0)
    }



    Visualization().showMatches(displayImageA.bufferedImage,displayImageB.bufferedImage,result)
}
