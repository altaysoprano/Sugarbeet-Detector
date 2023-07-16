package com.example.aifarmingapp.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.FileUtils
import android.os.Handler
import android.os.HandlerThread
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.aifarmingapp.databinding.FragmentHomeBinding
import com.example.aifarmingapp.ml.Detect
import com.example.aifarmingapp.presentation.HomeViewModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.lang.reflect.Method
import java.nio.ByteBuffer

class HomeFragment : Fragment() {

    lateinit var labels: List<String>
    var paint = Paint()
    var colors = listOf<Int>(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)
    private lateinit var binding: FragmentHomeBinding
    private lateinit var bitmap: Bitmap
    private lateinit var handler: Handler
    private lateinit var model: Detect
    private lateinit var cameraDevice: CameraDevice
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var cameraManager: CameraManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        labels = FileUtil.loadLabels(requireContext(), "labels.txt")
        model = Detect.newInstance(requireContext())
        viewModel.getPermission(requireContext(), requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101))

        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        binding.textureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                bitmap = binding.textureView.bitmap!!
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)

                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 300, 300, 3), DataType.UINT8)
                val tensorImage = TensorImage(DataType.UINT8)
                tensorImage.load(resizedBitmap);
                val byteBuffer = tensorImage.buffer
                inputFeature0.loadBuffer(byteBuffer)

                val outputs = model.process(inputFeature0)
                val locations = outputs.outputFeature0AsTensorBuffer.floatArray
                val classes = outputs.outputFeature1AsTensorBuffer.floatArray
                val scores = outputs.outputFeature2AsTensorBuffer.floatArray
                val numberOfDetections = outputs.outputFeature3AsTensorBuffer.floatArray

                var mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutable)

                val h = mutable.height
                val w = mutable.width
                paint.textSize = h/15f
                paint.strokeWidth = h/85f
                var x = 0
                scores.forEachIndexed { index, fl ->
                    x = index
                    x *= 4
                    if(fl > 0.5){
                        paint.setColor(colors.get(index))
                        paint.style = Paint.Style.STROKE
                        canvas.drawRect(RectF(locations.get(x+1)*w, locations.get(x)*h, locations.get(x+3)*w, locations.get(x+2)*h), paint)
                        paint.style = Paint.Style.FILL
                        canvas.drawText(labels.get(classes.get(index).toInt())+" "+fl.toString(), locations.get(x+1)*w, locations.get(x)*h, paint)
                    }
                }

                binding.imageView.setImageBitmap(mutable)

            }

        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            viewModel.getPermission(requireContext(), requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101))
        }
    }

    @SuppressLint("MissingPermission")
    fun openCamera() {
        cameraManager.openCamera(cameraManager.cameraIdList[0], object: CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0

                var surfaceTexture = binding.textureView.surfaceTexture
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

}