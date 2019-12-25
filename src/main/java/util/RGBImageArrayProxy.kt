package util

import java.awt.image.BufferedImage
import java.io.File
import java.lang.IllegalArgumentException
import javax.imageio.ImageIO

class RGBImageArrayProxy {

    constructor(f: File) {
        bufferedImage = ImageIO.read(f)
    }

    constructor(bufferedImageInput: BufferedImage) {
        bufferedImage = bufferedImageInput
    }

    constructor(width: Int, height: Int){
        bufferedImage = BufferedImage(width,height,BufferedImage.TYPE_INT_RGB)
    }

    val bufferedImage: BufferedImage

    val width: Int
        get() = bufferedImage.width

    val height: Int
        get() = bufferedImage.height

    operator fun get(x: Int, y: Int): IntArray {
        return bufferedImage.raster.getPixel(x, y, IntArray(3))
    }

    operator fun set(x: Int, y: Int, rgb: IntArray) {
        if (rgb.size != 3) {
            throw IllegalArgumentException()
        }
        bufferedImage.raster.setPixel(x, y, rgb)
    }

    fun forEachPixel(function: (x: Int, y: Int) -> IntArray): RGBImageArrayProxy {
        val result = RGBImageArrayProxy(bufferedImage.copy())
        for (x in 0 until width) {
            for (y in 0 until height) {
                result[x, y] = function.invoke(x, y)
            }
        }
        return result
    }
}
