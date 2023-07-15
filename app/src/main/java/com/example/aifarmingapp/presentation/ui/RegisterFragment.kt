package com.example.aifarmingapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.aifarmingapp.databinding.FragmentRegisterBinding
import com.example.aifarmingapp.presentation.LoginViewModel
import com.example.aifarmingapp.presentation.RegisterViewModel
import com.example.aifarmingapp.util.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var navRegister: FragmentNavigation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        navRegister = activity as FragmentNavigation

        binding.textView.setOnClickListener {
            navRegister.navigateFrag(LoginFragment(), false)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if(pass == confirmPass) {
                    viewModel.signUp(email, pass)
                } else {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Email, password or confirmation password cannot be left blank !", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.signUpState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiState.Loading -> {
                    binding.progressBarSignUp.visibility = View.VISIBLE
                }
                is UiState.Failure -> {
                    binding.progressBarSignUp.visibility = View.GONE
                    Toast.makeText(requireContext(), state.error.toString(), Toast.LENGTH_SHORT).show()
                }

                is UiState.Success -> {
                    binding.progressBarSignUp.visibility = View.GONE
                    Toast.makeText(requireContext(), "Successfully signed up", Toast.LENGTH_SHORT).show()
                    navRegister.navigateFrag(LoginFragment(), false)
                }

                else -> {
                }
            }
        }
        return binding.root
    }

}