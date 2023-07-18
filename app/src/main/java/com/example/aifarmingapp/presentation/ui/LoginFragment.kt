package com.example.aifarmingapp.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.aifarmingapp.R
import com.example.aifarmingapp.databinding.FragmentLoginBinding
import com.example.aifarmingapp.presentation.HomeFragment
import com.example.aifarmingapp.presentation.LoginViewModel
import com.example.aifarmingapp.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var isUserLoggedIn: Boolean = true
    private lateinit var navRegister: FragmentNavigation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        navRegister = activity as FragmentNavigation
        isUserLoggedIn = viewModel.isUserLoggedIn

        if (isUserLoggedIn) {
            navRegister.navigateFrag(HomeFragment(), false)
        }

        binding.textView.setOnClickListener {
            navRegister.navigateFrag(RegisterFragment(), false)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.signIn(email, pass)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Email, şifre veya onay şifresi boş bırakılamaz !",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.signInState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBarSignIn.visibility = View.VISIBLE
                }

                is UiState.Failure -> {
                    binding.progressBarSignIn.visibility = View.GONE
                    Toast.makeText(requireContext(), state.error.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

                is UiState.Success -> {
                    binding.progressBarSignIn.visibility = View.GONE
                    navRegister.navigateFrag(HomeFragment(), false)
                }
            }
        }

        return binding.root

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.sign_out)
        item.isVisible = false
    }
}