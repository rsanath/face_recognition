package com.facerecog.opencv

import android.content.Context
import android.util.Log
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import com.facerecog.R
import org.opencv.core.Size
import org.opencv.core.Scalar


/**
 *
 * A CascadeClassifier wrapper for OpenCV
 *
 * WARN: This is incomplete at the moment
 *       The classifier is not able to read the given xml file
 *       refer to https://stackoverflow.com/questions/27344741/opencv-3-0-0-facedetect-sample-fails
 *
 * @author - sanath
 */
class OpenCvFaceDetector(private val context: Context) {
    private val TAG = "OpenCvFaceDetector"
    private var mFaceCascadeClassifier: CascadeClassifier? = null

    // TODO - get the dimension of the camera frame and fix a ratio of the frame as the face height
    private val absoluteFaceSize: Double = 100.0

    init {
        initDependencies()
    }

    fun detectFace(input: Mat): Mat {
        if (mFaceCascadeClassifier?.empty()!!) {
            val msg = "Cascade classifier is not loaded!. " +
                    "Either the classifier is corrupt or " +
                    "the file provided does not exist"
            Log.w(TAG, msg)
            return input
        }

        val grayFrame = Mat()
        val faces = MatOfRect()

        Imgproc.cvtColor(input, grayFrame, Imgproc.COLOR_BGR2GRAY)

        mFaceCascadeClassifier?.detectMultiScale(
            grayFrame, // inputFrame
            faces, // resultObj
            1.1, // scaleFactor
            2, // minNeighbors
            2, // flags
            Size(absoluteFaceSize, absoluteFaceSize),  // faceSize in the frame
            Size() // maxSize
        )

        highlightFace(input, faces)

        return input
    }

    private fun highlightFace(frame: Mat, faces: MatOfRect) {
        val facesArray = faces.toArray()

        facesArray.forEach {
            Imgproc.rectangle(
                frame,
                it.tl(),
                it.br(),
                Scalar(255.0, 0.0, 0.0),
                1
            )
        }
    }

    private fun initDependencies() {

        // Copy the resource into a temp file so OpenCV can load it
        val inputStream = context.resources.openRawResource(R.raw.lbpcascade_frontalface_improved)

        val tempDir = context.getDir("cascade", Context.MODE_PRIVATE)
        val outputFile = File(tempDir, "lbpcascade_frontalface_improved.xml")
        val outputStream = outputFile.outputStream()

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        Log.i(TAG, outputFile.absolutePath + " exists?  = " + outputFile.exists())

        mFaceCascadeClassifier = CascadeClassifier(outputFile.absolutePath)

        /*
        The cascade classifier should work by just passing the filename to its constructor
        but in this case it only loads properly when we give it the xml file via the load method
         */
        mFaceCascadeClassifier?.load(outputFile.absolutePath)
    }
}