import math.GaussianProvider
import org.junit.jupiter.api.Test


class MatrixTest {


    @Test
    fun printGaussian() {
        val matrix = GaussianProvider.getGaussianMatrix(1.0,5)
        println(matrix)
    }


}