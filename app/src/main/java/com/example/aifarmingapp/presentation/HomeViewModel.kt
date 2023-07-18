package com.example.aifarmingapp.presentation

import androidx.lifecycle.ViewModel
import com.example.aifarmingapp.presentation.ui.FragmentNavigation
import com.example.aifarmingapp.presentation.ui.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var navRegister: FragmentNavigation


    fun signOut(activity: FragmentNavigation) {
        navRegister = activity as FragmentNavigation
        firebaseAuth.signOut()
        navRegister.navigateFrag(LoginFragment(), false)
    }

}