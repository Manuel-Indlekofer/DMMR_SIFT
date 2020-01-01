package math

import kotlin.math.min

class Symmetrization(private val width: Int, private val height: Int) {
    fun sym(x: Int, y: Int): Pair<Int, Int> {
        val returnX = min(x % (2 * width), 2 * width - 1 - x % (2 * width))
        val returnY = min(y % (2 * height), 2 * height - 1 - y % (2 * height))
        return Pair(returnX, returnY)
    }
}