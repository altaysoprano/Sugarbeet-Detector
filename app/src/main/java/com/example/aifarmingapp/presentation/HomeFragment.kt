package com.example.aifarmingapp.presentation

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.aifarmingapp.R
import com.example.aifarmingapp.databinding.FragmentCameraBinding
import com.example.aifarmingapp.databinding.FragmentHomeBinding
import com.example.aifarmingapp.presentation.ui.CameraFragment
import com.example.aifarmingapp.presentation.ui.FragmentNavigation
import com.example.aifarmingapp.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var navRegister: FragmentNavigation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        navRegister = activity as FragmentNavigation
        setHasOptionsMenu(true)

        binding.detectButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                navRegister.navigateFrag(CameraFragment(), true)
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
            }
        }

        viewModel.sugarBeetCountState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiState.Loading -> {
                    binding.progressBarHome.visibility = View.VISIBLE
                }

                is UiState.Failure -> {
                    binding.progressBarHome.visibility = View.GONE
                    Toast.makeText(requireContext(), state.error.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

                is UiState.Success -> {
                    binding.progressBarHome.visibility = View.GONE
                    binding.tvDetectionsCount.text = state.data.toString()
                }
            }

        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {
                viewModel.signOut(activity as FragmentNavigation)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navRegister.navigateFrag(CameraFragment(), true)
            } else {
                Toast.makeText(requireContext(), "You must give camera permission.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}