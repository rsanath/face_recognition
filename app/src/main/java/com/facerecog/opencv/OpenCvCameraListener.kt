package com.facerecog.opencv

import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat

/**
 * Abstraction over the CvCameraViewListener interface
 *
 * @author - sanath
 */
class OpenCvCameraListener : CameraBridgeViewBase.CvCameraViewListener2 {
    private val TAG = "OpenCvCameraListener"

    var onCameraViewStarted: ((Int, Int) -> Unit)? = null

    var onCameraViewStopped: (() -> Unit)? = null

    var onCameraFrame: ((CameraBridgeViewBase.CvCameraViewFrame) -> Mat)? = null

    override fun onCameraViewStarted(width: Int, height: Int) {
        if (onCameraViewStarted == null) return

        onCameraViewStarted?.invoke(width, height)
    }

    override fun onCameraViewStopped() {
        if (onCameraViewStopped == null) return

        onCameraViewStopped?.invoke()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        if (onCameraFrame == null) return inputFrame.rgba() // return the rgb frame by default

        return onCameraFrame?.invoke(inputFrame)!!
    }
}