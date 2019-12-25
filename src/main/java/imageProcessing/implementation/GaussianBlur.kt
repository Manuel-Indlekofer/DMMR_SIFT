package imageProcessing.implementation

import math.GaussianProvider

class GaussianBlur(sigma: Double, size: Int) : Convolution(GaussianProvider.getGaussianMatrix(sigma, size))
