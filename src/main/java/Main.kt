import matching.SiftMatcher
import sift.SiftGenerator
import util.RGBImageArrayProxy
import visualization.Visualization
import java.io.File

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("You must specify two separate image paths")
        return
    }
    val path1 = args[0]
    val path2 = args[1]

    val image1 = File(path1)
    val image2 = File(path2)

    if (!(image1.exists() && image2.exists() && image1.canRead() && image2.canRead())) {
        println("Files $path1, $path2 do not exist or program doesn't have read permission")
        return
    }

    val featuresA = SiftGenerator().generateKeypoints(path1)
    val featuresB = SiftGenerator().generateKeypoints(path2)

    val matchingResult = SiftMatcher().match(featuresA, featuresB)

    Visualization().showMatches(RGBImageArrayProxy(image1).bufferedImage, RGBImageArrayProxy(image2).bufferedImage, matchingResult)


    /* val image1descriptors = SiftGenerator().generateKeypoints("C:\\Users\\manue\\Desktop\\test.jpg")
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
     */
}
