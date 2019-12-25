package util

import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import java.awt.image.WritableRaster


fun BufferedImage.copy() : BufferedImage{
    val cm: ColorModel = this.colorModel
    val isAlphaPremultiplied = cm.isAlphaPremultiplied
    val raster: WritableRaster = this.copyData(null)
    return BufferedImage(cm, raster, isAlphaPremultiplied, null)
}