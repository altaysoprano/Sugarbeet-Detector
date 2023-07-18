package com.example.aifarmingapp.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.view.Surface
import androidx.lifecycle.ViewModel
import com.example.aifarmingapp.ml.Detect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private lateinit var cameraDevice: CameraDevice
    var numberOfSugarbeets = 0
    private lateinit var cameraManager: CameraManager
    var paint = Paint()
    var colors = listOf<Int>(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED
    )

    fun onDetection(bitmap: Bitmap, model: Detect, labels: List<String>): Bitmap {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)

        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 300, 300, 3), DataType.UINT8)
        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(resizedBitmap)
        val byteBuffer = tensorImage.buffer
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val locations = outputs.outputFeature0AsTensorBuffer.floatArray
        val classes = outputs.outputFeature1AsTensorBuffer.floatArray
        val scores = outputs.outputFeature2AsTensorBuffer.floatArray
        val numberOfDetections = outputs.outputFeature3AsTensorBuffer.floatArray

        val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutable)

        val h = mutable.height
        val w = mutable.width
        val scaleFactor = 0.5f

        paint.textSize = (h / 15f * scaleFactor).coerceAtLeast(1f)
        paint.strokeWidth = h / 85f
        var x = 0
        scores.forEachIndexed { index, fl ->
            x = index
            x *= 4
            if (fl > 0.5) {
                paint.setColor(colors.get(index))
                paint.style = Paint.Style.STROKE
                val rectF = RectF(
                    locations.get(x + 1) * w,
                    locations.get(x) * h,
                    locations.get(x + 3) * w,
                    locations.get(x + 2) * h
                )
                val text = labels.get(classes.get(index).toInt()) + " " + fl.toString()
                val textWidth = paint.measureText(text)
                if (textWidth < rectF.width()) {
                    paint.textAlign = Paint.Align.CENTER
                    canvas.drawRect(rectF, paint)
                    paint.style = Paint.Style.FILL
                    canvas.drawText(
                        text,
                        rectF.centerX(),
                        rectF.top + (h / 15f * scaleFactor),
                        paint
                    )
                } else {
                    paint.textAlign = Paint.Align.LEFT
                    canvas.drawRect(rectF, paint)
                    paint.style = Paint.Style.FILL
                    canvas.drawText(
                        text,
                        rectF.left,
                        rectF.top + (h / 15f * scaleFactor),
                        paint
                    )
                }
                numberOfSugarbeets++
            }
        }

        return mutable
    }

    @SuppressLint("MissingPermission")
    fun openCamera(surfaceTexture: SurfaceTexture, context: Context, handler: Handler) {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraManager.openCamera(cameraManager.cameraIdList[0], object: CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0

                var surface = Surface(surfaceTexture)

                var captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface), object: CameraCaptureSession.StateCallback(){
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(), null, null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {

                    }
                }, handler)
            }

            override fun onDisconnected(p0: CameraDevice) {

            }

            override fun onError(p0: CameraDevice, p1: Int) {

            }
        }, handler)
    }

    fun closeCamera() {
        cameraDevice.close()
    }

}