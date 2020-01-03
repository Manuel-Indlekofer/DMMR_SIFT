package steps

class KeypointDescriptorConstruction : Step<OrientationAssignment.OrientedKeypoints,KeypointDescriptorConstruction.KeypointDescriptors> {

    data class KeypointDescriptors(val descriptors : ArrayList<KeypointDescriptor>){
        data class KeypointDescriptor(val octave: Int, val scale: Int, val x: Int, val y: Int, val interpolatedScale: Double, val interpolatedX: Double, val interpolatedY: Double, val interpolatedValue: Double, val orientationTheta: Double,val featureVector : ArrayList<Double>)
    }

    override fun process(input: OrientationAssignment.OrientedKeypoints): KeypointDescriptors {



        return KeypointDescriptors(arrayListOf(KeypointDescriptors.KeypointDescriptor(0,0,0,0,1.0,1.0,1.0,1.0,1.0, arrayListOf(2.0))))
    }

}