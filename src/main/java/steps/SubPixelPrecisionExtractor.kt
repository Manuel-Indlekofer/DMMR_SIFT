package steps

import math.DoubleMatrix
import model.GaussianPyramid
import org.apache.commons.math3.linear.SingularMatrixException
import java.lang.Exception
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

class SubPixelPrecisionExtractor(private val dOGPyramid: GaussianPyramid.DifferenceOfGaussiansPyramid, private val scaleSpace: GaussianScaleSpace) : Step<DiscreteExtremaExtraction.DiscreteExtrema, SubPixelPrecisionExtractor.CandidateKeypoints> {

    data class CandidateKeypoints(val candidates: ArrayList<CandidateKeypoint>) {
        data class CandidateKeypoint(val octave: Int, val scale: Int, val x: Int, val y: Int, val interpolatedScale: Double, val interpolatedX: Double, val interpolatedY: Double, val interpolatedValue: Double)
    }

    override fun process(input: DiscreteExtremaExtraction.DiscreteExtrema): CandidateKeypoints {
        val resultList = CandidateKeypoints(ArrayList())
        for (extremum in input.extrema) {
            println("[SubPixelPrecisionExtractor] processing discrete extremum of octave:${extremum.octave} scale:${extremum.scale} x:${extremum.x} y:${extremum.y}")
            val result = processExtremum(extremum)
            result?.let {
                resultList.candidates.add(it)
            }
        }
        return resultList
    }


    private fun processExtremum(extremum: DiscreteExtremaExtraction.DiscreteExtrema.Extremum): SubPixelPrecisionExtractor.CandidateKeypoints.CandidateKeypoint? {
        var iteration = 0
        var currentScale = extremum.scale
        var currentX = extremum.x
        var currentY = extremum.y
        var alpha: DoubleMatrix
        var interpolatedValue: Double
        val pixelDistance = 0.5 * 2.0.pow(extremum.octave)
        var interpolatedX: Double
        var interpolatedY: Double
        var interpolatedScale: Double
        do {
            iteration++

            try {
                alpha = calculateOffsetAlpha(DiscreteExtremaExtraction.DiscreteExtrema.Extremum(extremum.octave, currentScale, currentX, currentY))
                interpolatedValue = calculateInterpolatedValueOmega(DiscreteExtremaExtraction.DiscreteExtrema.Extremum(extremum.octave, currentScale, currentX, currentY))
            }catch (ex : SingularMatrixException){
                return null
            }


            interpolatedScale = pixelDistance / scaleSpace.minInterPixelDistance * scaleSpace.minBlurSigma * 2.0.pow((alpha[0, 0] + currentScale) / scaleSpace.numberOfScales)
            interpolatedX = pixelDistance * (alpha[1, 0] + currentX)
            interpolatedY = pixelDistance * (alpha[2, 0] + currentY)

            currentScale = (currentScale + alpha[0, 0]).roundToInt().coerceIn(1 until dOGPyramid.numberOfScales - 1)
            currentX = (currentX + alpha[1, 0]).roundToInt().coerceIn(1 until dOGPyramid.getImage(extremum.octave, currentScale).columns - 1)
            currentY = (currentY + alpha[2, 0]).roundToInt().coerceIn(1 until dOGPyramid.getImage(extremum.octave, currentScale).rows - 1)
        } while (max(alpha[0, 0], max(alpha[1, 0], alpha[2, 0])) < 0.6 && iteration < 6)
        if (max(alpha[0, 0], max(alpha[1, 0], alpha[2, 0])) < 0.6) {
            return CandidateKeypoints.CandidateKeypoint(extremum.octave, extremum.scale, extremum.x, extremum.y, interpolatedScale, interpolatedX, interpolatedY, interpolatedValue)
        } else {
            return null
        }

    }

    private fun calculateGradient(extremum: DiscreteExtremaExtraction.DiscreteExtrema.Extremum): DoubleMatrix {
        val x = extremum.x
        val y = extremum.y
        val octave = extremum.octave
        val scale = extremum.scale

        val first = (dOGPyramid.getImage(octave, scale + 1)[x, y] - dOGPyramid.getImage(octave, scale - 1)[x, y]) / 2.0
        val second = (dOGPyramid.getImage(octave, scale)[x + 1, y] - dOGPyramid.getImage(octave, scale)[x - 1, y]) / 2.0
        val third = (dOGPyramid.getImage(octave, scale)[x, y + 1] - dOGPyramid.getImage(octave, scale)[x, y - 1]) / 2.0
        val gradient = DoubleMatrix(1, 3)
        gradient[0, 0] = first
        gradient[1, 0] = second
        gradient[2, 0] = third
        return gradient
    }

    private fun calculateOffsetAlpha(extremum: DiscreteExtremaExtraction.DiscreteExtrema.Extremum): DoubleMatrix {
        val hessian = calculateHessian(extremum)
        val gradient = calculateGradient(extremum)
        return -(hessian.inverse) * gradient
    }

    private fun calculateInterpolatedValueOmega(extremum: DiscreteExtremaExtraction.DiscreteExtrema.Extremum): Double {
        val alpha = calculateOffsetAlpha(extremum)
        val gradient = calculateGradient(extremum)
        val temp = alpha.transposed * gradient
        if (temp.columns != 1 && temp.rows != 1) {
            throw Exception("Invalid matrix format!")
        }
        val result = temp[0, 0]

        return dOGPyramid.getImage(extremum.octave, extremum.scale)[extremum.x, extremum.y] + 0.5 * result
    }

    private fun calculateHessian(extremum: DiscreteExtremaExtraction.DiscreteExtrema.Extremum): DoubleMatrix {
        val x = extremum.x
        val y = extremum.y
        val octave = extremum.octave
        val scale = extremum.scale

        val h11 = dOGPyramid.getImage(octave, scale + 1)[x, y] + dOGPyramid.getImage(octave, scale - 1)[x, y] - 2.0 * dOGPyramid.getImage(octave, scale)[x, y]
        val h12 = (dOGPyramid.getImage(octave, scale + 1)[x + 1, y] - dOGPyramid.getImage(octave, scale + 1)[x - 1, y] - dOGPyramid.getImage(octave, scale - 1)[x + 1, y] + dOGPyramid.getImage(octave, scale - 1)[x - 1, y]) / 4.0
        val h22 = dOGPyramid.getImage(octave, scale)[x + 1, y] + dOGPyramid.getImage(octave, scale)[x - 1, y] - 2.0 * dOGPyramid.getImage(octave, scale)[x, y]
        val h13 = (dOGPyramid.getImage(octave, scale + 1)[x, y + 1] - dOGPyramid.getImage(octave, scale + 1)[x, y - 1] - dOGPyramid.getImage(octave, scale - 1)[x, y + 1] + dOGPyramid.getImage(octave, scale - 1)[x, y - 1]) / 4.0
        val h33 = dOGPyramid.getImage(octave, scale)[x, y + 1] + dOGPyramid.getImage(octave, scale)[x, y - 1] - 2.0 * dOGPyramid.getImage(octave, scale)[x, y]
        val h23 = (dOGPyramid.getImage(octave, scale)[x + 1, y + 1] - dOGPyramid.getImage(octave, scale)[x + 1, y - 1] - dOGPyramid.getImage(octave, scale)[x - 1, y + 1] + dOGPyramid.getImage(octave, scale)[x - 1, y - 1]) / 4.0

        val hessian = DoubleMatrix(3, 3)
        hessian[0, 0] = h11
        hessian[1, 0] = h12
        hessian[2, 0] = h13
        hessian[0, 1] = h12
        hessian[1, 1] = h22
        hessian[2, 1] = h23
        hessian[0, 2] = h13
        hessian[1, 2] = h23
        hessian[2, 2] = h33
        return hessian
    }
}