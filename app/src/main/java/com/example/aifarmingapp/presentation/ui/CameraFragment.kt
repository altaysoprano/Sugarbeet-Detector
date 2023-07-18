package com.example.aifarmingapp.presentation.ui

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.aifarmingapp.databinding.FragmentCameraBinding
import com.example.aifarmingapp.ml.Detect
import com.example.aifarmingapp.presentation.CameraViewModel
import org.tensorflow.lite.support.common.FileUtil

class CameraFragment : Fragment() {

    lateinit var labels: List<String>
    private lateinit var binding: FragmentCameraBinding
    private lateinit var bitmap: Bitmap
    private lateinit var handler: Handler
    private lateinit var model: Detect
    private val viewModel: CameraViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCameraBinding.inflate(inflater, container, false)
        labels = FileUtil.loadLabels(requireContext(), "labels.txt")
        model = Detect.newInstance(requireContext())
        setHasOptionsMenu(true)
        viewModel.getPermission(requireContext(), requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101))

        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        binding.textureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                binding.textureView.surfaceTexture?.let { viewModel.openCamera(it, requireContext(), handler) }
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                bitmap = binding.textureView.bitmap!!
                binding.imageView.setImageBitmap(viewModel.onDetection(bitmap, model, labels))

            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.closeCamera()
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
}