package com.example.aifarmingapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.aifarmingapp.R
import com.example.aifarmingapp.databinding.FragmentCameraBinding
import com.example.aifarmingapp.databinding.FragmentHomeBinding
import com.example.aifarmingapp.presentation.ui.CameraFragment
import com.example.aifarmingapp.presentation.ui.FragmentNavigation

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
            navRegister.navigateFrag(CameraFragment(), true)
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
}