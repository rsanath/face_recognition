package com.facerecog.opencv

import android.content.Context
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface

class OpenCvLoaderCallback(context: Context) : BaseLoaderCallback(context) {

    var onManagerConnectionSuccess: (() -> Unit)? = null

    var onManagerConnectionFailure: (() -> Unit)? = null

    override fun onManagerConnected(status: Int) {
        when (status) {
            LoaderCallbackInterface.SUCCESS -> {
                if (this.onManagerConnectionSuccess == null) {
                    super.onManagerConnected(status)
                } else {
                    onManagerConnectionSuccess?.invoke()
                }
            }

            LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION,
            LoaderCallbackInterface.INIT_FAILED -> {
                if (this.onManagerConnectionFailure == null) {
                    super.onManagerConnected(status)
                } else {
                    onManagerConnectionFailure?.invoke()
                }
            }
        }
    }
}