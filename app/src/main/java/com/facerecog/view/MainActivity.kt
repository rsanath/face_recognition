package com.facerecog.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import com.facerecog.R
import com.facerecog.opencv.OpenCvCameraListener
import com.facerecog.opencv.OpenCvFaceDetector
import com.facerecog.opencv.OpenCvLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var mOpenCvCameraView: CameraBridgeViewBase
    private lateinit var mOpenCvManagerCallback: OpenCvLoaderCallback
    private lateinit var faceDetector: OpenCvFaceDetector

    private var cameraIndex: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initOpenCvManager()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun switchCamera(view: View) {
        mOpenCvCameraView.visibility = View.GONE

        val newIndex = if (cameraIndex == 0) 1 else 0
        cameraIndex = newIndex
        mOpenCvCameraView.setCameraIndex(newIndex)

        mOpenCvCameraView.visibility = View.VISIBLE
    }

    private fun onFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        val outputFrame = inputFrame.rgba().t()

        val flipCode = if (cameraIndex == 0) 1 else -1

        Core.flip(outputFrame, outputFrame, flipCode)  // flip camera vertically
        return faceDetector.detectFace(outputFrame)
    }

    private fun initOpenCvManager() {
        mOpenCvManagerCallback = OpenCvLoaderCallback(this)

        mOpenCvManagerCallback.onManagerConnectionSuccess = {
            initDependencies()
            initCvCamera()
            mOpenCvCameraView.enableView()
        }

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mOpenCvManagerCallback)
    }

    private fun initCvCamera() {
        mOpenCvCameraView = findViewById<JavaCameraView>(R.id.openCvCamera)
        mOpenCvCameraView.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView.enableView()
        mOpenCvCameraView.setCameraIndex(cameraIndex)

        val cameraListener = OpenCvCameraListener()
        cameraListener.onCameraFrame = { frame -> this.onFrame(frame) }
        mOpenCvCameraView.setCvCameraViewListener(cameraListener)
    }

    override fun onResume() {
        super.onResume()
        mOpenCvCameraView.enableView()
    }

    public override fun onPause() {
        super.onPause()
        mOpenCvCameraView.disableView()
    }

    private fun initDependencies() {
        faceDetector = OpenCvFaceDetector(this)
    }

}
